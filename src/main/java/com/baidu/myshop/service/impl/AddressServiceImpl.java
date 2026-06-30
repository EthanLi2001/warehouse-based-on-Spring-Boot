package com.baidu.myshop.service.impl;

import com.baidu.myshop.dao.AddressMapper;
import com.baidu.myshop.pojo.Address;
import com.baidu.myshop.service.AddressService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, Address> implements AddressService {

    @Autowired
    private AddressMapper addressMapper;

    @Override
    public List<Address> getAddressesByUserId(Integer userId) {
        return getAddressesByUserId(userId, null);
    }

    public List<Address> getAddressesByUserId(Integer userId, String keyword) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", userId);

        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.trim();
            queryWrapper.and(qw -> qw.like("consigneeName", kw)
                                     .or().like("phoneNumber", kw)
                                     .or().like("province", kw)
                                     .or().like("city", kw)
                                     .or().like("district", kw)
                                     .or().like("detailedAddress", kw));
        }

        queryWrapper.orderByDesc("isDefault").orderByDesc("updateTime");
        return addressMapper.selectList(queryWrapper);
    }

    @Transactional
    @Override
    public boolean addAddress(Address address) {
        address.setCreateTime(new Date());
        address.setUpdateTime(new Date());
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            // 如果将此地址设为默认，则将该用户其他地址设为非默认
            clearDefaultAddress(address.getUid());
        }
        return addressMapper.insert(address) > 0;
    }

    @Transactional
    @Override
    public boolean updateAddress(Address address) {
        address.setUpdateTime(new Date());
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            // 如果将此地址设为默认，则将该用户其他地址设为非默认
            clearDefaultAddress(address.getUid());
        }
        return addressMapper.updateById(address) > 0;
    }

    @Transactional
    @Override
    public boolean setDefaultAddress(Integer userId, Integer addressId) {
        // 1. 将该用户所有地址设为非默认
        clearDefaultAddress(userId);
        // 2. 将指定地址设为默认
        Address address = new Address();
        address.setAddressId(addressId);
        address.setIsDefault(1);
        address.setUpdateTime(new Date());
        return addressMapper.update(address, new UpdateWrapper<Address>().eq("addressId", addressId).eq("uid", userId)) > 0;
    }

    @Override
    public boolean deleteAddress(Integer userId, Integer addressId) {
        QueryWrapper<Address> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uid", userId).eq("addressId", addressId);
        return addressMapper.delete(queryWrapper) > 0;
    }
    
    /**
     * 将指定用户的所有地址设置为非默认
     * @param userId 用户ID
     */
    private void clearDefaultAddress(Integer userId) {
        Address nonDefaultAddress = new Address();
        nonDefaultAddress.setIsDefault(0);
        nonDefaultAddress.setUpdateTime(new Date());
        addressMapper.update(nonDefaultAddress, new UpdateWrapper<Address>().eq("uid", userId).eq("isDefault", 1));
    }
} 