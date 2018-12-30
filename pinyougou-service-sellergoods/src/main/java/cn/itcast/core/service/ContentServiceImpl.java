package cn.itcast.core.service;

import cn.itcast.core.mapper.ad.ContentDao;
import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class ContentServiceImpl implements ContentService {

    @Autowired
    private ContentDao contentDao;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public List<Content> findAll() {
        List<Content> list = contentDao.selectByExample(null);
        return list;
    }

    @Override
    public PageResult findPage(Content content, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<Content> page = (Page<Content>) contentDao.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void add(Content content) {
        contentDao.insertSelective(content);
    }

    //修改-广告-新建-删除也需要考虑缓存问题,比这个逻辑简单,TODO
    //@Transactional,redis没有事务,这是@Transactional的极限了,多数据库需要分布式事务
    //@Transactional,Spring不支持事务  利用Mysql的事务 来完成事务管理    Mysql因为默认的InnoDB引擎支持事务,所以也支持事务
    //之前 先进入切面 对象(DataSourceTransactionManager) 注入dataSource 连接Mysql   begin transation  开启事务
    @Override
    public void edit(Content content) {
        //最先查询,查询语句不考虑事务,不能保存后查询
        //修改广告可能修改图片,但也可能修改位置,这时需要根据主键id(不会变化),查询出分类表id,也就是自己从键,看看位置变化与否
        Content oldContent = contentDao.selectByPrimaryKey(content.getId());

        //这里抛异常立即回滚,下面没有删除,下面抛异常这里未提交,下面同一连接操作,前一个成功,后一个肯定成功,不考虑第三个抛异常,第二个无法回滚
        contentDao.updateByPrimaryKeySelective(content);

        //位置变化,需要删除以前分类下的缓存广告
        if (oldContent.getCategoryId() != content.getCategoryId()) {
            redisTemplate.boundHashOps("content").delete(oldContent.getCategoryId());
        }
        //位置是否发生变化都需要删除,当前位置的缓存
        redisTemplate.boundHashOps("content").delete(content.getCategoryId());


        //mysql不能放在这里,前面抛出异常,立即回滚没问题,一旦这里抛出异常,前面删除了缓存,这里没有更新
        //contentDao.updateByPrimaryKeySelective(content);
        //程序一旦抛出异常 再次进入切面 对象(DataSourceTransactionManager) 注入dataSource 连接Mysql   rootback  提交事务


        //优化:修改完调用查询语句
        findByCategoryId(oldContent.getCategoryId());
        findByCategoryId(content.getCategoryId());

    }
    //之后 再次进入切面 对象(DataSourceTransactionManager) 注入dataSource 连接Mysql   commit  提交事务

    @Override
    public Content findOne(Long id) {
        Content content = contentDao.selectByPrimaryKey(id);
        return content;
    }

    @Override
    public void delAll(Long[] ids) {
        if (ids != null) {
            for (Long id : ids) {
                contentDao.deleteByPrimaryKey(id);
            }
        }
    }


    //根据外键 查询轮播图  放入hash可以存所有广告,字符串需要创建多个
    @Override
    public List<Content> findByCategoryId(Long categoryId) {
        //查询缓存
        List<Content> contents = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);

        //没有 查询Mysql数据,保存缓存一份
        if (contents == null || contents.size() == 0) {
            ContentQuery contentQuery = new ContentQuery();
            contentQuery.createCriteria().andCategoryIdEqualTo(categoryId);
            contentQuery.setOrderByClause("sort_order desc");

            contents = contentDao.selectByExample(contentQuery);
            redisTemplate.boundHashOps("content").put(categoryId, contents);
            //设置存活时间,用于换广告 1.设置缓存时间 2.定时器
            redisTemplate.boundHashOps("content").expire(1, TimeUnit.DAYS);

        }
        //有 直接返回
        return contents;

    }

    /**
     * 保存在线人数到缓存中
     * @param lineCount
     */
    @Override
    public void savecount(Integer lineCount) {
        redisTemplate.boundValueOps("lineCount").set(lineCount);
    }

    /**
     * 设置一段时间内用户登录次数 - - - > 活跃用户
     */
    Integer count = 1;
    @Override
    public void activeUser(String name) {
        if (null==redisTemplate.boundHashOps("ActiveUser").get(name)){
            //第一次登录
            redisTemplate.boundHashOps("ActiveUser").put(name,count);
        }else {
            //
            count = (Integer) redisTemplate.boundHashOps("ActiveUser").get(name);
            count++;
            redisTemplate.boundHashOps("ActiveUser").put(name,count);
        }
        //设置存活时间 7 天   - - > 测试用3分钟
        redisTemplate.boundHashOps("ActiveUser").expire(7,TimeUnit.DAYS);
    }

}
