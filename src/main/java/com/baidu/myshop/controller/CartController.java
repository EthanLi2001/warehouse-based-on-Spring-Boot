package com.baidu.myshop.controller;


import com.baidu.myshop.pojo.Cart;
import com.baidu.myshop.pojo.User;
import com.baidu.myshop.service.CartService;
import com.baidu.myshop.service.ProductService;
import com.baidu.myshop.service.UserService;
import com.baidu.myshop.utils.AjaxResult;
import com.baidu.myshop.utils.PageResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 * 购物车管理表 前端控制器
 * </p>
 *
 * @author xiaoye
 * @since 2025-05-18
 */
@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private UserService  userService;
    @Autowired
    private ProductService productService;

    /**
     * 跳转到当前用户的购物车列表页面
     * (此方法与原来的toList功能上可能重叠，但语义上更清晰指向用户个人购物车视图)
     * @return
     */
    @RequestMapping("/myList")
    public String toMyList(){
        return "/cart/list";
    }

    /**
     * 获取当前登录用户的购物车项 (分页)
     * @param currentPage 当前页
     * @param pageSize 页面容量
     * @param session HttpSession
     * @return
     */
    @RequestMapping("/getMyCartItems")
    @ResponseBody
    public PageResult<Cart> getMyCartItems(
            @RequestParam(value = "page",defaultValue = "1") Integer currentPage,
            @RequestParam(value = "limit",defaultValue = "5") Integer pageSize,
            HttpSession session){
        
        User loggedInUser = (User) session.getAttribute("user2");
        
        if (loggedInUser == null) {
            // 用户未登录，返回空或错误提示
            PageResult<Cart> emptyResult = new PageResult<>();
            emptyResult.setCode(1); // 自定义错误码，表示需要登录
            emptyResult.setMsg("用户未登录，请先登录后再查看购物车。");
            emptyResult.setCount(0L);
            emptyResult.setData(java.util.Collections.emptyList());
            return emptyResult;
        }
        
        return cartService.getCurrentUserCart(loggedInUser.getUid(), currentPage, pageSize);
    }

    /**
     * 查询所有购物车信息
     * @param cid 购物车ID,用于模糊查询
     * @param currentPage 当前页
     * @param pageSize 页面容量
     * @return
     */
    @RequestMapping("/getPage")
    @ResponseBody
    public PageResult<Cart> getPage(
            @RequestParam(value="cid",defaultValue = "") String cid,
            @RequestParam(value = "page",defaultValue = "1") Integer currentPage,
            @RequestParam(value = "limit",defaultValue = "5") Integer pageSize){

        // 执行查询所有购物车信息
        PageResult<Cart> pageResult = cartService.getAllCart(cid, currentPage, pageSize);
        return pageResult;
    }

    /**
     * 批量删除
     * @param cids
     * @return
     */
    @RequestMapping("/batchDel")
    @ResponseBody
    public AjaxResult batchDel(@RequestParam("cid[]") Integer[] cids) {
        int total = cartService.deleteCartByIds(cids);
        if (total == cids.length) {
            // 删除成功
            return AjaxResult.right();
        } else {
            // 删除失败
            return AjaxResult.error();
        }
    }

    /**
     * 根据id删除
     * @param cid
     * @return
     */
    @RequestMapping("/doDel/{cid}")
    @ResponseBody
    public AjaxResult doDel(@PathVariable Integer cid){
        int total = cartService.deleteCartById(cid);
        if (total == 1){
            return AjaxResult.right();
        }else {
            return AjaxResult.error();
        }
    }

    /**
     * 跳转到购物车添加页面
     * @return
     */
    @RequestMapping("/toAdd")
    public String toAdd(HttpServletRequest request, HttpSession session){
        User loggedInUser = (User) session.getAttribute("user2");
        String role = (String) session.getAttribute("role");

        if (loggedInUser != null) {
            request.setAttribute("currentUser", loggedInUser);
            request.setAttribute("currentUserRole", role);
        }

        // Only load all users if the current user is an admin
        if ("admin".equals(role)) {
            request.setAttribute("users", userService.list());
        }
        // Products are needed for everyone
        request.setAttribute("products", productService.list());
        return "/cart/add";
    }

    /**
     * 添加购物车
     * @param cart
     * @param session
     * @return
     */
    @RequestMapping("/doAdd")
    @ResponseBody
    public AjaxResult doAdd(Cart cart, HttpSession session){
        User loggedInUser = (User) session.getAttribute("user2");
        String role = (String) session.getAttribute("role");

        if (loggedInUser == null) {
            return AjaxResult.error("用户未登录，请先登录。");
        }

        if ("user".equals(role)) {
            // If normal user, force the cart's uid to be the logged-in user's id
            cart.setUid(loggedInUser.getUid());
        } else if ("admin".equals(role)) {
            // If admin, they must specify the uid.
            if (cart.getUid() == null) {
                 return AjaxResult.error("管理员操作时，请选择用户。");
            }
        } else {
            // Unknown role or no role
            return AjaxResult.error("无法识别用户角色或权限不足。");
        }

        // Basic validation for product and quantity
        if (cart.getPid() == null) {
            return AjaxResult.error("请选择商品。");
        }
        if (cart.getQuantity() == null || cart.getQuantity() <= 0) {
            return AjaxResult.error("购买数量必须大于0。");
        }
        
        try {
            boolean success = cartService.addItemToCart(cart); // Call new service method
            if (success) {
                return AjaxResult.right("商品已成功添加到购物车。");
            } else {
                // This part might be tricky if addItemToCart throws exceptions for specific failures
                // rather than returning false for all non-exception failures.
                return AjaxResult.error("添加到购物车失败，请重试。");
            }
        } catch (IllegalArgumentException e) {
            // Log e.getMessage() or e
            return AjaxResult.error(e.getMessage()); // Specific error from service
        }
         catch (Exception e) {
            // Log e
            // Consider logging e.getMessage() or a generic message for unexpected errors
            return AjaxResult.error("添加到购物车时发生未知错误。");
        }
    }

    /**
     * 根据id查询数据 --- 回显
     * @param cid
     * @param request
     * @return
     */
    @RequestMapping("/toEdit/{cid}")
    public String toEdit(@PathVariable("cid") Integer cid, HttpServletRequest request, HttpSession session){
        User loggedInUser = (User) session.getAttribute("user2");
        String role = (String) session.getAttribute("role");

        Cart cart = cartService.getCartById(cid);
        if (cart == null) {
            request.setAttribute("errorMessage", "未找到对应的购物车条目。");
            request.setAttribute("editCart", null);
            return "cart/edit"; 
        }

        // Security check for normal users
        if ("user".equals(role) && loggedInUser != null) {
            if (!cart.getUid().equals(loggedInUser.getUid())) {
                request.setAttribute("errorMessage", "您无权修改此购物车条目。");
                request.setAttribute("editCart", null); 
                 return "cart/edit";
            }
        }
        
        // Fetch and set product name
        if (cart.getPid() != null) {
            com.baidu.myshop.pojo.Product product = productService.getById(cart.getPid());
            if (product != null) {
                cart.setProdName(product.getPname());
            } else {
                // Product not found, maybe set a default or log an error
                cart.setProdName("商品信息未找到");
            }
        }

        request.setAttribute("editCart", cart);
        request.setAttribute("currentUser", loggedInUser);
        request.setAttribute("currentUserRole", role);

        if ("admin".equals(role)) {
        request.setAttribute("users", userService.list());
        }
        return "cart/edit";
    }

    /**
     * 处理执行修改操作的请求
     * @param cid
     * @param quantity
     * @param session // Added HttpSession
     * @return
     */
    @RequestMapping("/doEdit")
    @ResponseBody
    public AjaxResult doEdit(@RequestParam Integer cid, @RequestParam Integer quantity, HttpSession session){ // Added HttpSession
        User loggedInUser = (User) session.getAttribute("user2");
        String role = (String) session.getAttribute("role");

        if (loggedInUser == null) {
            return AjaxResult.error("用户未登录或会话已过期。");
        }

        if (quantity == null || quantity <= 0) {
            return AjaxResult.error("购买数量必须大于0。");
        }

        Cart existingCartItem = cartService.getCartById(cid);
        if (existingCartItem == null) {
            return AjaxResult.error("要修改的购物车条目不存在。");
        }

        // Security check for normal users
        if ("user".equals(role)) {
            if (!existingCartItem.getUid().equals(loggedInUser.getUid())) {
                return AjaxResult.error("您无权修改此购物车条目。");
            }
        }
        // Admins can edit any cart item's quantity, so no specific UID check for them here,
        // but it's assumed they are acting on a valid cid.

        // Update only the quantity
        Cart cartToUpdate = new Cart();
        cartToUpdate.setCid(cid);
        cartToUpdate.setQuantity(quantity);
        // UID and PID are not changed during an edit of quantity.
        // If the service's updateCart method is smart enough to only update non-null fields 
        // or specifically targets quantity, this is fine.
        // Otherwise, ensure existingCartItem's UID and PID are preserved if they get overwritten.
        // For safety, the service method `updateCart` should ideally only update quantity based on CID.

        int total = cartService.updateCart(cartToUpdate); 
        if (total == 1) {
            return AjaxResult.right("购物车商品数量已更新。");
        } else {
            return AjaxResult.error("更新失败，请重试。");
        }
    }


}
