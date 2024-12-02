package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Transactional //事物注解
    public void save(DishDTO dishDTO) {
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //向菜品表插入1条数据
         dishMapper.insert(dish);

         //获取insert语句生成的主键值
          Long dishId=dish.getId();

        //向口味表插入n条数据
        List<DishFlavor> flavors=dishDTO.getFlavors();
         if(flavors !=null && flavors.size()>0){

             //将dishId赋值给每一个口味
             flavors.forEach(s->{s.setDishId(dishId);});

             dishFlavorMapper.insert(flavors);
         }

    }
}
