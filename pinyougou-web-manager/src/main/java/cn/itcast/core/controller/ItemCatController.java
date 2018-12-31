package cn.itcast.core.controller;

import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.service.ItemCatService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.Result;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

//产品分类

@RestController
@Transactional
@RequestMapping("/itemCat")
public class ItemCatController {


    @Reference
    private ItemCatService itemCatService;

    //查询产品管理,3级分类,查看下一级使用的都是同一个方法,数据表示一张自关联表,
    //数据页面,不采用分页,数据少,可以页面初始化id为0,查询所有一级数据显示
    @RequestMapping("/findByParentId")
    public List<ItemCat> findByParentId(Long parentId) {
        return itemCatService.findByParentId(parentId);
    }

    //新建
    @RequestMapping("/add")
    public Result add(@RequestBody ItemCat itemCat){
        try {
            itemCatService.add(itemCat);
            return new Result(true,"保存成功");
        } catch (Exception e) {
            return new Result(false,"保存失败");
        }
    }

    //修改-回显
    @RequestMapping("/findOne")
    public ItemCat findOne(Long id){
       return itemCatService.findOne(id);
    }


    //修改-保存
    @RequestMapping("/update")
    public Result update(@RequestBody ItemCat itemCat){
        try {
            itemCatService.update(itemCat);
            return new Result(true,"保存成功");
        } catch (Exception e) {
            return new Result(false,"保存失败");
        }
    }
    //查询所有,用于前端放在数组,根据id显示对应分类
    @RequestMapping("/findAll")
    public List<ItemCat> findAll() {
        return itemCatService.findAll();
    }
    //更新状态   审核通过  或驳回
    @RequestMapping("updateStatus")
    public Result updateStatus(Long[] ids, Integer status) {
        try {
            itemCatService.updateStatus(ids, status);
            return new Result(true, "操作成功!");

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "操作失败!");
        }
    }

}
