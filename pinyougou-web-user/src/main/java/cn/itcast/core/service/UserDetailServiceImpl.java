package cn.itcast.core.service;

import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Set;

//自定义授权类
public class UserDetailServiceImpl implements UserDetailsService {


    //这里不能注解注入,security1.xml扫在父容器中,没有办法用controller的mvc注解,属性注入,标签配置

    private ContentService contentService;

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //权限
        Set<GrantedAuthority> authorities=new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        //1.判断是否登录 配置了匿名登录,未登录显示 anonymousUser  为了防止出现空指针异常
        if (!"anonymousUser".equals(username)) {
            //登录 - - >设置活跃用户
            contentService.activeUser(username);
        }



        //密码不能为null,查看源码发现为null会抛异常
        return new User(username,"",authorities);
    }
}
