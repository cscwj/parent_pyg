package cn.itcast.core.controller;

import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

//商品管理-新增商品,页面调用运营商后台管理的产品分类方法
//6级联动,页面使用监听器实现
@RestController
@RequestMapping("/typeTemplate")
public class GoodsEditController_TempTemplate {

    @Reference
    private TypeTemplateService typeTemplateService;

    //监听模板id的变化,查询出模板数据,联动出品牌和扩展属性
    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
        return typeTemplateService.findOne(id);
    }

    //map相当于pojo,pojo中封装list
    //6级联动,使用的上监听的模板id,显示模板中规格,规格选择后,笛卡尔积形式
    //纵向遍历,和横向遍历,返回参数是List<map>,map中嵌套list集合    例如  颜色 : 红 黄 蓝   list中map1  id=  text=颜色  map1.pust(k,{红 黄 蓝})
    @RequestMapping("/findBySpecList")                      //  大小 : 大 中 小        map2  id=  text=大小
    public List<Map> findBySpecList(Long id){
        return typeTemplateService.findBySpecList(id);
    }


}
