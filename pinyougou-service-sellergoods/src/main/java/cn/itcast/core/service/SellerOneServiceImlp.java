package cn.itcast.core.service;

import cn.itcast.core.mapper.seller.SellerDao;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.seller.SellerQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
public class SellerOneServiceImlp implements SellerOneService {

    @Autowired
    private SellerDao sellerDao;

    @Override
    public PageResult search(Integer page, Integer rows, Seller seller) {
        //分页插件
        PageHelper.startPage(page ,rows);
        //创建条件对象,模糊查询
        SellerQuery query=new SellerQuery();
        SellerQuery.Criteria criteria = query.createCriteria();

        if(null!=seller.getName() && !"".equals(seller.getName().trim())){
            criteria.andNameLike(seller.getName().trim());
        }

        if(null!=seller.getNickName() && !"".equals(seller.getNickName().trim())){
            criteria.andNameLike(seller.getNickName().trim());
        }

        Page<Seller> p = (Page<Seller>) sellerDao.selectByExample(query);

        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public Seller findOne(String id) {
        return sellerDao.selectByPrimaryKey(id);
    }

    @Override
    public void updateStatus(Seller seller) {
        sellerDao.updateByPrimaryKeySelective(seller);
    }
}
