package com.baidu.myshop.controller;

import com.baidu.myshop.pojo.Customer;
import com.baidu.myshop.service.CustomerService;
import com.baidu.myshop.utils.AjaxResult;
import com.baidu.myshop.utils.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @GetMapping("/toList")
    public String toCustomerList() {
        return "customer/list";
    }

    @GetMapping("/toAdd")
    public String toAddCustomer() {
        return "customer/add";
    }

    @GetMapping("/toEdit/{customerId}")
    public String toEditCustomer(@PathVariable Integer customerId, Model model) {
        Customer customer = customerService.getCustomerById(customerId);
        model.addAttribute("customer", customer);
        return "customer/edit";
    }

    @PostMapping("/getPage")
    @ResponseBody
    public PageResult<Customer> getPage(String customerName, 
                                         @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer limit) {
        if (customerName != null && customerName.trim().isEmpty()) {
            customerName = null;
        }
        return customerService.getCustomerByPage(customerName, page, limit);
    }

    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addCustomer(@RequestBody Customer customer) {
        if (customerService.addCustomer(customer)) {
            return AjaxResult.right("客户添加成功");
        }
        return AjaxResult.error("客户添加失败");
    }

    @PostMapping("/update")
    @ResponseBody
    public AjaxResult updateCustomer(@RequestBody Customer customer) {
        if (customerService.updateCustomer(customer)) {
            return AjaxResult.right("客户更新成功");
        }
        return AjaxResult.error("客户更新失败");
    }

    @PostMapping("/delete/{customerId}")
    @ResponseBody
    public AjaxResult deleteCustomer(@PathVariable Integer customerId) {
        if (customerService.deleteCustomer(customerId)) {
            return AjaxResult.right("客户删除成功");
        }
        return AjaxResult.error("客户删除失败");
    }

    @PostMapping("/batchDel")
    @ResponseBody
    public AjaxResult batchDeleteCustomers(@RequestBody Map<String, List<Integer>> params) {
        List<Integer> customerIds = params.get("customerIds");
        if (customerIds == null || customerIds.isEmpty()) {
            return AjaxResult.error("请选择要删除的数据");
        }
        boolean success = customerService.removeByIds(customerIds);
        if (success) {
            return AjaxResult.right("批量删除成功");
        }
        return AjaxResult.error("批量删除失败");
    }

    @GetMapping("/get/{customerId}")
    @ResponseBody
    public AjaxResult getCustomerById(@PathVariable Integer customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        if (customer != null) {
            return AjaxResult.right().put("customer", customer);
        }
        return AjaxResult.error("客户不存在");
    }

    @GetMapping("/list")
    @ResponseBody
    public AjaxResult getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return AjaxResult.right().put("data", customers);
    }
}