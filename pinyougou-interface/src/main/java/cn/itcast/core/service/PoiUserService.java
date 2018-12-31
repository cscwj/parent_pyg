package cn.itcast.core.service;

//Created by Wang on itCast.  

import cn.itcast.core.pojo.user.User;

import java.util.List;

/**
 * @author Wang
 * @version 1.0
 * -description lianxi
 * -date        2018-12-30--13:14 @_@ ~~
 */

public interface PoiUserService {
  List<User> selectByQuery(User user);
}
