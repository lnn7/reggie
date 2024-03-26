package com.itheima.reggie.common;


//全局异常处理器
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice(annotations = {RestController.class, Controller.class})//有加RestController和Controller注解的会被处理
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {

    //异常处理方法
    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception){
        log.error(exception.getMessage());

        if (exception.getMessage().contains("Duplicate entry")){
            String[] split = exception.getMessage().split(" ");
            String msg = split[2]+"用户已存在";
            return R.error(msg);
        }

        return R.error("未知错误");

    }


    //自定义异常处理方法
    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException customException){
        log.error(customException.getMessage());

        return R.error(customException.getMessage());

    }
}



