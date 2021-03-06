package cn.itcast.core.service;

import cn.itcast.core.pojo.good.Goods;
import entity.PageResult;
import entity.Result;
import pojogroup.GoodsVo;

public interface GoodsService {
    void add(GoodsVo vo);

    PageResult search(Integer page, Integer rows, Goods goods);

    GoodsVo findOne(Long id);

    void update(GoodsVo vo);

    void updateStatus(Long[] ids, String status);

    void delete(Long[] ids);

    void upjia(Long[] ids);

    void downjia(Long[] ids);
}
