package cn.itcast.core.service;

//Created by Wang on itCast.  

import cn.itcast.core.pojo.order.Order;

import java.util.List;

/**
 * @author Wang
 * @version 1.0
 * -description lianxi
 * -date        2018-12-30--17:09 @_@ ~~
 */

public interface PoiOrderService {
  List<Order> selectList(Order order);
}
