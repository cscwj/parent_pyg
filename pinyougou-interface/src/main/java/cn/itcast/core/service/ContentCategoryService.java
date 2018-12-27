package cn.itcast.core.service;

import cn.itcast.core.pojo.ad.ContentCategory;
import entity.PageResult;

import java.util.List;

public interface ContentCategoryService {

	public List<ContentCategory> findAll();
	
	public PageResult findPage(ContentCategory contentCategory, Integer pageNum, Integer pageSize);
	
	public void add(ContentCategory contentCategory);
	
	public void edit(ContentCategory contentCategory);
	
	public ContentCategory findOne(Long id);
	
	public void delAll(Long[] ids);
}
