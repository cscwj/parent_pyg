package cn.itcast.core.service;

import cn.itcast.core.controller.ContentController;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import redis.clients.jedis.JedisPoolConfig;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.HashSet;
@Component
public class OnlineCounterListener  implements HttpSessionListener{
    // 监听 session 创建
    public void sessionCreated(HttpSessionEvent event) {
    //拿到session
     HttpSession session=event.getSession();
     //域对象
    ServletContext context=session.getServletContext();
    //用set集合来存储session对象
    HashSet<HttpSession> sessionSet=(HashSet<HttpSession>) context.getAttribute("sessionSet");
    if(sessionSet==null){
        //第一次创建
        sessionSet=new HashSet<HttpSession>();
        context.setAttribute("sessionSet", sessionSet);
    }

    sessionSet.add(session);
    //存储在线人数，利用了set集合不重复的特性，避免了重复登录
    context.setAttribute("lineCount", sessionSet.size());
    //System.out.println("创建在线人数:"+context.getAttribute("lineCount"));

}

    //session的销毁监听
    public void sessionDestroyed(HttpSessionEvent event) {
        int lineCount=0;
        ServletContext context = event.getSession().getServletContext();
        if (context.getAttribute("lineCount") == null) {
            context.setAttribute("lineCount", 0);
        } else {
            lineCount = (Integer) context.getAttribute("lineCount");
            if (lineCount < 1) {
                lineCount = 1;
            }
            context.setAttribute("lineCount", lineCount - 1);
        }
        HttpSession session = event.getSession();
        HashSet<HttpSession> sessionSet = (HashSet<HttpSession>)context.getAttribute("sessionSet");
        if(sessionSet!=null){
            sessionSet.remove(session);
        }
       // System.out.println("销毁在线人数:"+context.getAttribute("lineCount"));
    }

}
