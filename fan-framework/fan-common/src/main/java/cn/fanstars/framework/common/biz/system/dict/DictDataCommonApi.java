package cn.fanstars.framework.common.biz.system.dict;

import cn.fanstars.framework.common.biz.system.dict.dto.DictDataRespDTO;
import cn.fanstars.framework.common.biz.system.dict.dto.DictTypeSaveReqDTO;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.List;

/**
 * 字典数据 API 接口
 *
 * @author 繁星源码
 */
@Validated
public interface DictDataCommonApi {

    /**
     * 获得指定字典类型的字典数据列表
     *
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    List<DictDataRespDTO> getDictDataList(String dictType);

    /**
     * 同步字典数据
     *
     * @param dictTypeSaveReqDTO 字典类型
     */
    void syncDictData(@Valid DictTypeSaveReqDTO dictTypeSaveReqDTO);

}
