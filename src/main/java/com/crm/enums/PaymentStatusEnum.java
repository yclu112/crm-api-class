package com.crm.enums;


import lombok.Getter;

@Getter
public enum PaymentStatusEnum {
    PENDING_AUDIT(1, "待审核"),
    APPROVED(2, "已通过"),
    REJECTED(3, "已拒绝");

    private final int code;
    private final String message;

    PaymentStatusEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    // 根据code获取枚举
    public static PaymentStatusEnum getByCode(int code) {
        for (PaymentStatusEnum statusEnum : PaymentStatusEnum.values()) {
            if (statusEnum.getCode() == code) {
                return statusEnum;
            }
        }
        return null;
    }
}