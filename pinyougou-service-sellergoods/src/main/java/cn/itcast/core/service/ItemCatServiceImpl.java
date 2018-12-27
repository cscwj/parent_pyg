package cn.itcast.core.service;

import cn.itcast.core.mapper.item.ItemCatDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ItemCatServiceImpl implements ItemCatService {

    @Autowired
    private ItemCatDao itemCatDao;
    @Autowired
    private RedisTemplate redisTemplate; //common工程

    @Override
    public List<ItemCat> findByParentId(Long id) {
        //查询所有分类,缓存一份ridis,用于前台页面分类
        List<ItemCat> items = findAll();
        for (ItemCat item : items) {
            redisTemplate.boundHashOps("itemCat").put(item.getName(),item.getTypeId() );

        }

        ItemCatQuery query = new ItemCatQuery();
        query.createCriteria().andParentIdEqualTo(id);

        return itemCatDao.selectByExample(query);
    }

    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);
    }

    @Override
    public List<ItemCat> findAll() {
        return itemCatDao.selectByExample(null);
    }

    //保存
    @Override
    public void add(ItemCat itemCat) {

        itemCatDao.insertSelective(itemCat);
    }

    @Override
    public void update(ItemCat itemCat) {
        itemCatDao.updateByPrimaryKeySelective(itemCat);
    }
}
