package cn.fanstars.module.system.service.dict;

import cn.hutool.core.util.StrUtil;
import cn.fanstars.framework.common.enums.CommonStatusEnum;
import cn.fanstars.framework.common.exception.ServiceException;
import cn.fanstars.framework.common.pojo.PageResult;
import cn.fanstars.framework.common.util.date.LocalDateTimeUtils;
import cn.fanstars.framework.common.util.object.BeanUtils;
import cn.fanstars.module.system.controller.admin.dict.vo.data.DictDataSaveReqVO;
import cn.fanstars.module.system.controller.admin.dict.vo.type.DictTypePageReqVO;
import cn.fanstars.module.system.controller.admin.dict.vo.type.DictTypeSaveReqVO;
import cn.fanstars.module.system.dal.dataobject.dict.DictTypeDO;
import cn.fanstars.module.system.dal.mysql.dict.DictTypeMapper;
import com.google.common.annotations.VisibleForTesting;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    private void validateAndSaveDictData(DictTypeSaveReqVO createReqVO) {
        String dataValues;
        if (StrUtil.isEmpty(dataValues = createReqVO.getDataValues())) {
            return;
        }
        String[] dataValue = dataValues.split("\n");
        List<DictDataSaveReqVO> dictDataSaveReqVOS = new ArrayList<>(dataValue.length);
        int sort = 1;
        for (String item : dataValue) {
            if (StrUtil.isEmpty(item.trim())) {
                continue;
            }
            String[] keyValue = item.split(":");
            if (keyValue.length != 2) {
                throw new ServiceException(DICT_TYPE_CHILDREN_ERROR);
            }
            DictDataSaveReqVO dictDataDO = new DictDataSaveReqVO();
            dictDataDO.setSort(sort++);
            dictDataDO.setDictType(createReqVO.getType());
            dictDataDO.setLabel(keyValue[1].trim());
            dictDataDO.setValue(keyValue[0].trim());
            dictDataDO.setDictType(createReqVO.getType());
            dictDataDO.setStatus(CommonStatusEnum.ENABLE.getStatus());
            dictDataSaveReqVOS.add(dictDataDO);
        }
        for (DictDataSaveReqVO dictDataSaveReqVO : dictDataSaveReqVOS) {
            dictDataService.createDictData(dictDataSaveReqVO);
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

}
