package cn.itcast.core.service;

import cn.itcast.core.mapper.good.GoodsDao;
import cn.itcast.core.mapper.good.GoodsDescDao;
import cn.itcast.core.mapper.item.ItemCatDao;
import cn.itcast.core.mapper.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import com.alibaba.dubbo.config.annotation.Service;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.io.FilenameUtils.getPath;

@Service
public class StaticPageServiceImpl implements StaticPageService,ServletContextAware {
    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer; //spring容器bean标签中配置
    @Autowired
    private GoodsDao goodsDao;
    @Autowired
    private GoodsDescDao goodsDescDao;
    @Autowired
    private ItemDao itemDao;
    @Autowired
    private ItemCatDao itemCatDao;



    @Override
    public void index(Long id) {
        //bean标签,有目录地址,这里不需要,固定解决路径问题
        Configuration conf = freeMarkerConfigurer.getConfiguration();
        //输出路径
        String allPath = getPath("/" + id + ".html");
        //数据
        Map<String, Object> root = new HashMap<>();

        //商品详细
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        root.put("goodsDesc", goodsDesc);

        //商品对象
        Goods goods = goodsDao.selectByPrimaryKey(id);
        root.put("goods", goods);

        //库存对象
        ItemQuery query=new ItemQuery();
        query.createCriteria().andGoodsIdEqualTo(id).andStatusEqualTo("1");
        List<Item> items = itemDao.selectByExample(query);
        root.put("itemList",items );

        //分类
        root.put("itemCat1", itemCatDao.selectByPrimaryKey(goods.getCategory1Id()).getName());
        root.put("itemCat2", itemCatDao.selectByPrimaryKey(goods.getCategory2Id()).getName());
        root.put("itemCat3", itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName());


        //读写一致,解决乱码问题
        Writer out = null;
        try {
            //读
            Template template = conf.getTemplate("item.ftl");
            //写
            out = new OutputStreamWriter(new FileOutputStream(new File(allPath)), "UTF-8");
            template.process(root, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    //获取路径
    public String getPath(String path){
        //是从当前servlet 在tomcat 中的存放文件夹开始计算起的,存在虚拟部署项目target下
        return servletContext.getRealPath(path);
    }

    //不能使用request.getContextPath,/request.getSession.getServiceContext.getContextPath
    //定时器内部方法调用,没有request,走页面才有
    private ServletContext servletContext;
    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext=servletContext;
    }
}
