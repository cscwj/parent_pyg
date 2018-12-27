package cn.itcast.core.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/login")
public class LoginController {

    //获取当前登录人
    @RequestMapping("/showName")
    public Map<String,Object> showName(){
        Map<String,Object> map=new HashMap<>();

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        map.put("username",name );
        //new Date() 英国:格林威志时间   页面回显时间 字符串 yyyy-MM-dd HH:mm:ss
        //@ResponseBody 会自动转成默认类型
        map.put("curTime",new Date());

        return map;

    }


}
