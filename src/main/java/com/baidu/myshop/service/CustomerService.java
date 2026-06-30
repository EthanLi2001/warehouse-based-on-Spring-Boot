package com.baidu.myshop.service;

import com.baidu.myshop.pojo.Customer;
import com.baidu.myshop.utils.PageResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CustomerService extends IService<Customer> {

    PageResult<Customer> getCustomerByPage(String customerName, Integer page, Integer limit);

    boolean addCustomer(Customer customer);

    boolean updateCustomer(Customer customer);

    boolean deleteCustomer(Integer customerId);

    Customer getCustomerById(Integer customerId);

    List<Customer> getAllCustomers();
}