package cn.itcast.core.controller;

import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.BrandService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@CrossOrigin//允许所有方法跨域访问
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    //查询所有
    @RequestMapping("/findAll")
    public List<Brand> findAll() {
        return brandService.findAll();
    }

    //分页
    @RequestMapping("/findPage")
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        PageResult pageResult = brandService.findPage(pageNum, pageSize);
        return pageResult;

    }

    //添加
    @RequestMapping("/add")
    public Result add(@RequestBody Brand brand) {
        try {
            brandService.add(brand);
            return new Result(true,"保存成功");
        } catch (Exception e) {
            return new Result(false,"保存失败");
        }

    }

    //修改数据回显
    @RequestMapping("/findOne")
    public Brand findOne(Long id){
        return brandService.findOne(id);
    }

    //修改数据
    @RequestMapping("/update")
    public Result update(@RequestBody Brand brand){
        try {
            brandService.update(brand);
            return new Result(true,"修改成功");
        } catch (Exception e) {
            return new Result(false,"修改失败");
        }

    }

    //删除数据
    @RequestMapping("/delete")
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true,"删除成功");
        } catch (Exception e) {
            return new Result(false,"删除失败");
        }

    }

    //收索带条件分页,无条件为null
    //分页 1,后台:@RequestBody(required = false)转对象,对象不能为空,默认是true,前台赋值:$scope.searchEntity={},保证对象不为空
    @RequestMapping("/search")
    public PageResult search(Integer pageNum, Integer pageSize,@RequestBody(required = false) Brand brand) {
        PageResult pageResult = brandService.search(pageNum, pageSize,brand);
        return pageResult;

    }

    //模板管理的加载品牌数据
    @RequestMapping("selectOptionList")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }


}
