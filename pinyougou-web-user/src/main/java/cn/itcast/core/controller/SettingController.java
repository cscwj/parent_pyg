package cn.itcast.core.controller;

//Created by Wang on itCast.  

import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Wang
 * @version 1.0
 * -description step :
 * -date        2018-12-29--14:58 @_@ ~~
 */
@RestController
@RequestMapping("/setting")
public class SettingController {
  @Reference
  private UserService userService;

  @RequestMapping("/findMessage")
  public User findMessage() {
    String name = SecurityContextHolder.getContext().getAuthentication().getName();
    return userService.findByName(name);
  }
}
