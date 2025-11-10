package com.crm.controller;

import com.crm.common.aop.Log;
import com.crm.common.result.PageResult;
import com.crm.common.result.Result;
import com.crm.enums.BusinessType;
import com.crm.query.ContractQuery;
import com.crm.query.ContractTrendQuery;
import com.crm.service.ContractService;
import com.crm.vo.ContractVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@Tag(name = "合同管理")
@RestController
@RequestMapping("contract")
@AllArgsConstructor
public class ContractController {
    private final ContractService contractService;


    @PostMapping("page")
    @Operation(summary = "合同列表-分页")
    @Log(title = "合同列表-分页", businessType = BusinessType.SELECT)
    public Result<PageResult<ContractVO>> getPage(@RequestBody @Validated ContractQuery contractQuery) {
        return Result.ok(contractService.getPage(contractQuery));
    }

    @PostMapping("saveOrUpdate")
    @Operation(summary = "新增/修改合同信息")
    @Log(title = "新增/修改合同信息", businessType = BusinessType.SELECT)
    public Result saveOrUpdate(@RequestBody @Validated ContractVO customerVO) {
        contractService.saveOrUpdate(customerVO);
        return Result.ok();
    }

    @PostMapping("getContractTrendData")
    @Operation(summary = "合同数量趋势统计", description = "按时间维度（日/周/月）统计合同数量趋势")
    public Result<Map<String, List>> getContractTrendData(@RequestBody ContractTrendQuery query) {
        // 调用合同服务层获取趋势数据，返回格式与客户接口一致
        return Result.ok(contractService.getContractTrendData(query));
    }

}