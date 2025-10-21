package com.crm.security.user;

import com.crm.entity.Manager;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ManagerDetail implements UserDetails {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String account;
    private String password;
    private String realName;
    private Integer status;
    private String username;
    private String nickname;
    private Integer departId; // 部门ID
    private String departName; // 部门名称


    /**
     * 帐户是否过期
     */
    private boolean isAccountNonExpired = true;
    /**
     * 帐户是否被锁定
     */
    private boolean isAccountNonLocked = true;
    /**
     * 密码是否过期
     */
    private boolean isCredentialsNonExpired = true;
    /**
     * 帐户是否可用
     */
    private boolean isEnabled = true;
    /**
     * 拥有权限集合
     */
    private Set<String> authoritySet;

    // 从Manager实体复制属性
    public static ManagerDetail fromManager(Manager manager) {
        ManagerDetail detail = new ManagerDetail();
        detail.setId(manager.getId());
        detail.setAccount(manager.getAccount());
        detail.setPassword(manager.getPassword());
        detail.setNickname(manager.getNickname());
        detail.setDepartId(manager.getDepartId());
        detail.setStatus(manager.getStatus());
        return detail;
    }

    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authoritySet.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }

    @Override
    public String getUsername() {
        return account;
    }

}