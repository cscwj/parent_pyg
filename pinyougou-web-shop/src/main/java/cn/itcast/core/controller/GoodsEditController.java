package cn.itcast.core.controller;

import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.service.GoodsService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pojogroup.GoodsVo;

@RestController
@RequestMapping("/goods")
public class GoodsEditController {

    @Reference
    private GoodsService goodsService;

    //保存3表操作,复杂,对数据库,看那些字段需要额外查询
    @RequestMapping("/add")
    public Result add(@RequestBody GoodsVo vo){
        try {
            //需要公司id,是一个字符串,登录用户名
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            vo.getGoods().setSellerId(name);

            goodsService.add(vo);
            return new Result(true,"保存成功");
        } catch (Exception e) {
            return new Result(false,"保存失败");

        }

    }

    //条件查询
    @RequestMapping("/search")
    public PageResult search(Integer page,Integer rows ,@RequestBody  Goods goods){
        //获取当前公司登陆ID
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        goods.setSellerId(name);
        return goodsService.search(page,rows,goods);
    }

    //修改-回显
    @RequestMapping("/findOne")
    public GoodsVo findOne(Long id){
        return goodsService.findOne(id);
    }

    //修改-保存
    @RequestMapping("/update")
    public Result update(@RequestBody GoodsVo vo){
        try {
            goodsService.update(vo);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            return new Result(false,"修改失败");
        }
    }


}
