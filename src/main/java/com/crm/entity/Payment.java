package com.crm.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@TableName("t_payment")
@ApiModel(value = "Payment对象", description = "")
public class Payment {

    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("合同id")
    @TableField("contract_id")
    private Integer contractId;

    @ApiModelProperty("客户id")
    @TableField("customer_id")
    private Integer customerId;

    @ApiModelProperty("合同编号")
    @TableField("contract_number")
    private String contractNumber;

    @ApiModelProperty("合同名称")
    @TableField("contract_name")
    private String contractName;

    @ApiModelProperty("回款编号")
    @TableField("number")
    private Integer number;

    @ApiModelProperty("创建人id")
    @TableField("creater_id")
    private Integer createrId;

    @ApiModelProperty("审核状态")
    @TableField("status")
    private Integer status;

    @ApiModelProperty("支付方式")
    @TableField("payment_method")
    private Integer paymentMethod;

    @ApiModelProperty("支付时间")
    @TableField("payment_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime paymentTime;

    // 新增：回款金额字段（根据实际数据库类型选择，通常是BigDecimal）
    @ApiModelProperty("回款金额")
    @TableField("amount")
    private BigDecimal amount;

    @ApiModelProperty("逻辑删除 0-未删除，1-已删除")
    @TableField(value = "delete_flag", fill = FieldFill.INSERT)
    @TableLogic
    private Integer deleteFlag;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @ApiModelProperty("更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}