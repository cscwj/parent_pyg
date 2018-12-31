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

    @Override
    public void setDefault(Long id,String name) {
        if (id != null){
            List<Address> addressList = findListByLoginUser(name);
            for (Address address : addressList) {
                    address.setIsDefault("0");
                    addressDao.updateByPrimaryKeySelective(address);
            }
            Address address = addressDao.selectByPrimaryKey(id);
            address.setIsDefault("1");
            addressDao.updateByPrimaryKeySelective(address);
        }
    }

    @Override
    public void delete(Long id) {
        if (id!=null){
            addressDao.deleteByPrimaryKey(id);
        }
    }

    @Override
    public void add(Address address) {
        if (address!=null){
            addressDao.insertSelective(address);
        }
    }
}
