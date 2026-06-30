package com.xiaomei.controller;

import com.xiaomei.common.base.BaseController;
import com.xiaomei.common.base.BaseQuery;
import com.xiaomei.common.base.PageObject;
import com.xiaomei.entity.Product;
import com.xiaomei.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController extends BaseController {

    @Autowired
    private ProductService productService;

    @PostMapping("/list")
    public PageObject<Product> list(@RequestBody BaseQuery query) {
        if (query.getFactoryId() == null) {
            query.setFactoryId(getFactoryId());
        }
        return productService.queryList(query);
    }

    @PostMapping("/detail")
    public Product detail(@RequestBody Product product) {
        return productService.queryById(product.getId());
    }

    @PostMapping("/add")
    public Map<String, String> add(@RequestBody Product product) {
        product.setCreateUserid(getCurrentUserId());
        product.setUpdateUserid(getCurrentUserId());
        if (product.getFactoryId() == null) {
            product.setFactoryId(getFactoryId());
        }
        return productService.add(product);
    }

    @PostMapping("/edit")
    public Map<String, String> edit(@RequestBody Product product) {
        product.setUpdateUserid(getCurrentUserId());
        return productService.edit(product);
    }

    @PostMapping("/delete")
    public Map<String, String> delete(@RequestBody Product product) {
        return productService.delete(product.getId(), getCurrentUserId());
    }
}
