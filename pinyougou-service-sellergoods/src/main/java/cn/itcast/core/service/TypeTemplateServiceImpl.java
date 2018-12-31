package cn.itcast.core.service;

import cn.itcast.core.mapper.specification.SpecificationOptionDao;
import cn.itcast.core.mapper.template.TypeTemplateDao;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Autowired
    private TypeTemplateDao typeTemplateDao;
    @Autowired
    private SpecificationOptionDao specificationOptionDao;
    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public PageResult search(Integer page, Integer rows, TypeTemplate template) {
        //查询所有模板集(实质需要品牌集,规格集),缓存一份redis,用于前台页面分类
        List<TypeTemplate> typeTemplates = typeTemplateDao.selectByExample(null);
        for (TypeTemplate typeTemplate : typeTemplates) {
            //品牌集
            //[{"id":41,"text":"奥义"},{"id":42,"text":"金啦啦"}]
            String brandIds = typeTemplate.getBrandIds();
            List<Map> maps = JSON.parseArray(brandIds, Map.class);
            redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(), maps);

            //规格集  排量:规格集   最终结果list<map<String,list<String> > >
            //[{"id":46,"text":"汽车排量"},{"id":44,"text":"汽车颜色"}]
            List<Map> specList = findBySpecList(typeTemplate.getId());
            redisTemplate.boundHashOps("specList").put(typeTemplate.getId(),specList );

        }


        //分页插件
        PageHelper.startPage(page, rows);
        TypeTemplateQuery query = new TypeTemplateQuery();

        //排序
        //PageHelper.orderBy("id desc");

        //逆向工程排序
        query.setOrderByClause("id desc");

        //名字模糊查询
        if (null != template.getName() && !"".equals(template.getName().trim())) {
            query.createCriteria().andNameLike("%" + template.getName().trim() + "%");
        }
        Page<TypeTemplate> p = (Page<TypeTemplate>) typeTemplateDao.selectByExample(query);

        return new PageResult(p.getTotal(), p.getResult());
    }

    @Override
    public void add(TypeTemplate template) {
        typeTemplateDao.insertSelective(template);
    }

    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }

    @Override
    public void update(TypeTemplate template) {
        typeTemplateDao.updateByPrimaryKey(template);
    }

    @Override
    public List<Map> findBySpecList(Long id) {
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        //规格,字符串类型存在模板中,转为map
        //例如:规格  [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        String specIds = typeTemplate.getSpecIds();
        //fastjson转换
        List<Map> listMap = JSON.parseArray(specIds, Map.class);

        for (Map map : listMap) {
            //K:id V:27  K:text V:网络 K:options V:list
            //根据id到规格选项表,查询结果集  map中对象不能直接转long,长整特殊,需要先int,在long
            SpecificationOptionQuery query = new SpecificationOptionQuery();  //不能放外边,对象一样,条件是拼接的
            query.createCriteria().andSpecIdEqualTo((long) (int) map.get("id"));

            List<SpecificationOption> options = specificationOptionDao.selectByExample(query);
            map.put("options", options);
        }

        return listMap;
    }

    @Override
    public void updateStatus(Long[] ids, Integer status) {
        TypeTemplate typeTemplate=new TypeTemplate();
        typeTemplate.setStatus(String.valueOf(status));
        for (Long id : ids) {
            typeTemplate.setId(id);
            typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
        }
    }
    @Override
    public void delete(Long[] ids) {
        TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
        //数组转集合 Arrays.asList(ids)                               andIdIn (adn id in )
        //品牌条件对象,brandQuery.createCriteria()相当动态sql的where标签(删除第一个and) 后面跟的方法是条件and..and
        typeTemplateQuery.createCriteria().andIdIn(Arrays.asList(ids));

        typeTemplateDao.deleteByExample(typeTemplateQuery);
    }
}
