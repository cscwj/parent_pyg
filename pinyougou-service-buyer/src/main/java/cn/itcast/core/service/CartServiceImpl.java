package cn.itcast.core.service;

import cn.itcast.core.mapper.item.ItemDao;
import cn.itcast.core.pojo.Cart;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private ItemDao itemDao;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Item findItemById(Long itemId) {
        return itemDao.selectByPrimaryKey(itemId);
    }

    @Override
    public List<Cart> findCartList(List<Cart> cartList) {
        //购物车中只存在商家id,库存id,数量

        for (Cart cart : cartList) {
            List<OrderItem> orderItemList = cart.getOrderItemList();
            for (OrderItem orderItem : orderItemList) {
                Item item = findItemById(orderItem.getItemId());
                //商品图片
                orderItem.setPicPath(item.getImage());
                //标题
                orderItem.setTitle(item.getTitle());
                //单价
                orderItem.setPrice(item.getPrice());
                //小计  大类型-->double-->价格大类型
                orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));
                //商家名称
                cart.setSellerName(item.getSeller());
            }
        }

        return cartList;
    }

    @Override
    public void merge(List<Cart> newCartList, String name) {
        //1:获取出原来的购物车结果集
        List<Cart> oldCartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(name);
        //2:新老车大合并
        newCartList = mergeNewOld(oldCartList, newCartList);
        //3:合并后的老车结果集保存进缓存中
        redisTemplate.boundHashOps("cartList").put(name, newCartList);
    }

    //新老大车合并
    public List<Cart> mergeNewOld(List<Cart> oldCartList, List<Cart> newCartList) {
        //老车为空-->返回新车 | 老车不为空,新车为空-->返回老车 | 老车,新车都不为空-->合并   这里是添加添加购物车选项,如果自己点购物车都为空会在controll判断
        if (null != oldCartList && oldCartList.size() > 0) {
            if (null != newCartList && newCartList.size() > 0) {
                //新老车合并,和前面controller步骤一样,这里不加注释了,只是这是两集合的合并,前面是单独的一个合并集合
                //取出每一个购物车一一合并到老车
                for (Cart newcart : newCartList) {
                    int index1 = oldCartList.indexOf(newcart);
                    //比较是否存在商家
                    if (-1 != index1) {
                        Cart oldCart = oldCartList.get(index1);
                        List<OrderItem> oldOrderItemList = oldCart.getOrderItemList(); //指定角标的老车购物车购物项集合

                        List<OrderItem> newOrderItemList = newcart.getOrderItemList();
                        for (OrderItem newOrderItem : newOrderItemList) {              //取出新车购物项
                            int index2 = oldOrderItemList.indexOf(newOrderItem);
                            //判断是否有当前款
                            if (-1 != index2) {
                                //追加数量
                                OrderItem oldOrderItem = oldOrderItemList.get(index2);
                                oldOrderItem.setNum(oldOrderItem.getNum() + newOrderItem.getNum());

                            } else {
                                //追加当前款
                                oldOrderItemList.add(newOrderItem);
                            }

                        }

                    } else {
                        //追加购物车
                        oldCartList.add(newcart);
                    }
                }
            }

        } else {
            return newCartList;
        }
        return oldCartList;
    }

    @Override
    public List<Cart> findCartListFromRedis(String name) {

        return (List<Cart>) redisTemplate.boundHashOps("cartList").get(name);
    }

}
