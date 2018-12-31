package cn.itcast.core.service;

import cn.itcast.core.pojo.address.Address;

import java.util.List;

public interface AddressService {
    List<Address> findListByLoginUser(String name);

    void setDefault(Long id,String name);

    void delete(Long id);

    void add(Address address);
}
