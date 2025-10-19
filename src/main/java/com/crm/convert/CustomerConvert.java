package com.crm.convert;
import com.crm.entity.Customer;
import com.crm.vo.CustomerVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomerConvert {
    CustomerConvert INSTANCE = Mappers.getMapper(CustomerConvert.class);

    // 新增：CustomerVO 转换为 Customer 实体
    Customer convert(CustomerVO customerVO);

    // 可选：如果需要反向转换，可添加
    CustomerVO convert(Customer customer);
}
