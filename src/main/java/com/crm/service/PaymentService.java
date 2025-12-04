package com.crm.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.crm.common.result.PageResult;
import com.crm.entity.Payment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.entity.Product;
import com.crm.query.ApprovalQuery;
import com.crm.query.IdQuery;
import com.crm.query.PaymentQuery;

import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
public interface PaymentService extends IService<Payment> {

    PageResult<Payment> getPage(PaymentQuery query);
//    void createPayment(Payment payment);
    void approvalPayment(ApprovalQuery query);

}
