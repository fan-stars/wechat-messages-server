package cn.fanstars.module.system.service.dict;

import cn.hutool.core.util.StrUtil;
import cn.fanstars.framework.common.biz.system.dict.dto.DictTypeSaveReqDTO;
import cn.fanstars.framework.common.enums.CommonStatusEnum;
import cn.fanstars.framework.common.exception.ServiceException;
import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.framework.common.util.date.LocalDateTimeUtils;
import cn.fanstars.framework.common.util.object.BeanUtils;
import cn.fanstars.module.system.controller.admin.dict.vo.data.DictDataSaveReqVO;
import cn.fanstars.module.system.controller.admin.dict.vo.type.DictTypePageReqVO;
import cn.fanstars.module.system.controller.admin.dict.vo.type.DictTypeSaveReqVO;
import cn.fanstars.module.system.dal.dataobject.dict.DictDataDO;
import cn.fanstars.module.system.dal.dataobject.dict.DictTypeDO;
import cn.fanstars.module.system.dal.mysql.dict.DictTypeMapper;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.fanstars.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fanstars.module.system.enums.ErrorCodeConstants.*;

/**
 * 字典类型 Service 实现类
 *
 * @author 繁星源码
 */
@Service
public class DictTypeServiceImpl implements DictTypeService {

    @Resource
    private DictDataService dictDataService;

    @Resource
    private DictTypeMapper dictTypeMapper;

    @Override
    public PageResult<DictTypeDO> getDictTypePage(DictTypePageReqVO pageReqVO) {
        return dictTypeMapper.selectPage(pageReqVO);
    }

    @Override
    public DictTypeDO getDictType(Long id) {
        return dictTypeMapper.selectById(id);
    }

    @Override
    public DictTypeDO getDictType(String type) {
        return dictTypeMapper.selectByType(type);
    }

    @Override
    @Transactional
    public Long createDictType(DictTypeSaveReqVO createReqVO) {
        // 校验字典类型的名字的唯一性
        validateDictTypeNameUnique(null, createReqVO.getName());
        // 校验字典类型的类型的唯一性
        validateDictTypeUnique(null, createReqVO.getType());

        // 插入字典类型
        DictTypeDO dictType = BeanUtils.toBean(createReqVO, DictTypeDO.class);
        dictType.setDeletedTime(LocalDateTimeUtils.EMPTY); // 唯一索引，避免 null 值
        dictTypeMapper.insert(dictType);

        validateAndSaveDictData(createReqVO);
        return dictType.getId();
    }

    /**
     * 验证并保存字典数据
     * <p>
     * 该方法首先检查传入的字典数据值是否为空，然后将数据值按行分割处理
     * 对于每一行数据，方法会检查其格式是否正确（应包含键值对），并根据键值对构建字典数据对象
     * 如果键值已存在于数据库中，则更新其状态为启用，否则创建新的字典数据对象
     * 最后，对于所有未被更新的字典数据，将其状态设置为禁用，并一起保存到数据库中
     *
     * @param createReqVO 包含字典类型和数据值的请求对象
     */
    private void validateAndSaveDictData(DictTypeSaveReqVO createReqVO) {
        // 获取字典数据值，如果为空则直接返回
        String dataValues;
        if (StrUtil.isEmpty(dataValues = createReqVO.getDataValues())) {
            return;
        }

        // 将字典数据值按行分割成数组
        String[] dataValue = dataValues.split("\n");
        // 初始化用于保存字典数据的列表
        List<DictDataSaveReqVO> dictDataSaveReqVOS = new ArrayList<>(dataValue.length);
        // 初始化排序号
        int sort = 1;

        // 获取指定字典类型的字典数据列表，并转换为字典数据对象的Map
        List<DictDataDO> dictDataListByDictType = dictDataService.getDictDataListByDictType(createReqVO.getType());
        Map<String, DictDataDO> dictDataDOMap = dictDataListByDictType.stream()
                .collect(Collectors.toMap(DictDataDO::getValue, v -> v));

        // 遍历每一行字典数据值
        for (String item : dataValue) {
            // 去除前后空格后，如果行数据为空，则跳过
            if (StrUtil.isEmpty(item.trim())) {
                continue;
            }

            // 将行数据分割成键值对
            String[] keyValue = item.split(":");
            // 如果键值对格式不正确，抛出异常
            if (keyValue.length != 2) {
                throw new ServiceException(DICT_TYPE_CHILDREN_ERROR);
            }

            // 提取键值对中的值和标签
            String value = keyValue[0].trim();
            DictDataSaveReqVO dictDataSaveReqVO = new DictDataSaveReqVO();

            // 如果值已存在于数据库中，更新字典数据对象的状态
            DictDataDO dictDataDO = dictDataDOMap.get(value);
            if (dictDataDO != null && dictDataDO.getId() != null) {
                dictDataSaveReqVO.setId(dictDataDO.getId());
                dictDataSaveReqVO.setColorType(dictDataDO.getColorType());
                dictDataSaveReqVO.setCssClass(dictDataDO.getCssClass());
                dictDataDOMap.remove(value);
            }

            // 设置字典数据对象的排序号、标签、值、字典类型和状态
            dictDataSaveReqVO.setSort(sort++);
            dictDataSaveReqVO.setLabel(keyValue[1].trim());
            dictDataSaveReqVO.setValue(value);
            dictDataSaveReqVO.setDictType(createReqVO.getType());
            dictDataSaveReqVO.setStatus(CommonStatusEnum.ENABLE.getStatus());

            // 将字典数据对象添加到列表中
            dictDataSaveReqVOS.add(dictDataSaveReqVO);
        }

        // 对于所有未被更新的字典数据，将其状态设置为禁用，并添加到列表中
        for (Map.Entry<String, DictDataDO> entry : dictDataDOMap.entrySet()) {
            DictDataSaveReqVO dictDataSaveReqVO = new DictDataSaveReqVO();
            BeanUtils.copyProperties(entry.getValue(), dictDataSaveReqVO);
            dictDataSaveReqVO.setStatus(CommonStatusEnum.DISABLE.getStatus());
            dictDataSaveReqVO.setSort(0);
            dictDataSaveReqVOS.add(dictDataSaveReqVO);
        }

        // 遍历所有字典数据对象，根据其ID决定是更新还是创建
        for (DictDataSaveReqVO dictDataSaveReqVO : dictDataSaveReqVOS) {
            if (dictDataSaveReqVO.getId() != null) {
                dictDataService.updateDictData(dictDataSaveReqVO);
            } else {
                dictDataService.createDictData(dictDataSaveReqVO);
            }
        }
    }

