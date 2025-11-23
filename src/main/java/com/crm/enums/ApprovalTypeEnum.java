package com.crm.enums;

public enum ApprovalTypeEnum {
    PASS(0, "通过"),
    REJECT(1, "拒绝");

    private final Integer code;
    private final String desc;

    ApprovalTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // getter方法
    public Integer getCode() { return code; }
    public String getDesc() { return desc; }
}
