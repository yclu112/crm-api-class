package com.crm.controller;

import com.crm.common.result.PageResult;
import com.crm.common.result.Result;
import com.crm.entity.Payment;
import com.crm.query.ApprovalQuery;
import com.crm.query.IdQuery;
import com.crm.query.PaymentQuery;
import com.crm.service.PaymentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@RestController
@RequestMapping("/crm/payment")
@AllArgsConstructor
@Api(tags = "回款单管理")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/page")
    public Result<PageResult<Payment>> getPage(@RequestBody PaymentQuery query) {
        return Result.ok(paymentService.getPage(query));
    }

//    @PostMapping("/create")
//    public Result<?> createPayment(@RequestBody Payment payment) {
//        paymentService.createPayment(payment);
//        return Result.ok("回款单创建成功");
//    }

//    @PostMapping("/approvalPayment")
//    public Result<?> approvalPayment(@RequestBody ApprovalQuery query) {
//        paymentService.approvalPayment(query);
//        return Result.ok("审核操作成功");
//    }
    @PostMapping("/approvalPayment")
    public Result<?> approvalPayment(@RequestBody ApprovalQuery query) {
        try {
            paymentService.approvalPayment(query);
            return Result.ok("审核操作成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

}