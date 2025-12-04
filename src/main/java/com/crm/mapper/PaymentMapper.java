package com.crm.mapper;

import com.crm.entity.Payment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.query.PaymentQuery;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;


/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
public interface PaymentMapper extends BaseMapper<Payment> {
    /**
     * 回款审核（更新状态）
     * @param paramMap 审核参数（id、type）
     * @return 影响行数
     */
    int approvalPayment(@Param("map") Map<String, Object> paramMap);

    /**
     * 查询合同下所有审核通过的回款金额总和
     * @param contractId 合同ID
     * @return 金额总和
     */
    BigDecimal selectApprovedPaymentSum(@Param("contractId") Integer contractId);
}
