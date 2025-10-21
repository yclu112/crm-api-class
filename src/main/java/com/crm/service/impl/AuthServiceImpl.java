package com.crm.service.impl;

import com.crm.common.exception.ServerException;
import com.crm.entity.Department;
import com.crm.entity.Manager;
import com.crm.mapper.DepartmentMapper;
import com.crm.mapper.ManagerMapper;
import com.crm.security.cache.TokenStoreCache;
import com.crm.security.user.ManagerDetail;
import com.crm.security.utils.TokenUtils;
import com.crm.service.AuthService;
import com.crm.vo.SysAccountLoginVO;
import com.crm.vo.SysTokenVO;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;



/**
 * 认证服务实现
 *
 * @Author crm
 * @Date 2023-05-18 17:31
 */
@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final ManagerMapper managerMapper;
    private final DepartmentMapper departmentMapper;
    private final TokenStoreCache tokenStoreCache;

    @Override
    public SysTokenVO loginByAccount(SysAccountLoginVO params) {
        Authentication authentication;
        try {
            // 认证用户
             authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(params.getAccount(), params.getPassword())
            );
        } catch (DisabledException e) {
            throw new ServerException("该账号已被禁用");
        } catch (BadCredentialsException e) {
            throw new ServerException("用户名或密码错误");
        }
        // 用户信息
        ManagerDetail managerDetail = (ManagerDetail) authentication.getPrincipal();
        // 获取用户详情并补充部门信息
        if (managerDetail.getDepartId() != null) {
            Department department = departmentMapper.selectById(managerDetail.getDepartId());
            if (department != null) {
                managerDetail.setDepartName(department.getName());
            }
        }
        // 生成 accessToken
        String accessToken = TokenUtils.generator();
        // 保存用户信息到缓存
        tokenStoreCache.saveUser(accessToken, managerDetail);
        return new SysTokenVO(accessToken);
    }
    @Override
    public void logout(String accessToken) {
        // 用户信息
        ManagerDetail manager = tokenStoreCache.getUser(accessToken);

        // 删除用户信息
        tokenStoreCache.deleteUser(accessToken);

    }
}
