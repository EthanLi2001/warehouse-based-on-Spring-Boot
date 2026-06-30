package com.baidu.myshop.service.impl;

import com.baidu.myshop.dao.CustomerMapper;
import com.baidu.myshop.pojo.Customer;
import com.baidu.myshop.service.CustomerService;
import com.baidu.myshop.utils.PageResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Override
    public PageResult<Customer> getCustomerByPage(String customerName, Integer page, Integer limit) {
        Page<Customer> pageParam = new Page<>(page, limit);
        QueryWrapper<Customer> wrapper = new QueryWrapper<>();
        
        if (customerName != null && !customerName.trim().isEmpty()) {
            wrapper.like("customerName", customerName.trim());
        }
        
        wrapper.orderByDesc("createTime");
        Page<Customer> resultPage = customerMapper.selectPage(pageParam, wrapper);
        
        PageResult<Customer> pageResult = new PageResult<>();
        pageResult.setCode(0);
        pageResult.setData(resultPage.getRecords());
        pageResult.setMsg("查询成功");
        pageResult.setCount(resultPage.getTotal());
        
        return pageResult;
    }

    @Override
    public boolean addCustomer(Customer customer) {
        customer.setCreateTime(new Date());
        customer.setUpdateTime(new Date());
        return customerMapper.insert(customer) > 0;
    }

    @Override
    public boolean updateCustomer(Customer customer) {
        customer.setUpdateTime(new Date());
        return customerMapper.updateById(customer) > 0;
    }

    @Override
    public boolean deleteCustomer(Integer customerId) {
        return customerMapper.deleteById(customerId) > 0;
    }

    @Override
    public Customer getCustomerById(Integer customerId) {
        return customerMapper.selectById(customerId);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerMapper.selectList(null);
    }
}