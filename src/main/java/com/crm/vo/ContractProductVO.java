package com.crm.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 合同商品关联VO（用于前后端数据传输）
 */
@Data
@ApiModel(value = "ContractProductVO对象", description = "合同商品关联数据传输模型")
public class ContractProductVO {

    @ApiModelProperty("主键自增（编辑时回显）")
    private Integer id;

    @ApiModelProperty("商品id（前端选择商品后赋值）")
    private Integer pId;

    @ApiModelProperty("合同id（后端自动赋值，前端无需传递）")
    private Integer cId;

    @ApiModelProperty("商品名称（前端显示/选择后回显）")
    private String pName;

    @ApiModelProperty("商品价格（选择商品后自动带入）")
    private BigDecimal price;

    @ApiModelProperty("购买商品数量（前端输入）")
    private Integer count;

    @ApiModelProperty("总价格（前端计算/后端校验）")
    private BigDecimal totalPrice;
}