package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    SetmealMapper setmealMapper;
    @Autowired
    DishMapper dishMapper;

    @Autowired
  private   ShoppingCartMapper shoppingCartMapper;

    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    public void add(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();//获取用户ID
        shoppingCart.setUserId(userId);
        shoppingCart.setCreateTime(LocalDateTime.now());

        //判断当前加入到购物车的商品是否已经存在
        List<ShoppingCart> list = shoppingCartMapper.selectShoppingCartList(shoppingCart);
        if (list != null && list.size() > 0) {
            //如果已经存在，只需要数量加一
            ShoppingCart cart = list.get(0);//只会存在一条数据
            cart.setNumber(cart.getNumber() + 1);
            shoppingCartMapper.updateNumberById(cart);
        }else {//不存在，需要插入一条购物车数据

                 //判断添加到购物车的是菜品还是套餐
            Long dishId = shoppingCart.getDishId();

            if (dishId != null) {
                //菜品
                Dish dish = dishMapper.getById(dishId);

                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setNumber(1);

            } else {
                //套餐
                Long setmealId = shoppingCart.getSetmealId();

                Setmeal setmeal = setmealMapper.getById(setmealId);
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
                shoppingCart.setNumber(1);

            }

            shoppingCartMapper.insert(shoppingCart);

        }



    }

    /**
     * 查看购物车
     * @return
     */
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId();//获取当前用户id
        ShoppingCart cart = ShoppingCart.builder()
                                        .userId(userId)
                                        .build();

        List<ShoppingCart> list = shoppingCartMapper.selectShoppingCartList(cart);


        return list;
    }

    /**
     * 清空购物车
     */
    public void clean() {
        Long userId = BaseContext.getCurrentId();

        shoppingCartMapper.deleteByUserId(userId);


    }

    /**
     * 删除购物车中一个商品
     * @param shoppingCartDTO
     */
    public void delete(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //判断该商品是否只有一个
        List<ShoppingCart> list = shoppingCartMapper.selectShoppingCartList(shoppingCart);
        if(list.get(0).getNumber()==1) {
            //只有一个，直接删除
            shoppingCartMapper.delete(shoppingCart);
        }else {
            //有多个，数量减一
            shoppingCart.setNumber(list.get(0).getNumber()-1);
            shoppingCart.setId(list.get(0).getId());
            shoppingCartMapper.updateNumberById(shoppingCart);
        }




    }
}
