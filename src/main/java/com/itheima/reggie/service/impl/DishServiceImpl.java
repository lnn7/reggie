package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    //新增菜品，同时保存对应的口味数据
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表dish
        this.save(dishDto);

        Long  dishId= dishDto.getId();//菜品id

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        //保存菜品口味到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);

    }


    //根据id查询菜品信息和对应的口味信息
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息 dish 表
        Dish dish = this.getById(id);

        //对象拷贝
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);


        //查询菜品对应的口味信息 dish_flavor表

        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(lambdaQueryWrapper);
        dishDto.setFlavors(flavors);

        return dishDto;


    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新菜品表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应的口味数据
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper =new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(lambdaQueryWrapper);

        //添加当前提交过来的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }













}
