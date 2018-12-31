package cn.itcast.core.controller;

import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.TypeTemplateService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;


    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody(required = false) TypeTemplate template){
        return  typeTemplateService.search(page,rows,template);

    }

    //保存
    @RequestMapping("/add")
    public Result add(@RequestBody TypeTemplate template){
        try {
            typeTemplateService.add(template);
            return new Result(true,"保存成功");
        } catch (Exception e) {
            return new Result(false,"保存失败");
        }
    }

    //修改数据-回显
    @RequestMapping("/findOne")
    public TypeTemplate findOne(Long id){
        return typeTemplateService.findOne(id);
    }

    //修改数据-保存
    @RequestMapping("/update")
    public Result uptade(@RequestBody TypeTemplate template){
        try {
            typeTemplateService.update(template);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            return new Result(false,"修改失败");
        }
    }
    //更新状态   审核通过  或驳回
    @RequestMapping("updateStatus")
    public Result updateStatus(Long[] ids, Integer status) {
        try {
            typeTemplateService.updateStatus(ids, status);
            return new Result(true, "操作成功!");

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "操作失败!");
        }
    }
}

