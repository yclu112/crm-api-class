package com.crm.controller;

import com.crm.common.aop.Log;
import com.crm.common.exception.ServerException;
import com.crm.common.result.PageResult;
import com.crm.common.result.Result;
import com.crm.entity.Contract;
import com.crm.enums.BusinessType;
import com.crm.query.ApprovalQuery;
import com.crm.query.ContractQuery;
import com.crm.query.ContractTrendQuery;
import com.crm.query.IdQuery;
import com.crm.service.ContractService;
import com.crm.service.PaymentService;
import com.crm.vo.ContractVO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@Slf4j
@Tag(name = "合同管理")
@RestController
@RequestMapping("/contract")
@AllArgsConstructor
public class ContractController {
    private final ContractService contractService;
    private final PaymentService paymentService;

    @PostMapping("/page")
    @Operation(summary = "合同列表-分页")
    @Log(title = "合同列表-分页查询", businessType = BusinessType.SELECT)
    public Result<PageResult<ContractVO>> getPage(@RequestBody @Validated ContractQuery contractQuery) {
        return Result.ok(contractService.getPage(contractQuery));
    }

    @PostMapping("/saveOrUpdate")
    @Operation(summary = "新增/修改合同信息")
    public Result saveOrUpdate(@RequestBody @Validated ContractVO contractVO) {
        contractService.saveOrUpdate(contractVO);
        return Result.ok();
    }

    @PostMapping("getContractTrendData")
    @Operation(summary = "合同数量趋势统计", description = "按时间维度（日/周/月）统计合同数量趋势")
    public Result<Map<String, List>> getContractTrendData(@RequestBody ContractTrendQuery query) {
        // 调用合同服务层获取趋势数据，返回格式与客户接口一致
        return Result.ok(contractService.getContractTrendData(query));
    }

    @PostMapping("startApproval")
    @Operation(summary = "启动合同审批")
    @Log(title = "启动合同审批", businessType = BusinessType.INSERT_OR_UPDATE)
    public Result startApproval(@RequestBody @Validated IdQuery idQuery) {
        contractService.startApproval(idQuery);
        return Result.ok();
    }

    @PostMapping("approvalContract")
    @Operation(summary = "合同审批")
    @Log(title = "合同审批", businessType = BusinessType.INSERT_OR_UPDATE)
    public Result approvalContract(@RequestBody @Validated ApprovalQuery query) {
        contractService.approvalContract(query);
        return Result.ok();
    }
    @GetMapping("/{id}")
    @Operation(summary = "根据ID查询合同详情")
    public Result<Contract> getById(@PathVariable Integer id) {
        Contract contract = contractService.getById(id);
        return Result.ok(contract);
    }
//    @PostMapping("/contract/update-received-amount")
//    public Result<?> updateReceivedAmount(@RequestBody ContractAmountUpdateRequest request) {
//        Contract contract = contractService.getById(request.getContractId());
//        if (contract == null) {
//            return Result.error("合同不存在");
//        }
//
//        // 使用 BigDecimal 进行加法运算
//        BigDecimal newReceivedAmount = contract.getReceivedAmount().add(new BigDecimal(request.getAddAmount()));
//        contract.setReceivedAmount(newReceivedAmount);
//
//        contractService.updateById(contract);
//        return Result.ok("更新成功");
//    }

}
