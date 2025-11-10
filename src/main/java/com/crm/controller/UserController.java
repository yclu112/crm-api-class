// com/crm/controller/UserController.java
package com.crm.controller;

import com.crm.common.aop.Log;
import com.crm.common.result.Result;
import com.crm.enums.BusinessType;
import com.crm.security.user.ManagerDetail;
import com.crm.security.user.SecurityUser;
import com.crm.vo.SysLoginResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "用户信息")
@RestController
@RequestMapping("sys/user")
public class UserController {

    @GetMapping("current")
    @Operation(summary = "获取当前登录用户信息")
    @Log(title = "获取当前登录用户信息", businessType = BusinessType.SELECT)
    public Result<SysLoginResultVO> getCurrentUser() {
        ManagerDetail manager = SecurityUser.getManager();

        SysLoginResultVO resultVO = new SysLoginResultVO();
        resultVO.setId(manager.getId());
        resultVO.setAccount(manager.getAccount());
        resultVO.setNickname(manager.getNickname());
        resultVO.setDepartId(manager.getDepartId());
        resultVO.setDepartName(manager.getDepartName());

        return Result.ok(resultVO);
    }
}