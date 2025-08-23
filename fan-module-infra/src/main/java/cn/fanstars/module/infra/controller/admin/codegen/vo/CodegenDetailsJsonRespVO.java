package cn.fanstars.module.infra.controller.admin.codegen.vo;

import lombok.Data;

@Data
public class CodegenDetailsJsonRespVO {

    private String key;
    private String label;
    private String value;
    private String type;
    private String dictTagType;
    private Integer row;
    private Integer rowspan;

}
