package cn.itcast.core.pojo;

import cn.itcast.core.pojo.order.OrderItem;

import java.io.Serializable;
import java.util.List;

public class Cart implements Serializable{
    //商家Id
    private String sellerId;

    //商家名称
    private String sellerName;

    //商品结构集
    private List<OrderItem> orderItemList;

    //编写商家id的equals和hashCode,因为List和Set,判断重复只能是简单类型,一旦是pojo需要重写


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cart)) return false;

        Cart cart = (Cart) o;

        return sellerId.equals(cart.sellerId);
    }

    @Override
    public int hashCode() {
        return sellerId.hashCode();
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public List<OrderItem> getOrderItemList() {
        return orderItemList;
    }

    public void setOrderItemList(List<OrderItem> orderItemList) {
        this.orderItemList = orderItemList;
    }
}
