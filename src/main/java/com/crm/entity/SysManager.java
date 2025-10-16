package com.crm.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@TableName("sys_manager")
public class SysManager {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("account")
    private String account;

    @TableField("nickname")
    private String nickname;

    @TableField("password")
    private String password;

    @TableField("status")
    private Integer status;

    @TableField("depart_id")
    private Integer departId;

    @TableField(value = "delete_flag", fill = FieldFill.INSERT)
    @TableLogic
    private Integer deleteFlag;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}