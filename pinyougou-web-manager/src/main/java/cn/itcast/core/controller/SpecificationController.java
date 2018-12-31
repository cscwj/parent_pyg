package cn.itcast.core.controller;

import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.service.SpecificationService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pojogroup.SpecificationVo;

import java.util.List;
import java.util.Map;

@RestController   //记得返回前台josn,不然404
@RequestMapping("/specification")
public class SpecificationController {

    //规格分页
    @Reference
    private SpecificationService specificationService;

    @RequestMapping("/search")
    public PageResult search(Integer page, Integer rows, @RequestBody Specification specification) {
        return specificationService.search(page, rows, specification);
    }

    //规格保存
    @RequestMapping("/add")
    public Result add(@RequestBody SpecificationVo vo) {
        try {
            specificationService.add(vo);
            return new Result(true, "保存成功");
        } catch (Exception e) {
            return new Result(true, "保存失败");
        }
    }

    //修改规格-数据回显
    @RequestMapping("/findOne")
    public SpecificationVo findOne(Long id) {
        return specificationService.findOne(id);
    }

    //修改数据-保存  多表关联修改,先删除主外键,在添加
    @RequestMapping("/update")
    public Result update(@RequestBody SpecificationVo vo) {
        try {
            specificationService.update(vo);
            return new Result(true, "修改成功");
        } catch (Exception e) {
            return new Result(false, "修改失败");
        }
    }

    //模板管理的加载规格
    @RequestMapping("selectOptionList")
    public List<Map> selectOptionList() {
        return specificationService.selectOptionList();
    }

    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            specificationService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            return new Result(false,"删除失败");
        }

    }

    //更新状态   审核通过  或驳回
    @RequestMapping("updateStatus")
    public Result updateStatus(Long[] ids, Integer status) {
        try {
            specificationService.updateStatus(ids, status);
            return new Result(true, "操作成功!");

        } catch (Exception e) {
            e.printStackTrace();
            return new Result(true, "操作失败!");
        }
    }
}


