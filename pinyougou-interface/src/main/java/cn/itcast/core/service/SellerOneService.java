package cn.itcast.core.service;

import cn.itcast.core.pojo.seller.Seller;
import entity.PageResult;


public interface SellerOneService {
    PageResult search(Integer page, Integer rows, Seller seller);

    Seller findOne(String id);

    void updateStatus(Seller seller);
}
