package com.baidu.myshop.service;

import com.baidu.myshop.pojo.Address;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface AddressService extends IService<Address> {

    /**
     * 根据用户ID查询该用户的所有收货地址
     * @param userId 用户ID
     * @return 地址列表
     */
    List<Address> getAddressesByUserId(Integer userId);

    /**
     * 根据用户ID和关键词查询该用户的收货地址
     * @param userId 用户ID
     * @param keyword 关键词 (可为null或空，表示不进行关键词过滤)
     * @return 地址列表
     */
    List<Address> getAddressesByUserId(Integer userId, String keyword);

    /**
     * 添加新的收货地址，并处理默认地址逻辑
     * @param address 地址信息
     * @return 是否添加成功
     */
    boolean addAddress(Address address);

    /**
     * 更新收货地址，并处理默认地址逻辑
     * @param address 地址信息
     * @return 是否更新成功
     */
    boolean updateAddress(Address address);

    /**
     * 设置为默认地址
     * @param userId 用户ID
     * @param addressId 要设置为默认的地址ID
     * @return 是否设置成功
     */
    boolean setDefaultAddress(Integer userId, Integer addressId);

    /**
     * 删除收货地址
     * @param userId 用户ID，确保用户只能删除自己的地址
     * @param addressId 地址ID
     * @return 是否删除成功
     */
    boolean deleteAddress(Integer userId, Integer addressId);
} 