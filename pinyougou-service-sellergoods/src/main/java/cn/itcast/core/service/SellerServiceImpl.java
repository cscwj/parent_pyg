package cn.itcast.core.service;

import cn.itcast.core.mapper.seller.SellerDao;
import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

//商家管理
@Service
@Transactional
public class SellerServiceImpl implements SellerService{

    @Autowired
    private SellerDao sellerDao;

    @Override
    public void add(Seller seller) {

        //密码加密
        BCryptPasswordEncoder bp = new BCryptPasswordEncoder();
        seller.setPassword(bp.encode(seller.getPassword()));

        //设置审核状态,默认0为审核
        seller.setStatus("0");

        sellerDao.insertSelective(seller);

    }

    @Override
    public Seller findOne(String username) {
        return sellerDao.selectByPrimaryKey(username);
    }
}
