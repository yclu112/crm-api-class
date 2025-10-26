package com.crm.convert;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.crm.enums.ProductStatusEnum;

/**
 * 商品状态转换器（Excel导入导出时的数字与中文描述互转）
 * 适配场景：商品状态（0-初始化，1-上架，2-下架）
 */
public class ProductStatusConverter implements Converter<Integer> {

    /**
     * 支持的Java类型（这里是商品状态的数字类型：Byte）
     */
    @Override
    public Class<?> supportJavaTypeKey() {
        return Byte.class;
    }

    /**
     * 支持的Excel单元格数据类型（这里用字符串展示中文描述）
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * Excel导入时：将单元格的中文描述（如"上架"）转为Java中的数字编码（如1）
     */
    @Override
    public Integer convertToJavaData(ReadConverterContext<?> context) {
        // 获取Excel单元格中的字符串值（如"初始化"）
        String statusName = context.getReadCellData().getStringValue();
        // 调用枚举的转换方法，返回对应的数字编码（如0）
        return ProductStatusEnum.getValueByName(statusName);
    }

    /**
     * Excel导出时：将Java中的数字编码（如2）转为Excel单元格的中文描述（如"下架"）
     */
//    @Override
//    public WriteCellData<?> convertToExcelData(WriteConverterContext<Byte> context) {
//        // 获取Java对象中的状态数字（如2）
//        Byte statusValue = context.getValue();
//        // 调用枚举的转换方法，返回对应的中文描述（如"下架"）
//        String statusName = ProductStatusEnum.getNameByValue(statusValue);
//        return new WriteCellData<>(statusName);
//    }
}