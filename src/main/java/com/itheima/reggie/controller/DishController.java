package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

//菜品管理
@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;

    //新增菜品
    @PostMapping
    public R<String> save(@RequestBody DishDto  dishDto){
        log.info(dishDto.toString());

        dishService.saveWithFlavor(dishDto);
        return R.success("新增菜品成功");
    }


    @GetMapping("/page")
    //菜品分页查询
    public R<Page> page(int page,int pageSize,String name){

        //构造分页构造器
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        //构造条件构造器
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        lambdaQueryWrapper.like(name != null,Dish::getName,name);
        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Dish::getUpdateTime);

        //执行分页查询
        dishService.page(pageInfo,lambdaQueryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

         List<Dish> records = pageInfo.getRecords();

         List<DishDto> list = records.stream().map((item) ->{
             DishDto dishDto = new DishDto();

             BeanUtils.copyProperties(item,dishDto);

             Long categoryId = item.getCategoryId();//分类id

             //根据id查询分类对象
             Category category = categoryService.getById(categoryId);
             if (category != null){
                 String categoryName = category.getName();
                 dishDto.setCategoryName(categoryName);
             }


             return dishDto;
         }).collect(Collectors.toList());

         dishDtoPage.setRecords(list);

         return  R.success(dishDtoPage);
}


    //根据id查询菜品信息和对应的口味信息
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);

    }


    //修改菜品
    @PutMapping
    public R<String> update(@RequestBody DishDto  dishDto){
        log.info(dishDto.toString());

        dishService.updateWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }


    //根据条件查询对应的菜品数据
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//
//        //构造查询条件
//        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
//        lambdaQueryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
//        //添加查询条件，查询状态为1（在售状态）的菜品
//        lambdaQueryWrapper.eq(Dish::getStatus,1);
//
//
//        //添加排序条件
//        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(lambdaQueryWrapper);
//
//
//        return R.success(list);
//
//    }
//

    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){

        //构造查询条件
        LambdaQueryWrapper<Dish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(dish.getCategoryId() != null ,Dish::getCategoryId,dish.getCategoryId());
        //添加查询条件，查询状态为1（在售状态）的菜品
        lambdaQueryWrapper.eq(Dish::getStatus,1);


        //添加排序条件
        lambdaQueryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(lambdaQueryWrapper);


        List<DishDto> dishDtoList = list.stream().map((item) ->{
            DishDto dishDto = new DishDto();

            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id

            //根据id查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category != null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }

            //当前菜品id
            Long dishId = item.getId();
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper=new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> flavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(flavors);

            return dishDto;
        }).collect(Collectors.toList());

        return R.success(dishDtoList);





    }





}
