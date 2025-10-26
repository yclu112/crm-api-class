package com.crm.query;

import com.crm.common.model.Query;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ProductQuery extends Query {
    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("商品状态")
    private Integer status;
}