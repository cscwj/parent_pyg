package cn.itcast.core.service;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.seller.Seller;
import entity.PageResult;
import pojogroup.OrderVo;

import java.util.List;
import java.util.Map;

public interface OrderService {
    void add(Order order);

    PageResult search(Integer page, Integer rows, Order order);

    OrderVo findOne(Long id);

    void update(OrderVo orderVo);

    List<Seller> findSellerList();

    Map<String,Object> orderCount(Order order);

    List<String> findSellerName();

    Map<String,Object> findEcharData(String sellerId);

    Map<String,Object> findEcharCircle();

}
