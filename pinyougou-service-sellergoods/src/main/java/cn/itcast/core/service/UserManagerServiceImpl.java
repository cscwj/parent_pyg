package cn.itcast.core.service;

import cn.itcast.core.mapper.user.UserDao;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.pojo.user.UserQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service
public class UserManagerServiceImpl implements UserManagerService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 条件查询
     * @param page
     * @param rows
     * @param user
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, User user) {
        //分页插件
        PageHelper.startPage(page, rows);
        //排序
        PageHelper.orderBy("id desc");
        UserQuery query = new UserQuery();
        UserQuery.Criteria criteria = query.createCriteria();
        if (null!=user.getUsername() && !"".equals(user.getUsername().trim())){
            criteria.andUsernameLike("%" + user.getUsername().trim() + "%");
        }
        Page<User> p = (Page<User>) userDao.selectByExample(query);
        return new PageResult(p.getTotal(), p.getResult());
    }

    /**
     * 账户冻结
     * @param ids
     */
    @Override
    public void freeze(Long[] ids) {
        User user = new User();
        user.setStatus("1");
        UserQuery query = new UserQuery();
        query.createCriteria().andIdIn(Arrays.asList(ids));
        userDao.updateByExampleSelective(user,query);
    }
    /**
     * 账户解冻
     * @param ids
     */
    @Override
    public void unfreeze(Long[] ids) {
        User user = new User();
        user.setStatus("");
        UserQuery query = new UserQuery();
        query.createCriteria().andIdIn(Arrays.asList(ids));
        userDao.updateByExampleSelective(user,query);
    }

    /**
     * 用户个数
     * @return
     */
    @Override
    public Integer findUserNum() {
        UserQuery query = new UserQuery();
        query.createCriteria().andIdIsNotNull();
        List<User> users = userDao.selectByExample(query);
        return users.size();
    }

    /**
     * 在线用户个数
     * @return
     */
    @Override
    public Integer findLineUserNum() {
        //redisTemplate.expire()
        return (Integer) redisTemplate.boundValueOps("lineCount").get();
        }

    @Override
    public Integer[] findNums() {
        Integer i = 0;

        Integer[] nums = new Integer[4];
        //总人数
        nums[0] = findUserNum();
        //在线人数
        nums[1] = findLineUserNum();
        //活跃人数
        //从缓存中获取到所有用户
        Set<String> activeUser = redisTemplate.boundHashOps("ActiveUser").keys();

        if (null != activeUser && activeUser.size()>0){

            for (String name : activeUser) {
                Integer count = (Integer) redisTemplate.boundHashOps("ActiveUser").get(name);
                //当用户一周内登录次数大于3 时 视为活跃用户
                if (count != null && count>3){
                    i++;
                }
            }
        }

        nums[2]=i;

        //非活跃人数
        nums[3]= nums[0]-i ;
        return nums;
    }


}
