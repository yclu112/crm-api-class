package com.crm.query;

import com.crm.common.model.Query;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ContractQuery extends Query {

    @Schema(description = "合同名称")
    private String name;

    @Schema(description = "客户id")
    private Integer customerId;

    @Schema(description = "合同编号")
    private String number;

    @Schema(description = "合同状态 0-合同初始化 1-审核中 2-审核通过 3-审核不通过")
    private Integer status;

    // 1. 时间范围查询
    @Schema(description = "创建时间开始")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间结束")
    private LocalDateTime createTimeEnd;

    @Schema(description = "签署时间开始")
    private LocalDateTime signTimeStart;

    @Schema(description = "签署时间结束")
    private LocalDateTime signTimeEnd;

    // 2. 金额范围查询
    @Schema(description = "合同金额最小值")
    private BigDecimal amountMin;

    @Schema(description = "合同金额最大值")
    private BigDecimal amountMax;

    @Schema(description = "已收金额最小值")
    private BigDecimal receivedAmountMin;

    @Schema(description = "已收金额最大值")
    private BigDecimal receivedAmountMax;

    // 3. 负责人/创建人查询
    @Schema(description = "创建人ID")
    private Integer createrId;

    @Schema(description = "负责人ID")
    private Integer ownerId;

    // 4. 模糊查询扩展
    @Schema(description = "客户名称模糊查询")
    private String customerName;

    @Schema(description = "合同备注模糊查询")
    private String remark;

    // 5. 排序字段
    @Schema(description = "排序字段")
    private String orderBy;

    @Schema(description = "排序方向（asc/desc）")
    private String orderDirection;

    // 6. 合同起止时间范围
    @Schema(description = "合同开始时间开始")
    private LocalDateTime startTimeStart;

    @Schema(description = "合同开始时间结束")
    private LocalDateTime startTimeEnd;

    @Schema(description = "合同结束时间开始")
    private LocalDateTime endTimeStart;

    @Schema(description = "合同结束时间结束")
    private LocalDateTime endTimeEnd;

    // 7. 商机 ID 查询
    @Schema(description = "商机ID")
    private Integer opportunityId;
}