package com.sky.controller.admin;


import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Api(tags = "套餐相关接口")
@RequestMapping("/admin/setmeal")
public class SetmealController {
    @Autowired
    SetmealService setmealService;

    /**
     * 添加套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation("添加套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO) {
        log.info("添加套餐");
       setmealService.save(setmealDTO);
        return Result.success();
    }

    /**
     * 分页查询
     * @return
     */
    @ApiOperation("分页查询")
    @GetMapping("/page")
    public Result<PageResult> page(SetmealPageQueryDTO pageQueryDTO){
    PageResult p=   setmealService.page(pageQueryDTO);

        return Result.success(p);
    }

    /**
     * 删除套餐
     * @return
     */
    @ApiOperation("删除套餐")
    @DeleteMapping
    public Result delete(@RequestParam List<Long> ids){

        setmealService.delete(ids);
        return Result.success();
    }

    /**
     * 根据id查询套餐，用于修改页面回显数据
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id) {
        SetmealVO setmealVO = setmealService.getByIdWithDish(id);
        return Result.success(setmealVO);
    }


    /**
     * 修改套餐
     * @return
     */
    @PutMapping
    @ApiOperation("修改套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO){

    setmealService.update(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售停售")
    public Result startOrStop(@PathVariable Integer status, Long id) {
        setmealService.startOrStop(status, id);
        return Result.success();
    }

}
