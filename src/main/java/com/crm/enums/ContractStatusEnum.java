package com.crm.enums;

public enum ContractStatusEnum {
    DRAFT(0, "草稿"),
    PENDING_APPROVAL(1, "待审核"),
    APPROVED(2, "审核通过"),
    REJECTED(3, "审核拒绝");

    private final Integer code;
    private final String desc;

    ContractStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // getter方法
    public Integer getCode() { return code; }
    public String getDesc() { return desc; }
}