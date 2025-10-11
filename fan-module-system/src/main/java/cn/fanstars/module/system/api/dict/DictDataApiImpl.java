package cn.fanstars.module.system.api.dict;

import cn.fanstars.framework.common.biz.system.dict.dto.DictTypeSaveReqDTO;
import cn.fanstars.framework.common.util.object.BeanUtils;
import cn.fanstars.framework.common.biz.system.dict.dto.DictDataRespDTO;
import cn.fanstars.module.system.dal.dataobject.dict.DictDataDO;
import cn.fanstars.module.system.service.dict.DictDataService;
import cn.fanstars.module.system.service.dict.DictTypeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * 字典数据 API 实现类
 *
 * @author 繁星源码
 */
@Service
public class DictDataApiImpl implements DictDataApi {

    @Resource
    private DictTypeService dictTypeService;

    @Resource
    private DictDataService dictDataService;

    @Override
    public void validateDictDataList(String dictType, Collection<String> values) {
        dictDataService.validateDictDataList(dictType, values);
    }

    @Override
    public List<DictDataRespDTO> getDictDataList(String dictType) {
        List<DictDataDO> list = dictDataService.getDictDataListByDictType(dictType);
        return BeanUtils.toBean(list, DictDataRespDTO.class);
    }

    @Override
    public void syncDictData(DictTypeSaveReqDTO dictTypeSaveReqDTO) {
        dictTypeService.syncDictTypeData(dictTypeSaveReqDTO);
    }

}
