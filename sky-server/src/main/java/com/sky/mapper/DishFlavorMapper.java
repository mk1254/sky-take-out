package com.sky.mapper;


import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper

public interface DishFlavorMapper {


    /**
     * 插入口味数据
     * @param flavors
     */

    void insert(List<DishFlavor> flavors);

}
