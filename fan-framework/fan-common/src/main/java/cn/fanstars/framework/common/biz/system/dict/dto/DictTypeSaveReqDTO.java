package cn.fanstars.framework.common.biz.system.dict.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class DictTypeSaveReqDTO {

    /**
     * 字典类型编号
     */
    private Long id;

    /**
     * 字典名称
     */
    @NotBlank(message = "字典名称不能为空")
    @Size(max = 100, message = "字典类型名称长度不能超过100个字符")
    private String name;

    /**
     * 字典类型
     */
    @NotNull(message = "字典类型不能为空")
    @Size(max = 100, message = "字典类型类型长度不能超过 100 个字符")
    private String type;

    /**
     * 状态，参见 CommonStatusEnum 枚举类
     */
    @NotNull(message = "状态不能为空")
    private Integer status;

    /**
     * 数据值 key: value 冒号间隔, 一行一个
     */
    private String dataValues;

    /**
     * 备注
     */
    private String remark;

}
