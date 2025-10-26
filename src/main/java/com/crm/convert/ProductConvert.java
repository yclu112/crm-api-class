package com.crm.convert;

import com.crm.entity.Product;
import com.crm.vo.ProductVO;
import org.mapstruct.factory.Mappers;

public interface ProductConvert {
    ProductConvert INSTANCE = Mappers.getMapper(ProductConvert.class);

    // 新增：CustomerVO 转换为 Customer 实体
    Product convert(ProductVO productVO);

    // 可选：如果需要反向转换，可添加
    ProductVO convert(Product product);
}
