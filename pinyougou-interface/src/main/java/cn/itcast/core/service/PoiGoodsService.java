package cn.itcast.core.service;

//Created by Wang on itCast.  

import cn.itcast.core.pojo.good.Goods;

import java.util.List;

/**
 * @author Wang
 * @version 1.0
 * -description lianxi
 * -date        2018-12-30--15:20 @_@ ~~
 */

public interface PoiGoodsService {
  List<Goods> selectList(Goods goods);
}
