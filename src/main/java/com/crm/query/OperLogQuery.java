package com.crm.query;

import com.crm.common.model.Query;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


import java.sql.Timestamp;
import java.util.List;

@Data
public class OperLogQuery extends Query {
    @ApiModelProperty("操作人账号")
    private String operName;

    @ApiModelProperty("业务日志操作时间段")
    private List<Timestamp> operTime;

    @ApiModelProperty("接口Url(精确)")
    private String operUrl;
}