package com.crm.enums;

import lombok.Getter;

@Getter
public enum PaymentMethodEnum {
    // 数字代码和对应的文字描述
    BANK_TRANSFER(1, "银行转账"),
    ALIPAY(2, "支付宝"),
    WECHAT_PAY(3, "微信支付"),
    CASH(4, "现金"),
    OTHER(5, "其他");

    // 数据库中存储的数字
    private final int code;
    // 前端显示的文字
    private final String desc;

    PaymentMethodEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    // 根据数字代码获取对应的枚举对象
    public static PaymentMethodEnum getByCode(int code) {
        for (PaymentMethodEnum method : PaymentMethodEnum.values()) {
            if (method.getCode() == code) {
                return method;
            }
        }
        return null; // 或者抛出异常
    }
}