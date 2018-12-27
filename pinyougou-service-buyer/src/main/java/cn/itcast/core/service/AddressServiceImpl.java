package cn.itcast.core.service;

import cn.itcast.core.mapper.address.AddressDao;
import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.address.AddressQuery;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressDao addressDao;

    @Override
    public List<Address> findListByLoginUser(String name) {
        AddressQuery query = new AddressQuery();
        query.createCriteria().andUserIdEqualTo(name);
        return addressDao.selectByExample(query);
    }
}
