package cn.itcast.core.service;

//Created by Wang on itCast.  

import cn.itcast.core.mapper.user.UserDao;
import cn.itcast.core.pojo.user.User;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Wang
 * @version 1.0
 * -description step :
 * -date        2018-12-30--13:15 @_@ ~~
 */
@Service
@Transactional
public class PoiUserServiceImpl implements PoiUserService {
  @Autowired
  private UserDao userDao;

  public void createXlsx(){
    List<User> users = userDao.selectByExample(null);

  }
  @Override
  public List<User> selectByQuery(User user) {
    return userDao.selectByExample(null);
  }
}
