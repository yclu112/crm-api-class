// com/crm/vo/SysLoginResultVO.java
package com.crm.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "登录结果")
public class SysLoginResultVO {
    @Schema(description = "access_token")
    private String access_token;

    @Schema(description = "用户ID")
    private Integer id;

    @Schema(description = "账号")
    private String account;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "部门ID")
    private Integer departId;

    @Schema(description = "部门名称")
    private String departName;

    @Schema(description = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;
}