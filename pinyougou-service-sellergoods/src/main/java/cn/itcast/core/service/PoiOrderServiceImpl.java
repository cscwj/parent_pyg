package cn.itcast.core.service;

//Created by Wang on itCast.  

import cn.itcast.core.mapper.order.OrderDao;
import cn.itcast.core.pojo.order.Order;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Wang
 * @version 1.0
 * -description step :
 * -date        2018-12-30--17:09 @_@ ~~
 */
@Service
@Transactional
public class PoiOrderServiceImpl implements PoiOrderService {
  @Autowired
  private OrderDao orderDao;
  @Override
  public List<Order> selectList(Order order) {
    return orderDao.selectByExample(null);
  }
}
