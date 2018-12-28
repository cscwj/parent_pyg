package cn.itcast.core.controller;

//Created by Wang on itCast.  

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * @author Wang
 * @version 1.0
 * -description step :
 * -date        2018-12-28--12:17 @_@ ~~
 */
@RestController
@RequestMapping("/test")
public class TestController {
  @RequestMapping("/getnum")
  public BigDecimal getnum(){
    Long aLong = new Long("123456789012345678");
    return new BigDecimal(aLong);
  }
  @RequestMapping("/getnum2")
  public long getnum2(){
    return 123456789012345678L;
  }
}
