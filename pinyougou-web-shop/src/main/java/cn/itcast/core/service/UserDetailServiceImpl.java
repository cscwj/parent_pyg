package cn.itcast.core.service;


import cn.itcast.core.pojo.seller.Seller;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Set;

public class UserDetailServiceImpl implements UserDetailsService {

    //这里不能注解注入,security1.xml扫在父容器中,没有办法用controller的mvc注解,属性注入,标签配置

    private SellerService sellerService;

    public void setSellerService(SellerService sellerService) {
        this.sellerService = sellerService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //根据用户名<这里表主键,name用于登录用户名>查询对象
        Seller seller = sellerService.findOne(username);

        //1.判断是商家对象否存在
        if (seller != null) {
            //2.商家存在,查看状态是否为1,为审核通过状态
            if("1".equals(seller.getStatus())){
                //配置1.设置权限集合
                Set<GrantedAuthority> authorities=new HashSet<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_SELLER"));

                return new User(username,seller.getPassword(),authorities);
            }

        }


        return null;
    }


}
