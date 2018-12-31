package cn.itcast.core.service;

import cn.itcast.core.mapper.good.BrandDao;
import cn.itcast.core.mapper.good.GoodsDao;
import cn.itcast.core.mapper.good.GoodsDescDao;
import cn.itcast.core.mapper.item.ItemCatDao;
import cn.itcast.core.mapper.item.ItemDao;
import cn.itcast.core.mapper.seller.SellerDao;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.pojo.seller.Seller;
import com.alibaba.dubbo.common.serialize.support.dubbo.GenericObjectOutput;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.dubbo.container.page.PageHandler;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import entity.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleFacetQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;
import pojogroup.GoodsVo;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("ALL")//不让页面警告
@Service
@Transactional
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private ItemDao itemDao;

    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private SellerDao sellerDao;
    @Autowired
    private BrandDao brandDao;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private Destination topicPageAndSolrDestination; //用于删除索引库,生成静态页面
    @Autowired
    private Destination queueSolrDeleteDestination; //用于删除索引库


    @Override
    public void add(GoodsVo vo) {
        //商品表-->商品详细表-->商品明细(spu设计的库存结构集):一对一对多
        //保存商品表,需要添加回显id,用于商品详细表的主键id,商品明细的外键

        //商品默认未审核
        vo.getGoods().setAuditStatus("0");

        goodsDao.insertSelective(vo.getGoods());

        //保存商品详细表
        vo.getGoodsDesc().setGoodsId(vo.getGoods().getId());
        goodsDescDao.insertSelective(vo.getGoodsDesc());

        //判断是否启用规格,启用才有规格集
        if ("1".equals(vo.getGoods().getIsEnableSpec())) {
            //启用

            //保存商品明细表,对应数据库保存字段,没有的补全,主键自增长
            List<Item> itemList = vo.getItemList();
            for (Item item : itemList) {

                //标题=spu+规格 例如:oppoR7 256g 玫瑰金 全网通,中间空格隔开
                String title = vo.getGoods().getGoodsName();
                //规格,只需要k值{"机身内存":"16G","网络":"联通3G"},不知道规格多少,fastjson转成map遍历取出
                String spec = item.getSpec();
                Map<String, String> map = JSON.parseObject(spec, Map.class);
                Set<Map.Entry<String, String>> entries = map.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    title += " " + entry.getValue();
                    item.setTitle(title);
                }

                //获取商品信息详情的第一张图片,用于收搜商品显示
                String itemImages = vo.getGoodsDesc().getItemImages();
                List<Map> itemImage = JSON.parseArray(itemImages, Map.class);
                if (itemImage != null && itemImage.size() > 0) {
                    item.setImage((String) itemImage.get(0).get("url"));
                }

                //第三个分类的id
                item.setCategoryid(vo.getGoods().getCategory3Id());
                //第三个分类的名称
                ItemCat itemCat = itemCatDao.selectByPrimaryKey(vo.getGoods().getCategory3Id());
                item.setCategory(itemCat.getName());
                //时间
                item.setCreateTime(new Date());
                item.setUpdateTime(new Date());
                //商品表id,外键
                item.setGoodsId(vo.getGoods().getId());
                //商家登录id
                item.setSellerId(vo.getGoods().getSellerId());
                //商家名称
                Seller seller = sellerDao.selectByPrimaryKey(vo.getGoods().getSellerId());
                item.setSeller(seller.getName());
                //品牌名称
                Brand brand = brandDao.selectByPrimaryKey(vo.getGoods().getBrandId());
                item.setBrand(brand.getName());

                //库存保存
                itemDao.insertSelective(item);
            }
        } else {
            //不启用  可能会有默认值,手动设置
        }


    }

    @Override
    public PageResult search(Integer page, Integer rows, Goods goods) {
        //分页插件
        PageHelper.startPage(page, rows);
        //排序
        PageHelper.orderBy("id desc");

        GoodsQuery query = new GoodsQuery();
        GoodsQuery.Criteria criteria = query.createCriteria();

        //商家和运营商共用查询,运营商后台是查询所有,不带当前登录id
        if (null != goods.getSellerId()) {
            //查询当前登录公司商品,id,controller获取
            criteria.andSellerIdEqualTo(goods.getSellerId());
        }

        //状态查询,未审核,审核,审未通过,前台下拉框,如果不选择(全部),是''
        if (null != goods.getAuditStatus() && !"".equals(goods.getAuditStatus())) {
            criteria.andAuditStatusEqualTo(goods.getAuditStatus());
        }
        //名字模糊查询 angularjs会去掉前后端空串,但是前段校验不安全,后台也必须校验
        if (null != goods.getGoodsName() && !"".equals(goods.getGoodsName().trim())) {
            criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
        }
        //只查询不删除的,为null表示未删除
        criteria.andIsDeleteIsNull();

        Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(query);

        return new PageResult(p.getTotal(), p.getResult());

    }

    //修改回显3表
    @Override
    public GoodsVo findOne(Long id) {
        GoodsVo vo = new GoodsVo();
        vo.setGoods(goodsDao.selectByPrimaryKey(id));
        vo.setGoodsDesc(goodsDescDao.selectByPrimaryKey(id));

        ItemQuery query = new ItemQuery();
        query.createCriteria().andGoodsIdEqualTo(id);
        vo.setItemList(itemDao.selectByExample(query));

        return vo;
    }

    //修改保存
    @Override
    public void update(GoodsVo vo) {
        //1.保存商品,商品详情表,一对一
        goodsDao.updateByPrimaryKeySelective(vo.getGoods());
        goodsDescDao.updateByPrimaryKeySelective(vo.getGoodsDesc());

        //2.库存表一对多的结果集,先删除,在保存
        //删除
        ItemQuery query = new ItemQuery();
        query.createCriteria().andGoodsIdEqualTo(vo.getGoods().getId());
        itemDao.deleteByExample(query);

        //保存
        //判断是否启用规格,启用才有规格集
        if ("1".equals(vo.getGoods().getIsEnableSpec())) {
            //启用

            //保存商品明细表,对应数据库保存字段,没有的补全,主键自增长
            List<Item> itemList = vo.getItemList();
            for (Item item : itemList) {

                //标题=spu+规格 例如:oppo 256g 玫瑰金 全网通,中间空格隔开
                String title = vo.getGoods().getGoodsName();
                //规格,只需要k值{"机身内存":"16G","网络":"联通3G"},不知道规格多少,fastjson转成map遍历取出
                String spec = item.getSpec();
                Map<String, String> map = JSON.parseObject(spec, Map.class);
                Set<Map.Entry<String, String>> entries = map.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    title += " " + entry.getValue();
                    item.setTitle(title);
                }

                //获取商品信息详情的第一张图片,用于收搜商品显示
                String itemImages = vo.getGoodsDesc().getItemImages();
                List<Map> itemImage = JSON.parseArray(itemImages, Map.class);
                if (itemImage != null && itemImage.size() > 0) {
                    item.setImage((String) itemImage.get(0).get("url"));
                }

                //第三个分类的id
                item.setCategoryid(vo.getGoods().getCategory3Id());
                //第三个分类的名称
                ItemCat itemCat = itemCatDao.selectByPrimaryKey(vo.getGoods().getCategory3Id());
                item.setCategory(itemCat.getName());
                //时间
                item.setCreateTime(new Date());
                item.setUpdateTime(new Date());
                //商品表id
                item.setGoodsId(vo.getGoods().getId());
                //商家登录id,外键
                item.setSeller(vo.getGoods().getSellerId());
                //商家名称
                Seller seller = sellerDao.selectByPrimaryKey(vo.getGoods().getSellerId());
                item.setSeller(seller.getName());
                //品牌名称
                Brand brand = brandDao.selectByPrimaryKey(vo.getGoods().getBrandId());
                item.setBrand(brand.getName());

                //库存保存
                itemDao.insertSelective(item);
            }
        } else {
            //不启用  可能会有默认值,手动设置
        }

    }

    //运营商-商品审核-审核通过,未通过
    @Override
    public void updateStatus(Long[] ids, String status) {
        //根据主键修改
        Goods goods = new Goods();
        goods.setAuditStatus(status);
        for (Long id : ids) {
            goods.setId(id);
            goods.setIsMarketable("1");
            goodsDao.updateByPrimaryKeySelective(goods);

            //如果表全,会有审核通过上架状态,修改为别的状态,所以需要删除索引库,使用户查询不到,这里通过需要保存
            if ("1".equals(status)) {
                jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage(String.valueOf(id));
                    }
                });

            }
        }

    }

    //删除-修改状态,并不是真的删除,状态不为null就是删除
    @Override
    public void delete(Long[] ids) {
        Goods goods = new Goods();
        for (Long id : ids) {
            goods.setId(id);
            goods.setIsDelete("1");
            goodsDao.updateByPrimaryKeySelective(goods);

            jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage(String.valueOf(id));
                }
            });

        }

    }

    //商品上架
    @Override
    public void upjia(Long[] ids) {
        //遍历ids  循环查询商品是否可以上架
        for (Long id : ids) {
            Goods goods = goodsDao.selectByPrimaryKey(id);

            //取出审核状态
            String auditStatus = goods.getAuditStatus();
            //取出上下架状态
            String isMarketable = goods.getIsMarketable();

            //已经上架的不能上架
            if ("1".equals(isMarketable)){
                // 添加商品上架状态 1 未上架  2已上架 更改页面显示  做判断 已上架的商品不能上架 已下架的商品不能下架
                goods.setIsMarketable("2");
                goodsDao.updateByPrimaryKeySelective(goods);
                //审核通过  可以进行上架(1.生成页面  2.导入索引库)
                if ("1".equals(auditStatus)) {
                    jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(String.valueOf(id));
                        }
                    });

                }
            }else {
                throw new RuntimeException();
            }

        }
    }

    //商品下架
    @Override
    public void downjia(Long[] ids) {
        //遍历ids  循环查询商品是否可以下架
        for (Long id : ids) {
            Goods goods = goodsDao.selectByPrimaryKey(id);


            //取出审核状态
            String auditStatus = goods.getAuditStatus();
            //取出上下架状态
            String isMarketable = goods.getIsMarketable();

            //已经下架的不能下架
            if ("2".equals(isMarketable)){

                // 添加商品下架状态 1 未上架  2已上架 更改页面显示  做判断 已上架的商品不能上架 已下架的商品不能下架
                goods.setIsMarketable("1");
                goodsDao.updateByPrimaryKeySelective(goods);

                //审核通过并且在上架状态  可以进行下架(1.删除页面  2.删除索引库)
                if ("1".equals(auditStatus)) {
                    jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(String.valueOf(id));
                        }
                    });

                }
            }else {
                throw new RuntimeException();
            }

        }
    }
}