    @Override
    public void updateDictType(DictTypeSaveReqVO updateReqVO) {
        // 校验自己存在
        validateDictTypeExists(updateReqVO.getId());
        // 校验字典类型的名字的唯一性
        validateDictTypeNameUnique(updateReqVO.getId(), updateReqVO.getName());
        // 校验字典类型的类型的唯一性
        validateDictTypeUnique(updateReqVO.getId(), updateReqVO.getType());

        // 更新字典类型
        DictTypeDO updateObj = BeanUtils.toBean(updateReqVO, DictTypeDO.class);
        dictTypeMapper.updateById(updateObj);
    }

    @Override
    public void deleteDictType(Long id) {
        // 校验是否存在
        DictTypeDO dictType = validateDictTypeExists(id);
        // 校验是否有字典数据
        if (dictDataService.getDictDataCountByDictType(dictType.getType()) > 0) {
            throw exception(DICT_TYPE_HAS_CHILDREN);
        }
        // 删除字典类型
        dictTypeMapper.updateToDelete(id, LocalDateTime.now());
    }

    @Override
    public void deleteDictTypeList(List<Long> ids) {
        // 1. 校验是否有字典数据
        List<DictTypeDO> dictTypes = dictTypeMapper.selectByIds(ids);
        dictTypes.forEach(dictType -> {
            if (dictDataService.getDictDataCountByDictType(dictType.getType()) > 0) {
                throw exception(DICT_TYPE_HAS_CHILDREN);
            }
        });

        // 2. 批量删除字典类型
        LocalDateTime now = LocalDateTime.now();
        ids.forEach(id -> dictTypeMapper.updateToDelete(id, now));
    }

    @Override
    public List<DictTypeDO> getDictTypeList() {
        return dictTypeMapper.selectList();
    }

    @Override
    @Transactional
    public void syncDictTypeData(DictTypeSaveReqDTO dictTypeSaveReqDTO) {
        DictTypeDO dictType = getDictType(dictTypeSaveReqDTO.getType());
        DictTypeSaveReqVO dictTypeSaveReqVO = BeanUtils.toBean(dictTypeSaveReqDTO, DictTypeSaveReqVO.class);
        if (dictType == null) {
            getSelf().createDictType(dictTypeSaveReqVO);
        } else {
            dictTypeSaveReqVO.setId(dictType.getId());
            getSelf().updateDictType(dictTypeSaveReqVO);
            validateAndSaveDictData(dictTypeSaveReqVO);
        }
    }

    @VisibleForTesting
    void validateDictTypeNameUnique(Long id, String name) {
        DictTypeDO dictType = dictTypeMapper.selectByName(name);
        if (dictType == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的字典类型
        if (id == null) {
            throw exception(DICT_TYPE_NAME_DUPLICATE);
        }
        if (!dictType.getId().equals(id)) {
            throw exception(DICT_TYPE_NAME_DUPLICATE);
        }
    }

    @VisibleForTesting
    void validateDictTypeUnique(Long id, String type) {
        if (StrUtil.isEmpty(type)) {
            return;
        }
        DictTypeDO dictType = dictTypeMapper.selectByType(type);
        if (dictType == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的字典类型
        if (id == null) {
            throw exception(DICT_TYPE_TYPE_DUPLICATE);
        }
        if (!dictType.getId().equals(id)) {
            throw exception(DICT_TYPE_TYPE_DUPLICATE);
        }
    }

    @VisibleForTesting
    DictTypeDO validateDictTypeExists(Long id) {
        if (id == null) {
            return null;
        }
        DictTypeDO dictType = dictTypeMapper.selectById(id);
        if (dictType == null) {
            throw exception(DICT_TYPE_NOT_EXISTS);
        }
        return dictType;
    }

    /**
     * 获得自身的代理对象，解决 AOP 生效问题
     *
     * @return 自己
     */
    private DictTypeServiceImpl getSelf() {
        return SpringUtil.getBean(getClass());
    }

}
