package com.crm.controller;


import com.crm.common.aop.Log;
import com.crm.common.result.Result;
import com.crm.enums.BusinessType;
import com.crm.security.cache.TokenStoreCache;
import com.crm.security.user.ManagerDetail;
import com.crm.security.utils.TokenUtils;
import com.crm.service.AuthService;
import com.crm.vo.SysAccountLoginVO;
import com.crm.vo.SysLoginResultVO;
import com.crm.vo.SysTokenVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


/**
 * <p>
 * 用户管理 前端控制器
 * </p>
 *
 * @author crm
 * @since 2023-05-18
 */
@Tag(name = "认证管理")
@RestController
@RequestMapping("sys/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final TokenStoreCache tokenStoreCache;

    @PostMapping("login")
    @Operation(summary = "账号密码登录")
    @Log(title = "账号密码登录", businessType = BusinessType.SELECT)
    public Result<SysLoginResultVO> login(@RequestBody SysAccountLoginVO login) {
        SysTokenVO tokenVO = authService.loginByAccount(login);
        ManagerDetail managerDetail = tokenStoreCache.getUser(tokenVO.getAccess_token());

        SysLoginResultVO resultVO = new SysLoginResultVO();
        resultVO.setAccess_token(tokenVO.getAccess_token());
        resultVO.setId(managerDetail.getId());
        resultVO.setAccount(managerDetail.getAccount());
        resultVO.setNickname(managerDetail.getNickname());
        resultVO.setDepartId(managerDetail.getDepartId());
        resultVO.setDepartName(managerDetail.getDepartName());
        // 设置过期时间，这里假设24小时后过期
        resultVO.setExpireTime(LocalDateTime.now().plusHours(24));

        return Result.ok(resultVO);
    }


    @PostMapping("logout")
    @Operation(summary = "退出")
    @Log(title = "退出", businessType = BusinessType.SELECT)
    public Result<String> logout(HttpServletRequest request) {
        authService.logout(TokenUtils.getAccessToken(request));

        return Result.ok();
    }


}
//    @PostMapping("login")
//    @Operation(summary = "账号密码登录")
//    public Result<SysTokenVO> login(@RequestBody SysAccountLoginVO login) {
//        return Result.ok(authService.loginByAccount(login));
//    }