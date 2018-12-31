package cn.itcast.core.service;

import cn.itcast.core.pojo.specification.Specification;
import entity.PageResult;
import pojogroup.SpecificationVo;

import java.util.List;
import java.util.Map;

public interface SpecificationService {

    PageResult search(Integer page, Integer rows, Specification specification);

    void add(SpecificationVo vo);

    SpecificationVo findOne(Long id);

    void update(SpecificationVo vo);

    List<Map> selectOptionList();

    void updateStatus(Long[] ids, Integer status);

    void delete(Long[] ids);
}
