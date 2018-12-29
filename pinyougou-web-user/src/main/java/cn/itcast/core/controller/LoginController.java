package cn.itcast.core.controller;

//Created by Wang on itCast.  

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Wang
 * @version 1.0
 * -description step :
 * -date        2018-12-29--14:14 @_@ ~~
 */
@RestController
@RequestMapping("/login")
public class LoginController {
  @RequestMapping("/name")
  public Map<String,String> name(){
    Map<String,String> map = new HashMap<>();
    String name = SecurityContextHolder.getContext().getAuthentication().getName();
    map.put("loginName", name);
    return map;
  }
}
