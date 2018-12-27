package cn.itcast.core.service;

import cn.itcast.core.mapper.good.BrandDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

    @Autowired  //不是ali的reference
    private BrandDao brandDao;

    @Override
    public List<Brand> findAll() {
        return brandDao.selectByExample(null);
    }

    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        //分页插件
        PageHelper.startPage(pageNum, pageSize);
        //List<Brand> brands = brandDao.selectByExample(null);
        //PageInfo<Brand> info=new PageInfo<>(brands);

        Page<Brand> p = (Page<Brand>) brandDao.selectByExample(null);
        return new PageResult(p.getTotal(), p.getResult());
    }

    @Override
    public void add(Brand brand) {
        //insertSelective,比insert效率高,应为前者保存有值得,后者没有值保存null处理
        brandDao.insertSelective(brand);
    }

    @Override
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(Brand brand) {
        //brandDao.updateByPrimaryKey(brand); 效率低
        brandDao.updateByPrimaryKeySelective(brand);
    }

    @Override
    public void delete(Long[] ids) {
        //多次连接数据库,效率低
//        for (Long id : ids) {
//            brandDao.deleteByPrimaryKey(id);
//        }

        BrandQuery brandQuery = new BrandQuery();
        //数组转集合 Arrays.asList(ids)                               andIdIn (adn id in )
        //品牌条件对象,brandQuery.createCriteria()相当动态sql的where标签(删除第一个and) 后面跟的方法是条件and..and
        brandQuery.createCriteria().andIdIn(Arrays.asList(ids));

        brandDao.deleteByExample(brandQuery);
    }

    @Override
    public PageResult search(Integer pageNum, Integer pageSize, Brand brand) {
        //分页插件
        PageHelper.startPage(pageNum,pageSize );
        //创建品牌条件对象
        BrandQuery brandQuery=new BrandQuery();
        //创建对象,如果if里面brandQuery.createCriteria().方法 会创建2个对象
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        //trim()去掉前后空串,判断名字
        if (null !=brand.getName() && !"".equals(brand.getName().trim())){
            criteria.andNameLike("%"+brand.getName().trim()+"%");
        }
        //判断首字母是否相等
        if (null !=brand.getFirstChar() && !"".equals(brand.getFirstChar().trim())){
            criteria.andFirstCharEqualTo(brand.getFirstChar().trim());
        }

        //查询
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(brandQuery);

        PageResult pageResult = new PageResult(page.getTotal(),page.getResult());
        return pageResult;
    }

    @Override
    public List<Map> selectOptionList() {
        //手写SQL,查询结果接,自己封装map效率低
        //自己查询1,手动封装map
        return brandDao.selectOptionList();
    }
}
