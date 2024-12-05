package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

public interface SetmealService {

    /**
     * 添加套餐
     * @param setmealDTO
     */
    void save(SetmealDTO setmealDTO);

    /**
     * 分页查询
     * @param pageQueryDTO
     * @return
     */
    PageResult page(SetmealPageQueryDTO pageQueryDTO);

    /**
     * 删除套餐
     * @param ids
     */
    void delete(List<Long> ids);

    /**
     * 修改套餐
     * @param setmealDTO
     */
    void update(SetmealDTO setmealDTO);

    /**
     * * 根据id查询套餐和关联的菜品数据
     *      * @param id
     *      * @return
     */
    SetmealVO getByIdWithDish(Long id);

    /**
     * 套餐起售、停售
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);
}