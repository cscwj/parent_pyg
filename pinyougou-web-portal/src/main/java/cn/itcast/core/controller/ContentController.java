package cn.itcast.core.controller;

import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.service.ContentService;
import com.alibaba.dubbo.config.annotation.Reference;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;

    //初始查询轮播图
    @RequestMapping("/findByCategoryId")
    public List<Content> findByCategoryId(Long categoryId,HttpServletRequest request){
        Integer lineCount = (Integer) request.getSession().getServletContext().getAttribute("lineCount");
        System.out.println(lineCount);
        return contentService.findByCategoryId(categoryId);
    }

    /**
     * 获取在线人数
     * @param request
     * @return
     */
    @RequestMapping("/savecount")
    public String savecount(HttpServletRequest request){

        Integer lineCount = (Integer) request.getSession().getServletContext().getAttribute("lineCount");
        //保存在线人数到缓存中
        contentService.savecount(lineCount);
        return String.valueOf(lineCount);
    }

}
