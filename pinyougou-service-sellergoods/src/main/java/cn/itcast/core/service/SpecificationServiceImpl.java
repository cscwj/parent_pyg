package cn.itcast.core.service;

import cn.itcast.core.mapper.specification.SpecificationDao;
import cn.itcast.core.mapper.specification.SpecificationOptionDao;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import pojogroup.SpecificationVo;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SpecificationServiceImpl implements SpecificationService{

    @Autowired
    private SpecificationDao specificationDao;

    @Autowired
    private SpecificationOptionDao specificationOptionDao;

    @Override
    public PageResult search(Integer page, Integer rows, Specification specification) {
        PageHelper.startPage(page ,rows);

        SpecificationQuery specificationQuery = new SpecificationQuery();
        SpecificationQuery.Criteria criteria = specificationQuery.createCriteria();


            if (null!=specification.getSpecName() && !"".equals(specification.getSpecName().trim())){
                criteria.andSpecNameLike("%"+specification.getSpecName().trim()+"%");
            }

        //模糊名字查询

        Page<Specification> specifications = (Page<Specification>) specificationDao.selectByExample(specificationQuery);


        return new PageResult(specifications.getTotal(),specifications.getResult());
    }

    @Override
    public void add(SpecificationVo vo) {
        //规格表主键 ,手写sql,回显id
        specificationDao.insertSelective(vo.getSpecification());
        //规格选项结果集 外键
        List<SpecificationOption> specificationOptions = vo.getSpecificationOptionList();
        for (SpecificationOption specificationOption : specificationOptions) {
            //外键
            specificationOption.setSpecId(vo.getSpecification().getId());
            //保存
            specificationOptionDao.insertSelective(specificationOption);
        }

    }

    @Override
    public SpecificationVo findOne(Long id) {
        SpecificationVo vo=new SpecificationVo();
        //主表数据
        Specification specification = specificationDao.selectByPrimaryKey(id);
        vo.setSpecification(specification);
        //从表结果集
        SpecificationOptionQuery query=new SpecificationOptionQuery();
        //条件主键=从键
        query.createCriteria().andSpecIdEqualTo(id);
        List<SpecificationOption> specificationOptions = specificationOptionDao.selectByExample(query);
        vo.setSpecificationOptionList(specificationOptions);

        return vo;
    }

    @Override
    public void update(SpecificationVo vo) {
        //修改主表数据 回显是带上了所有数据,所以有从表用到的主键id,根据主键修改不为null的数据
        specificationDao.updateByPrimaryKeySelective(vo.getSpecification());

        //修改结果结果集(先删除,在保存)
        //1.删除(不能根据主键删除,只能使用重载方法,自己写条件)
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        //SpecId=主键的值
        query.createCriteria().andSpecIdEqualTo(vo.getSpecification().getId());
        specificationOptionDao.deleteByExample(query);

        //保存
        List<SpecificationOption> optionList = vo.getSpecificationOptionList();
        for (SpecificationOption option : optionList) {
            //添加主键(删除的话,因为没有新增,数据回显回来是其实带以前id关联,新增就不行了,必须添加)
            option.setSpecId(vo.getSpecification().getId());
            specificationOptionDao.insertSelective(option);
        }

    }

    @Override
    public List<Map> selectOptionList() {
        return specificationDao.selectOptionList();
    }


}
