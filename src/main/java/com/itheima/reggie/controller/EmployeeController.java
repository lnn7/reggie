package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    //员工登录
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        /*
        1.将页面提交的密码解析MD5加密处理
        2.根据页面提交的用户名查询数据库
        3.若没有查询到则返回登录失败结果
        4.密码比对，若不一致则返回登录失败结果
        5.查看员工状态，若被禁用，则返回员工被禁用结果
        6.登录成功，将员工id存入session并返回登录成功结果
         */



        //1.将页面提交的密码解析MD5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());

        //2.根据页面提交的用户名查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);//.getOne()方法最终得到的是实体类对象，可以通过get方法获取

        //3.若没有查询到则返回登录失败结果
        if(emp == null){
            return R.error("账号不存在，请注册");
        }

        //4.密码比对，若不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)){
            return R.error("密码错误，登录失败");
        }

        //5.查看员工状态，若被禁用，则返回员工被禁用结果
        if (emp.getStatus() == 0){
            return R.error("账号被禁用");
        }

        //6.登录成功，将员工id存入session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    /*
         员工退出登录
    * */

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){

        //用户退出登录，销毁当前会话域中的一个属性。
        request.getSession().removeAttribute("employee");
        return R.success("退出成功！");
    }

    //新增员工
    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工信息为：{}",employee.toString());

        //设置用户初始密码，需要解析MD5加密处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));

        //设置创建与更新时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());

        //设置创建和更新用户（即当前登录用户）

//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);

        employeeService.save(employee); //调用IService的save()方法
        return R.success("成功新增员工");
    }


    //员工信息的分页查询
    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
        log.info("page = {},pageSize ={},name = {}",page,pageSize,name);

        //构造分页构造器
        Page pageInfo = new Page(page, pageSize);

        //构造条件构造器
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper();

        //添加过滤条件
        lambdaQueryWrapper.like(!StringUtils.isEmpty(name),Employee::getName,name);

        //添加排序条件
        lambdaQueryWrapper.orderByDesc(Employee::getUpdateTime);

        //执行查询

        employeeService.page(pageInfo,lambdaQueryWrapper);

        return R.success(pageInfo);
    }



    //根据id修改员工信息
    @PutMapping
    public R<String> update(HttpServletRequest request ,@RequestBody Employee employee){
        log.info(employee.toString());


//        Long empId = (Long) request.getSession().getAttribute("employee");
//
//        employee.setUpdateUser(empId);
//        employee.setUpdateTime(LocalDateTime.now());
        employeeService.updateById(employee);

        return R.success("用户信息修改成功");
    }

    //根据id查询员工信息
    @GetMapping("/{id}")
    public  R<Employee> grtById(@PathVariable Long id){
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("没有该用户");
    }

}
