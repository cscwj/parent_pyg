package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.service.ItemCatService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pojogroup.GoodsVo;

import java.util.List;

//商品管理-新增商品,页面调用运营商后台管理的产品分类方法
//6级联动,页面使用监听器实现
@RestController
@RequestMapping("/itemCat")
public class GoodsEditController_TtemCat {

    @Reference
    private ItemCatService itemCatService;

    //123级联动
    //进页面body标签中,初始化方法,而方法中调用itemCatService,父id为0,加载一级数据,页面使用angularjs遍历方法.
    //2,3级分类使用监听器,监听上一级分类主键id变化,传入变化的新主键,当做父id,继续执行此方法查询
    @RequestMapping("/findByParentId")
    public List<ItemCat> findByParentId(Long parentId){
        return itemCatService.findByParentId(parentId);
    }

    //监听3级分类id变化,传入id,查询出自己表中的type_id,也就是关联模板的id,只有模板才知道分类对应的品牌
    @RequestMapping("/findOne")
    public ItemCat findOne(Long id){
        return itemCatService.findOne(id);
    }

    //查询所有,用于前端放在数组,根据id显示对应分类
    @RequestMapping("/findAll")
    public List<ItemCat> findAll(){
        return itemCatService.findAll();
    }

}
