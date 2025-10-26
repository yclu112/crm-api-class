package com.crm.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.baomidou.mybatisplus.annotation.*;
import com.crm.convert.ProductStatusConverter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ExcelIgnoreUnannotated
@ContentRowHeight(105)
@ColumnWidth(24)
public class ProductVO {
    @Schema(description ="主键")
    private Integer id;

    @Schema(description ="商品名称")
    @NotNull(message = "商品名称不能为空")
    @ExcelProperty(value = "商品名称", converter = ProductStatusConverter.class)
    private String name;

    @Schema(description ="价格")
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    @ExcelProperty(value = "价格", converter = ProductStatusConverter.class)
    private BigDecimal price;

    @Schema(description ="销量")
    @NotNull(message = "销量不能为空")
    @PositiveOrZero(message = "销量不能为负数")
    @ExcelProperty(value = "销量", converter = ProductStatusConverter.class)
    private Integer sales;

    @Schema(description = "库存数量")
    @NotNull(message = "库存数量不能为空")
    @PositiveOrZero(message = "库存不能为负数")
    @ExcelProperty(value = "库存数量")
    private Integer stock;

    @Schema(description ="商品状态 0-初始化，1-上架，2-下架")
    @NotNull(message = "商品状态不能为空")
    @ExcelProperty(value = "商品状态", converter = ProductStatusConverter.class)
    private Byte status;

    @Schema(description ="封面图")
    @ExcelIgnore
    private String coverImage;

    @Schema(description ="商品简介")
    @ExcelProperty(value = "商品简介")
    private String description;

    @Schema(description ="创建时间")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @ExcelProperty(value = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    @ExcelProperty(value = "更新时间")
    private LocalDateTime updateTime;
}
