package com.xiaomei.service.impl;

import com.xiaomei.common.base.BaseQuery;
import com.xiaomei.common.base.PageObject;
import com.xiaomei.entity.Product;
import com.xiaomei.mapper.ProductMapper;
import com.xiaomei.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Override
    public PageObject<Product> queryList(BaseQuery query) {
        Integer factoryId = query.getFactoryId();
        if (factoryId == null) {
            return PageObject.error("工厂ID不能为空");
        }
        int offset = (query.getPageNum() - 1) * query.getPageSize();
        List<Product> list = productMapper.selectList(factoryId, offset, query.getPageSize());
        Long total = productMapper.selectCount(factoryId);
        return new PageObject<>(list, query.getPageNum(), query.getPageSize(), total);
    }

    @Override
    public Product queryById(Integer id) {
        return productMapper.selectById(id);
    }

    @Override
    public Map<String, String> add(Product product) {
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            return error("产品名称不能为空");
        }
        if (product.getProductNum() == null || product.getProductNum().trim().isEmpty()) {
            return error("产品编号不能为空");
        }
        if (product.getFactoryId() == null) {
            return error("工厂ID不能为空");
        }
        productMapper.insert(product);
        return success("添加成功");
    }

    @Override
    public Map<String, String> edit(Product product) {
        if (product.getId() == null) {
            return error("产品ID不能为空");
        }
        productMapper.update(product);
        return success("修改成功");
    }

    @Override
    public Map<String, String> delete(Integer id, Integer updateUserid) {
        productMapper.delete(id, updateUserid);
        return success("删除成功");
    }

    private Map<String, String> success(String msg) {
        return Map.of("status", "ok", "msg", msg);
    }

    private Map<String, String> error(String msg) {
        return Map.of("status", "error", "msg", msg);
    }
}
