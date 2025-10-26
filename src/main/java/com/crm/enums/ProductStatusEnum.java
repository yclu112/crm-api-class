package com.crm.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 商品状态枚举（整数类型状态值）
 */
@Getter
@AllArgsConstructor
public enum ProductStatusEnum {

    INIT_PRODUCT(0, "初始化"),  // 状态值为整数0
    ON_SHELF(1, "上架"),         // 状态值为整数1
    OFF_SHELF(2, "下架");        // 状态值为整数2

    private final Integer value;  // 改为Integer类型（整数）
    private final String name;    // 状态中文描述

    /**
     * 根据中文描述获取整数编码（用于Excel导入）
     */
    public static Integer getValueByName(String name) {
        for (ProductStatusEnum status : values()) {
            if (status.name.equals(name)) {
                return status.value;
            }
        }
        throw new IllegalArgumentException("无效的商品状态名称：" + name);
    }

    /**
     * 根据整数编码获取中文描述（用于Excel导出）
     */
    public static String getNameByValue(Integer value) {
        if (value == null) {
            return ""; // 空值处理
        }
        for (ProductStatusEnum status : values()) {
            if (status.value.equals(value)) { // Integer类型用equals比较
                return status.name;
            }
        }
        return "未知状态"; // 异常值处理
    }
}