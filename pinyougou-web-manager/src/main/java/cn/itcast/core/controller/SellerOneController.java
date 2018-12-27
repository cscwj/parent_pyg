package cn.itcast.core.controller;

import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.SellerOneService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



//商家管理-审核商家
@RestController
@RequestMapping("/seller")
public class SellerOneController {

    @Reference
    private SellerOneService sellerOneService;

    //条件分页

    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody Seller seller){
       return sellerOneService.search(page,rows,seller);
    }

    //详情-回显
    @RequestMapping("/findOne")
    public Seller findOne(String id){
        return sellerOneService.findOne(id);
    }

    //详情-审核状态
    @RequestMapping("/updateStatus")
    public Result updateStatus (Seller seller){
        try {
            sellerOneService.updateStatus(seller);
            return new Result(true,"审核成功");
        } catch (Exception e) {
            return new Result(true,"审核失败");
        }
    }



}
