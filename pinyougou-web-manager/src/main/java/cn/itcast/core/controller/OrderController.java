package cn.itcast.core.controller;


import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.OrderService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pojogroup.OrderVo;

import java.lang.reflect.Field;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @RequestMapping("/search")
    public PageResult search(Integer pageNum, Integer pageSize , @RequestBody Order order){

            PageResult search = orderService.search(pageNum, pageSize, order);
            List<Order> rows = search.getRows();
            for (Order row : rows) {
                System.out.println(row);
            }
        Field[] declaredFields = search.getRows().get(0).getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            System.out.println(declaredField+"="+declaredField.getGenericType());
        }
            return orderService.search(pageNum,pageSize,order);

    }


    @RequestMapping("/findOne")
    public OrderVo findOne(Long id){
        return orderService.findOne(id);
    }

    @RequestMapping("/update")
    public Result update(@RequestBody OrderVo orderVo){
        try {
            orderService.update(orderVo);
            return new Result(true,"更改订单成功");
        }catch (Exception e){
            e.printStackTrace();
            return new Result(false,"更改订单失败");
        }
    }

    @RequestMapping("/findSellerList")
    public List<Seller> findSellerList(){
        return orderService.findSellerList();
    }
}
