package com.crm.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.common.result.PageResult;
import com.crm.entity.Product;
import com.crm.query.ProductQuery;

public interface ProductService extends IService<Product> {
    /**
     * 商品列表分页查询
     * @param query 分页查询条件
     * @return 分页结果
     */
    PageResult<Product> getPage(ProductQuery query);
    void saveOrEdit(Product product);
    void batchUpdateProductState();
}