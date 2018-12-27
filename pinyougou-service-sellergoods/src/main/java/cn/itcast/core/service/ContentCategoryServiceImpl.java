package cn.itcast.core.service;

import cn.itcast.core.mapper.ad.ContentCategoryDao;
import cn.itcast.core.pojo.ad.ContentCategory;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
	
	@Autowired
	private ContentCategoryDao contentCategoryDao;

	@Override
	public List<ContentCategory> findAll() {
		List<ContentCategory> list = contentCategoryDao.selectByExample(null);
		return list;
	}

	@Override
	public PageResult findPage(ContentCategory contentCategory, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<ContentCategory> page = (Page<ContentCategory>)contentCategoryDao.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void add(ContentCategory contentCategory) {
		contentCategoryDao.insertSelective(contentCategory);
	}

	@Override
	public void edit(ContentCategory contentCategory) {
		contentCategoryDao.updateByPrimaryKeySelective(contentCategory);
	}

	@Override
	public ContentCategory findOne(Long id) {
		ContentCategory category = contentCategoryDao.selectByPrimaryKey(id);
		return category;
	}

	@Override
	public void delAll(Long[] ids) {
		if(ids != null){
			for(Long id : ids){
				contentCategoryDao.deleteByPrimaryKey(id);
			}
		}
		
	}

}
