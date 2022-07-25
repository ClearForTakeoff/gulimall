package com.gulimall.gulicart.controller;

import com.gulimall.gulicart.interceptor.CartInterceptor;
import com.gulimall.gulicart.service.CartService;
import com.gulimall.gulicart.vo.Cart;
import com.gulimall.gulicart.vo.CartItem;
import com.gulimall.gulicart.vo.UserInfo;
import io.swagger.models.auth.In;
import lombok.experimental.PackagePrivate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.common.constant.AuthConstant.LOGIN_USER;

/**
 * @Author: duhang
 * @Date: 2022/7/4
 * @Description:
 **/
@Controller
public class CartController {
    @Autowired
    CartService cartService;


    //获取选中的购物项
    @GetMapping("/getCheckedCartItem")
    @ResponseBody
    public List<CartItem> getCheckedCartItem(){
        return cartService.getCheckedCartItem();
    }

    //用户没有登陆时，要创建一个临时用户，放回到cookie中
    //以后每次再访问都使用临时用户的cookie
    //跳转到购物车页面
    @GetMapping("/cart.html")
    public String cartListPage( Model model) throws ExecutionException, InterruptedException {
        //从threadlocal获取用户信息
        //获取购物车
        Cart cart = cartService.getCart();
        model.addAttribute("cart",cart);
        return "cartList";
    }


    //添加购物车
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId")Long skuId,
                            @RequestParam("skuCount")int count,
                            Model model,
                            RedirectAttributes redirectAttributes) throws ExecutionException, InterruptedException {
        CartItem cartItem = cartService.addToCart(skuId,count);
        redirectAttributes.addAttribute("skuId",skuId); //重定向携带数据
        return "redirect:http://cart.gulimall.com/addToCartSuccess";
    }

    //跳转到成功页
    @GetMapping("/addToCartSuccess")
    public String addToCartSuccess(@RequestParam("skuId")Long skuId,Model model){
        //重定向过来,再查一次加入购物车的商品，再放到model中
        CartItem cartItem = cartService.getAddCartItem(skuId);
        model.addAttribute("skuInfo",cartItem);
        return "success";
    }

    //选中购物车商品
    @GetMapping("/checkedItem")
    public String checkItem(@RequestParam("skuId")Long skuId, @RequestParam("check")Integer check){
        cartService.checkItem(skuId,check);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    //修改购物车商品数量
    @GetMapping("/editCartItemCOunt")
    public String editCartItemCount(@RequestParam("skuId")Long skuId,@RequestParam("count")Integer count){
        cartService.editCartItemCount(skuId,count);
        //重定向回本页面
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    //删除购物车item
    @GetMapping("/deleteItem")
    public String deleteItem(@RequestParam("skuId")Long skuId){
        cartService.deleteItem(skuId);
        //重定向回本页面
        return "redirect:http://cart.gulimall.com/cart.html";
    }
}
