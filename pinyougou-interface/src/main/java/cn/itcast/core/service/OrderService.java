package cn.itcast.core.service;

import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.seller.Seller;
import entity.PageResult;
import pojogroup.OrderVo;

import java.util.List;

public interface OrderService {
    void add(Order order);

    PageResult search(Integer page, Integer rows, Order order);

    OrderVo findOne(Long id);

    void update(OrderVo orderVo);

    List<Seller> findSellerList();

}
