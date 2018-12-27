package cn.itcast.core.controller;

import cn.itcast.common.utils.PhoneFormatCheckUtils;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
public class UserController {

    @Reference
    private UserService userService;

    @RequestMapping("/sendCode")
    public Result sendCode(String phone){
        //工具类判断手机号是否合法
        if (PhoneFormatCheckUtils.isPhoneLegal(phone)){
            try {
                userService.sendCode(phone);
                return new Result(true,"发送成功");
            } catch (Exception e) {
                return new Result(false,"服务器异常");
            }

        }else{
            return new Result(false,"验证码不合法");
        }

    }

    @RequestMapping("/add")
    public Result add(@RequestBody User user,String smscode){
        try {
            userService.add(user,smscode);
            return new Result(true,"注册成功");
        } catch (RuntimeException e) {
            //这里捕捉service层传来异常
            throw new RuntimeException(e);
        }catch (Exception e){
            return new Result(true,"注册失败");
        }

    }

}
