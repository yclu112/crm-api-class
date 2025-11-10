package com.crm.mapper;

import com.crm.entity.Contract;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.query.ContractTrendQuery;
import com.crm.vo.ContractTrendVO;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
public interface ContractMapper extends MPJBaseMapper<Contract> {
    // 通用维度统计（对应 XML 中 id="getContractTradeStatistics"）
    List<ContractTrendVO> getTradeStatistics(@Param("query") ContractTrendQuery query);

    // 日维度统计（对应 XML 中 id="getContractTradeStatisticsByDay"，修正方法名）
    List<ContractTrendVO> getTradeStatisticsByDay(@Param("query") ContractTrendQuery query);

    // 周维度统计（对应 XML 中 id="getContractTradeStatisticsByWeek"）
    List<ContractTrendVO> getTradeStatisticsByWeek(@Param("query") ContractTrendQuery query);
}
