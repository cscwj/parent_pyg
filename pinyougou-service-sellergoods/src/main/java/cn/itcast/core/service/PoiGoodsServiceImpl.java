package cn.itcast.core.service;

//Created by Wang on itCast.  

import cn.itcast.core.mapper.good.GoodsDao;
import cn.itcast.core.pojo.good.Goods;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Wang
 * @version 1.0
 * -description step :
 * -date        2018-12-30--15:20 @_@ ~~
 */
@Service
@Transactional
public class PoiGoodsServiceImpl implements PoiGoodsService {
  @Autowired
  private GoodsDao goodsDao;
  @Override
  public List<Goods> selectList(Goods goods) {
    return goodsDao.selectByExample(null);
  }
}
