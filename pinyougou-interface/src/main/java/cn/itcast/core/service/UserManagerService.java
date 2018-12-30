package cn.itcast.core.service;

import cn.itcast.core.pojo.user.User;
import entity.PageResult;

public interface UserManagerService {

    PageResult search(Integer page, Integer rows, User user);

    void freeze(Long[] ids);

    void unfreeze(Long[] ids);

    Integer findUserNum();

    Integer findLineUserNum();

    Integer[] findNums();
}
