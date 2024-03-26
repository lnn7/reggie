package com.itheima.reggie.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/*
* 分类管理
* */
@RestController
@RequestMapping("/category")
@Slf4j
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    //新增分类

    @PostMapping
    public R<String> save(@RequestBody Category category){

        log.info("category {}",category);
        categoryService.save(category);
        return R.success("分类新增成功");
    }


    @GetMapping("/page")
    public R<Page> page(int page,int pageSize){

        //构造分页构造器
        Page<Category> pageInfo = new Page<>(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        //执行查询
        categoryService.page(pageInfo,lambdaQueryWrapper);

        return R.success(pageInfo);
    }

    /*
    *根据id删除分类
    * */
    @DeleteMapping
    public R<String> delete(Long ids){
        log.info("删除id为{}的分类",ids);

        categoryService.remove(ids);
        return R.success("成功删除该分类");
    }


    /*
    * 根据id修改分类信息
    * */
    @PutMapping
    public R<String> update(@RequestBody Category category){
        log.info("修改分类信息");

        categoryService.updateById(category);
        
        return  R.success("修改分类信息成功");

    }

    //根据条件查询分类数据
    @GetMapping("/list")
    public R<List<Category>> list(Category category){

        //条件构造器
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加条件
        lambdaQueryWrapper.eq(category.getType()!=null,Category::getType,category.getType());
        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(lambdaQueryWrapper);

        return R.success(list);
    }

}
