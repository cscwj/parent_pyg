package cn.itcast.core.controller;

import cn.itcast.core.pojo.Cart;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.service.CartService;
import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    /* @CrossOrigin(origins = {"http://localhost:9003"},allowCredentials = "true")*/
    @CrossOrigin(origins = {"http://localhost:9003"}) //默认允许带个人信息
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {
        try {
            //创建购物车集合
            List<Cart> cartList = null;

            //1.获取Cookie数组
            //2.从Cookie数组获取购物车
            Cookie[] cookies = request.getCookies();
            if (null != cookies && cookies.length > 0) {
                for (Cookie cookie : cookies) {
                    if ("CART".equals(cookie.getName())) {
                        String cartValue = cookie.getValue();
                        cartList = JSON.parseArray(cartValue, Cart.class);
                        break;  //查询到跳出,优化

                    }
                }
            }
            //3.没有创建购物车
            if (null == cartList) {
                cartList = new ArrayList<>();
            }

            //4.追加当前款排除嵌套循序方法,另辟新径-->创建新购物车只包含商家id,库存id,数量也就确定了那个购物车,订单项数据
            Cart newCart = new Cart();
            Item item = cartService.findItemById(itemId);
            newCart.setSellerId(item.getSellerId());

            //创建商品结果集对象
            List<OrderItem> orderItemLits = new ArrayList<>();

            OrderItem orderItem = new OrderItem();
            orderItem.setNum(num);
            orderItem.setItemId(itemId);
            orderItemLits.add(orderItem);
            //创建新车完成
            newCart.setOrderItemList(orderItemLits);
//----------------------------------------------------------------------------------------------------

            //4.追加当前款
            //1.判断购物车是否存在有此商品的商家        //根据商家id,SellerId重写equals和HashCode,来比较是否存在商家购物车,也就判断是否存在商家
            int index1 = cartList.indexOf(newCart);   //判断对象是否存在list集合中,存在返回角标,不存在返回-1
            if (-1 != index1) {
                //-有  判断此商家是否存在该商品
                Cart oldCart = cartList.get(index1);
                List<OrderItem> orderItemList = oldCart.getOrderItemList();
                int index2 = orderItemList.indexOf(orderItem);//上方新车中仅有的一条购物项
                if (-1 != index2) {
                    //--有  追加数量
                    OrderItem oldOrderItem = orderItemList.get(index2);
                    oldOrderItem.setNum(oldOrderItem.getNum() + orderItem.getNum());

                } else {
                    //--没有追加订单
                    orderItemList.add(orderItem);
                }

            } else {
                //-没有  追加新的购物车
                cartList.add(newCart);

            }
//----------------------------------------------------------------------------------------------------
            //1.判断是否登录 配置了匿名登录,未登录显示 anonymousUser  为了防止出现空指针异常
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!"anonymousUser".equals(name)) {
                //登录
                //5.将当前购物车合并到Redis中
                cartService.merge(cartList, name);

                //6.清空cookie
                Cookie cookie = new Cookie("CART", null);
                cookie.setMaxAge(0);
                cookie.setPath("/");
                response.addCookie(cookie);

            } else {
                //未登录
                //5.将购物车添回Cookie
                String cartListString = JSON.toJSONString(cartList);
                Cookie cookie = new Cookie("CART", cartListString);
                cookie.setMaxAge(60 * 60 * 12 * 3);
                cookie.setPath("/");
                //cookie.setDomain("jd.com");   设置一级域名,允许在二级域名下跨域访问
                //http://www.jd.com/cart/addGoodsToCart.do
                //http://search.jd.com/shopping/addGoodsToCart.do

                //6.回写浏览器
                response.addCookie(cookie);
            }

            return new Result(true, "添加成功");
        } catch (Exception e) {
            return new Result(false, "添加失败");
        }

    }

    //查询购物车结果集
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request, HttpServletResponse response) {
        List<Cart> cartList = null;

        //1.获取Cookie数组
        //2.从Cookie数组获取购物车
        Cookie[] cookies = request.getCookies();
        if (null != cookies && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if ("CART".equals(cookie.getName())) {
                    cartList = JSON.parseArray(cookie.getValue(), Cart.class);
                }

            }
        }

        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!"anonymousUser".equals(name)) {
            //登录
            //3.将cookie中购物车合并到redis中,并清除cookie-->可能添加物品是,突然登录,导致cookie有值
            cartService.merge(cartList, name);

            Cookie cookie = new Cookie("CART", null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
            response.addCookie(cookie);

            //5.从redis中查询购物车
            cartList=cartService.findCartListFromRedis(name);

        }
        //未登录,和登录都需要走这一步

        //5.有-->将购物车装满,回显
        if (null != cartList && cartList.size() > 0) {   //判断防止直接点购物车出现空指针
            cartList = cartService.findCartList(cartList);
        }

        return cartList;
    }
}
