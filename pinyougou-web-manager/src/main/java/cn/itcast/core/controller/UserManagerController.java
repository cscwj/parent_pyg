package cn.itcast.core.controller;

import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.UserManagerService;
import cn.itcast.core.service.UserService;
import com.alibaba.dubbo.config.annotation.Reference;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userManager")
public class UserManagerController {
    @Reference
    private UserManagerService userManagerService;

    //条件查询
    @RequestMapping("/search")
    public PageResult search(Integer pageNum, Integer pageSize , @RequestBody User user){
        PageResult search = userManagerService.search(pageNum, pageSize, user);
        return search;
    }
    //冻结
    @RequestMapping("/freeze")
    public Result freeze(Long[] ids){
        try {
            userManagerService.freeze(ids);
            return new Result(true,"冻结成功");
        } catch (Exception e) {
            return new Result(false,"冻结失败");
        }

    }
    //解冻
    @RequestMapping("/unfreeze")
    public Result unfreeze(Long[] ids){
        try {
            userManagerService.unfreeze(ids);
            return new Result(true,"成功");
        } catch (Exception e) {
            return new Result(false,"失败");
        }

    }

//    /**
//     * 用户个数
//     * @return
//     */
//    @RequestMapping("findUserNum")
//    public Integer findUserNum(){
//        return userManagerService.findUserNum();
//    }
//    /**
//     * 用户在线个数
//     * @return
//     */
//    @RequestMapping("findLineUserNum")
//    public Integer findLineUserNum(){
//        return userManagerService.findLineUserNum();
//    }

    @RequestMapping("findNums")
    public Integer[] findNums(){
        return userManagerService.findNums();
    }

}
