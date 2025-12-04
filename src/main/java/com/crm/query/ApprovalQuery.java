package com.crm.query;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description:
 * @author: ycshang
 * @create: 2025-11-16 09:28
 **/
@Data
public class ApprovalQuery {
    @NotNull(message = "审核id不能为空")
    private Integer id;
    @NotNull(message = "审核状态不能为空")
    private Integer type;
    @NotBlank(message = "审核内容不能为空")
    private String comment; // 新增：审核原因/备注

    private BigDecimal actualAmount;

    // 以下字段用于特定场景，如回款审核
    private Integer paymentMethod; // 支付方式
    private String paymentTime;    // 支付时间
}
