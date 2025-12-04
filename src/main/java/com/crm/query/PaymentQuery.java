package com.crm.query;

import com.crm.common.model.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@ApiModel("回款单分页查询参数")
public class PaymentQuery extends Query {
        @ApiModelProperty("回款单状态")
        private Integer status;

        @ApiModelProperty("合同ID")
        private Integer contractId; // 建议使用 Integer，与实体类保持一致

        @ApiModelProperty("客户ID")
        private Integer customerId;

        @ApiModelProperty("合同编号")
        private String contractNumber;

        @ApiModelProperty("支付方式")
        private Integer paymentMethod;

        @ApiModelProperty("创建人ID")
        private Integer createrId; // 建议使用 Integer，与实体类保持一致

        @ApiModelProperty("创建时间开始")
        private LocalDateTime createTimeStart;

        @ApiModelProperty("创建时间结束")
        private LocalDateTime createTimeEnd;
}