package cn.itcast.core.service;

import cn.itcast.core.mapper.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.*;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Autowired
    private ItemDao itemDao;
    @Autowired
    private SolrTemplate solrTemplate;
    @Autowired
    private RedisTemplate redisTemplate;


    //开始搜索
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        //处理关键词,避免输入出现空格
        String replace = searchMap.get("keywords").replaceAll(" ", "");
        searchMap.put("keywords", replace);


        //1:结果集
        //2:总条数(分页手写,没有引入angularjs分页插件)
        Map<String, Object> map = searchHighlightPage(searchMap);

        //3:商品分类
        List<String> categoryList = searchCategoryByKeywords(searchMap);
        map.put("categoryList", categoryList);

        //例行公事,判断是否有分类
        if (null != categoryList && categoryList.size() != 0) {
            //第一个分类名
            String category = categoryList.get(0);
            Object typeId = redisTemplate.boundHashOps("itemCat").get(category);
            //4:品牌列表(缓存获取)
            List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
            //5:规格列表(缓存获取)
            List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);

            map.put("brandList", brandList);
            map.put("specList", specList);
        }


        return map;
    }

    // $scope.searchMap={'keywords':'','category':'','brand':'',
    // 'spec':{},'price':'','pageNo':1,'pageSize':40,'sort':'','sortField':''};

    //查询结果集  总条数
    public Map<String, Object> searchHighlightPage(Map<String, String> searchMap) {
        Map<String, Object> map = new HashMap<>();

        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        HighlightQuery query = new SimpleHighlightQuery(criteria);


        //TODO 过滤条件
        //商品分类
        if (null != searchMap.get("category") && !"".equals(searchMap.get("category").trim())) {
            FilterQuery filterQuery = new SimpleFilterQuery(
                    new Criteria("item_category").is(searchMap.get("category")));
            query.addFilterQuery(filterQuery);
        }

        //品牌
        if (null != searchMap.get("brand") && !"".equals(searchMap.get("brand").trim())) {
            FilterQuery filterQuery = new SimpleFilterQuery(
                    new Criteria("item_brand").is(searchMap.get("brand")));
            query.addFilterQuery(filterQuery);
        }

        //规格
        //"item_spec_网络": "联通3G"
        // "item_spec_机身内存": "16G"
        if (null != searchMap.get("spec") && !"".equals(searchMap.get("spec").trim())) {

            Map<String, String> specMap = JSON.parseObject(searchMap.get("spec").trim(), Map.class);
            Set<Map.Entry<String, String>> entries = specMap.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                FilterQuery filterQuery = new SimpleFilterQuery(
                        new Criteria("item_spec_" + entry.getKey()).is(entry.getValue()));
                query.addFilterQuery(filterQuery);
            }


        }


        //价格区间 0-500,3000-*
        if (null != searchMap.get("price") && !"".equals(searchMap.get("price").trim())) {
            String[] prices = searchMap.get("price").trim().split("-");
            FilterQuery filterQuery = null;
            if (searchMap.get("price").contains("*")) {
                filterQuery = new SimpleFilterQuery(
                        new Criteria("item_price").greaterThanEqual(prices[0]));
            } else {
                filterQuery = new SimpleFilterQuery(           //true,false 开闭区间
                        new Criteria("item_price").between(prices[0], prices[1], true, false));
            }

            query.addFilterQuery(filterQuery);
        }


        //TODO 排序
        if (null != searchMap.get("sortField") && !"".equals(searchMap.get("sortField").trim())) {
            Sort sortField = null;
            if ("DESC".equals(searchMap.get("sort"))) {
                sortField = new Sort(Sort.Direction.DESC, "item_" + searchMap.get("sortField").trim());
            } else {
                sortField = new Sort(Sort.Direction.ASC, "item_" + searchMap.get("sortField").trim());
            }
            query.addSort(sortField);
        }


        //TODO 分页
        String pageNo = searchMap.get("pageNo");
        String pageSize = searchMap.get("pageSize");
        //偏移量
        query.setOffset((Integer.parseInt(pageNo) - 1) * Integer.parseInt(pageSize));
        //每页数
        query.setRows(Integer.parseInt(pageSize));

        //TODO 开启高亮
        HighlightOptions options = new HighlightOptions();
        //需要高亮的域
        options.addField("item_title");
        //前缀
        options.setSimplePrefix("<span style='color:red'> ");
        //后缀
        options.setSimplePostfix("</span>");

        query.setHighlightOptions(options);

        //TODO 执行查询
        HighlightPage<Item> page = solrTemplate.queryForHighlightPage(query, Item.class);
        //page.getContent(),和page.getHighlighted()底层都是用的同一个list容器,添加的高亮不需要处理
        //高亮可能存在,也可能不出在,比如收搜店铺名,此时的高亮变不存在,要显示普通名

        //断点查询出高亮的所在位置
        List<HighlightEntry<Item>> highlighted = page.getHighlighted(); //长度是设置的40
        for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
            //普通名
            Item item = itemHighlightEntry.getEntity();
            //高亮集合(域加域内容),长度为1,因为只有一个域
            List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights();
            if (null != highlights && highlights.size() > 0) {
                HighlightEntry.Highlight highlight = highlights.get(0);
                //高亮域内容集合,长度为1
                List<String> snipplets = highlight.getSnipplets();
                String title = snipplets.get(0);
                //有高亮 换成高亮的  没有高亮 使用自己原来的名称
                item.setTitle(title);

            }
        }

        //总条数
        map.put("total", page.getTotalElements());
        //总页数,用于设计的分页逻辑使用
        map.put("totalPages", page.getTotalPages());

        //结果集
        map.put("rows", page.getContent());

        return map;
    }

    //通过关键词查询商品分类(分组方式),查询店铺分类才不会重复
    public List<String> searchCategoryByKeywords(Map<String, String> searchMap) {
        List<String> list = new ArrayList();

        Query query = new SimpleQuery("item_keywords:" + searchMap.get("keywords")); //一个条件,不必Criteria

        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category"); //group by item_category
        query.setGroupOptions(groupOptions);

        GroupPage<Item> items = solrTemplate.queryForGroupPage(query, Item.class);
        //断点查看结构,取值
        GroupResult<Item> categoryList = items.getGroupResult("item_category");//select item_category from
        Page<GroupEntry<Item>> groupEntries = categoryList.getGroupEntries();
        List<GroupEntry<Item>> content = groupEntries.getContent();
        for (GroupEntry<Item> itemGroupCategory : content) {
            list.add(itemGroupCategory.getGroupValue());
        }

        return list;
    }
}

