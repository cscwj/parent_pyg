package cn.itcast.core.service;

import cn.itcast.core.pojo.item.ItemCat;

import java.util.List;

public interface ItemCatService {
    List<ItemCat> findByParentId(Long id);

    ItemCat findOne(Long id);

    List<ItemCat> findAll();

    void add(ItemCat itemCat);

    void update(ItemCat itemCat);

    void updateStatus(Long[] ids, Integer status);

    void delete(Long[] ids);
}
