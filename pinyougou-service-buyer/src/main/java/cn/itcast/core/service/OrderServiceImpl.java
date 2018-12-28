package cn.itcast.core.service;

import cn.itcast.common.utils.IdWorker;
import cn.itcast.core.mapper.item.ItemDao;
import cn.itcast.core.mapper.log.PayLogDao;
import cn.itcast.core.mapper.order.OrderDao;
import cn.itcast.core.mapper.order.OrderItemDao;
import cn.itcast.core.pojo.Cart;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.log.PayLog;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.pojo.order.OrderItemQuery;
import cn.itcast.core.pojo.order.OrderQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import pojogroup.OrderVo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderItemDao orderItemDao;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private PayLogDao payLogDao;   //订单日志表,合并每个商家的订单

    @Override
    public void add(Order order) {
        //cart只存在商家id,库存id,购买数量
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("cartList").get(order.getUserId());

        //总金额
        double tp = 0;
        //每个商家的订单id
        List<String> idList = new ArrayList<>();


        //order页面上添加了收款人,地址,controller添加了登录用户名
        if (null != cartList && cartList.size() > 0) {
            for (Cart cart : cartList) {
                //1:保存订单表
                //2:订单ID 分布式ID生成器
                long orderId = idWorker.nextId();
                order.setOrderId(orderId);

                //合并商家id,到订单日志
                idList.add(String.valueOf(orderId));

                //3.实付金额
                double totalPrice = 0;
                //4:支付类型
                order.setPaymentType("1");
                //5:支付状态  未
                order.setStatus("1");
                //创建时间
                order.setCreateTime(new Date());
                //更新时间
                order.setUpdateTime(new Date());
                //订单来源
                order.setSourceType("2");
                //商家ID-----------------------------------
                order.setSellerId(cart.getSellerId());

                List<OrderItem> orderItemList = cart.getOrderItemList();
                for (OrderItem orderItem : orderItemList) {
                    Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
                    //1:订单详情表
                    //2:订单详情表
                    long orderItemId = idWorker.nextId();
                    orderItem.setId(orderItemId);
                    //3:外键订单主表的ID
                    orderItem.setOrderId(orderId);
                    //4:商品ID
                    orderItem.setGoodsId(item.getGoodsId());
                    //5:标题
                    orderItem.setTitle(item.getTitle());
                    //6:单价
                    orderItem.setPrice(item.getPrice());
                    //7:数量 已经存在
                    //8:小计-->大类型转换
                    orderItem.setTotalFee(new BigDecimal(orderItem.getPrice().doubleValue() * orderItem.getNum()));

                    //当前商家商品的总金额
                    totalPrice += orderItem.getTotalFee().doubleValue();
                    //8:图片
                    orderItem.setPicPath(item.getImage());
                    //9:商家Id
                    orderItem.setSellerId(item.getSellerId());

                    //保存
                    orderItemDao.insertSelective(orderItem);

                }
                //设置总金额
                order.setPayment(new BigDecimal(totalPrice));
                //保存主表
                orderDao.insertSelective(order);

                //总金额
                tp += order.getPayment().doubleValue();


                //清空购物车
                redisTemplate.boundHashOps("cartList").delete(order.getUserId());
                //redisTemplate.boundHashOps("cartList").delete("","");-->用户名--商品id--商品,这样保存是不是可以选择性购买商品了

                //支付日志表 (多个订单合并成一个支付日志 目的是为了一次性付款)
                PayLog payLog = new PayLog();

                //1.支付订单号
                long logId = idWorker.nextId();
                payLog.setOutTradeNo(String.valueOf(logId));
                //2.创建日期
                payLog.setCreateTime(new Date());
                //3.支付金额（分）
                payLog.setTotalFee((long) (tp * 100));
                //4.用户ID
                payLog.setUserId(order.getUserId());
                //5.交易状态
                payLog.setTradeState("0");
                //订单编号列表 [212131413,1434343413213],去掉大括号
                payLog.setOrderList(idList.toString().replace("[", "").replace("]", ""));
                //支付类型:微信:1
                payLog.setPayType("1");

                //保存数据库
                payLogDao.insertSelective(payLog);
                //保存一份redis,微信支付调用会更快
                redisTemplate.boundHashOps("payLog").put(order.getUserId(), payLog);


            }


        }
    }

    @Override
    public PageResult search(Integer page, Integer rows, Order order) {
        //分页插件
        PageHelper.startPage(page, rows);
        OrderQuery orderQuery = new OrderQuery();
        OrderQuery.Criteria criteria = orderQuery.createCriteria();

        //商家和运营商共用查询,运营商后台是查询所有,不带当前登录id
        if (null != order.getSellerId() && !"".equals(order.getSellerId().trim())) {
            //查询当前登录公司商品,id,controller获取
            criteria.andSellerIdEqualTo(order.getSellerId().trim());
        }
        if (null != order.getOrderId()) {
            criteria.andOrderIdEqualTo(order.getOrderId());
        }

        //状态查询,未审核,审核,审未通过,前台下拉框,如果不选择(全部),是''
        if (null != order.getStatus() && !"".equals(order.getStatus())) {
            criteria.andStatusEqualTo(order.getStatus());
        }
//        //名字模糊查询 angularjs会去掉前后端空串,但是前段校验不安全,后台也必须校验
//        if (null != order.getGoodsName() && !"".equals(order.getGoodsName().trim())) {
//            criteria.andGoodsNameLike("%" + order.getGoodsName() + "%");
//        }
        //只查询不删除的,为null表示未删除
//        criteria.andIsDeleteIsNull();

        Page<Order> p = (Page<Order>) orderDao.selectByExample(orderQuery);

        List<Order> result = p.getResult();
        for (Order order1 : result) {
            System.out.println("++++ "+order1);
        }
        return new PageResult(p.getTotal(), p.getResult());

    }

    @Override
    public OrderVo findOne(Long id) {
        OrderVo orderVo = new OrderVo();
        Order order = orderDao.selectByPrimaryKey(id);
        OrderItemQuery orderItemQuery = new OrderItemQuery();
        OrderItemQuery.Criteria criteria = orderItemQuery.createCriteria();
        criteria.andOrderIdEqualTo(id);
        List<OrderItem> orderItems = orderItemDao.selectByExample(orderItemQuery);
        orderVo.setOrder(order);
        orderVo.setOrderItems(orderItems);
        return orderVo;
    }

    @Override
    public void update(OrderVo orderVo) {
        orderDao.updateByPrimaryKeySelective(orderVo.getOrder());
        OrderItemQuery orderItemQuery = new OrderItemQuery();
        OrderItemQuery.Criteria criteria = orderItemQuery.createCriteria();
        criteria.andOrderIdEqualTo(orderVo.getOrder().getOrderId());
        orderItemDao.deleteByExample(orderItemQuery);
        for (OrderItem orderItem : orderVo.getOrderItems()) {
            orderItemDao.insertSelective(orderItem);
        }
    }

}