package cn.itcast.core.service;

import cn.itcast.core.pojo.seller.Seller;

public interface SellerService {
    void add(Seller seller);

    Seller findOne(String username);
}
