package cn.itcast.core.service;


import cn.itcast.core.mapper.user.UserDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.pojo.user.UserQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;


import javax.jms.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 用户管理
 */
@Service
public class UserServiceImpl implements  UserService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination smsDestination;
    @Autowired
    private UserDao userDao;

    @Override
    public void sendCode(String phone) {
        //1.生成6为验证码
        String code = RandomStringUtils.randomNumeric(6);
        //2.放入缓存一份,用于注册校验
        redisTemplate.boundValueOps(phone).set(code);
        //设置存活时间,5分钟,开发阶段5天
        redisTemplate.boundValueOps(phone).expire(5, TimeUnit.DAYS );

        //3.发送消息
        jmsTemplate.send(smsDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                MapMessage mapMessage = session.createMapMessage();
                //跟换为腾讯云
                mapMessage.setString("phone",phone);
                mapMessage.setString("templateParam",code);
                mapMessage.setString("signName","程文佳畅想IT");
                mapMessage.setString("templateCode","249711");

                //mapMessage.setString("templateParam","{\"number\":\""+code+"\"}");
                //mapMessage.setString("signName","品优购商城");
                //mapMessage.setString("templateCode","SMS_126462276");
                return mapMessage;
            }
        });

    }

    @Override
    public void add(User user, String smscode) {
        //判断验证码是否正确
        String code = (String) redisTemplate.boundValueOps(user.getPhone()).get();
        if (null!=code && smscode.equals(code)){
            //验证码是正确的,保存
            user.setCreated(new Date());
            user.setUpdated(new Date());
            userDao.insertSelective(user);
        }else{
            //设置运行时异常,前台接收
            throw new RuntimeException("验证码不正确");
        }

    }


}
