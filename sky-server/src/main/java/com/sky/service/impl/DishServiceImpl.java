package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
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
    private SetmealDishMapper setmealDishMapper;
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

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        //DTO接收前端（属性比实体类少）  VO传给前端（属性比实体类多）
      Page<DishVO> p= dishMapper.pageSelect(dishPageQueryDTO);

        return new PageResult(p.getTotal(),p.getResult());
    }

    /**
     * 菜品批量删除
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否存在起售中;是否被套餐关联
        for (Long id : ids) {
          Dish dish=  dishMapper.getById(id);
          if(dish.getStatus()== StatusConstant.ENABLE){
              //起售中
              throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
          }
        }

        List<Long> setmealIds = setmealDishMapper.getSetmealIdsByDishIds(ids);
        if(setmealIds !=null && setmealIds.size()>0){
            //被套餐关联
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

    //删除菜品表的菜品数据
       /* for (Long id : ids) {
            dishMapper.deleteById(id);
            //删除菜品表的口味数据
            dishFlavorMapper.deleteById(id);

        }*/
       //dishMapper.deleteById(id);
        dishMapper.deleteByIds(ids);
        //dishFlavorMapper.deleteById(id);
        dishFlavorMapper.deleteByIds(ids);



    }

    /**
     * 根据id查询菜品和口味
     * @param id
     * @return
     */
    public DishVO getByIdWithFlavor(Long id) {
        //根据id查菜品
        Dish dish = dishMapper.getById(id);
        //根据菜品id查口味
      List<DishFlavor> f=  dishFlavorMapper.getByDishId(id);

      DishVO dv=new DishVO();
      BeanUtils.copyProperties(dish,dv);
      dv.setFlavors(f);

        return dv;
    }

    /**
     * 修改菜品和口味
     * @param dishDTO
     */
    public void updateWithFlavor(DishDTO dishDTO) {
        //修改菜品表基本信息
        Dish dish=new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.update(dish);
        //修改口味
        List<DishFlavor> l=dishDTO.getFlavors();
        if (l != null && l.size() > 0) {

            //删除口味
            dishFlavorMapper.deleteById(dishDTO.getId());
            //重新插入
               //将dishId赋值给每一个口味
            l.forEach(s -> {
                s.setDishId(dishDTO.getId());
            });

            dishFlavorMapper.insert(l);
        }
    }


}
