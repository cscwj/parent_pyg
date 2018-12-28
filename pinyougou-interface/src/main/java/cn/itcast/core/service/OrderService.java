package cn.itcast.core.service;

import cn.itcast.core.pojo.order.Order;
import entity.PageResult;
import pojogroup.OrderVo;

public interface OrderService {
    void add(Order order);

    PageResult search(Integer page, Integer rows, Order order);

    OrderVo findOne(Long id);

    void update(OrderVo orderVo);

}
