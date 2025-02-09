package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    //发送手机验证码方法
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        log.info("phone = {}",phone);
        if(!StringUtils.isEmpty(phone)){

            //生成随机4为验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();
            log.info("code={}",code);
            //发送短信

            //需要将生成的验证码保存到session
           session.setAttribute(phone,code);

           return R.success("验证码发送成功");

        }

        return R.error("验证码发送失败");
    }



    //移动端用户登录
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session){
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String  code = map.get("code").toString();
        //从session中获取保存的验证码
        Object  codeInSession = session.getAttribute(phone);
        //验证码比对（页面提交的验证码和session中保存的验证码比对）
        if (codeInSession != null && codeInSession.equals(code)){
            //比对成功，登录成功

            LambdaQueryWrapper<User> lambdaQueryWrapper= new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(lambdaQueryWrapper);
            if (user== null){
                //判断手机号对应的用户是否为新用户，若是新用户则自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                userService.save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }

        return R.error("登录失败");
    }


}
