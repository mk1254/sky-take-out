package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 */

@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    RedisTemplate redisTemplate;

  @Autowired
    private DishService dishService;

    /**
     * 清理缓存数据
     * @param pattern
     */
  private void clearDishCache(String pattern) {
      Set keys = redisTemplate.keys(pattern);
      redisTemplate.delete(keys);

  }



    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
@ApiOperation("新增菜品")
public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品:{}",dishDTO);
        dishService.save(dishDTO);
        //清理缓存数据
String key="dish_"+dishDTO.getCategoryId();
    clearDishCache(key);
    return Result.success();
}

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
 @ApiOperation("菜品分页查询")
@GetMapping("/page")
 public Result<PageResult> pageList(DishPageQueryDTO dishPageQueryDTO){
     log.info("菜品分页查询,{}",dishPageQueryDTO);
      PageResult l=  dishService.page(dishPageQueryDTO);

        return Result.success(l);
 }

    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
 @DeleteMapping
 @ApiOperation("菜品批量删除")
 public  Result delete(@RequestParam List<Long> ids){
log.info("菜品批量删除:{}",ids);
dishService.deleteBatch(ids);
//清理缓存数据(所有的)
String key="dish_*";
clearDishCache(key);

     return Result.success();
 }

    /**
     * 根据id查询菜品和口味
     * @param id
     * @return
     */
 @ApiOperation("根据id查询菜品")
 @GetMapping("/{id}")
 public Result<DishVO> getById(@PathVariable Long id){
     log.info("根据id查询菜品:{}",id);

    DishVO dishVo= dishService.getByIdWithFlavor(id);
     return Result.success(dishVo);
 }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
 @PutMapping
 @ApiOperation("修改菜品")
 public Result update(@RequestBody DishDTO dishDTO){
     log.info("修改菜品:{}",dishDTO);
     dishService.updateWithFlavor(dishDTO);
     //清理缓存数据(所有的)
     String key="dish_*";
     clearDishCache(key);
     return Result.success();
 }

    /**
     * 修改菜品的状态
     * @param status
     * @param id
     * @return
     */
    @ApiOperation("修改菜品的状态")
    @PostMapping("/status/{status}")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("修改菜品的状态:{}，{}",status,id);
    dishService.startOrStop(status,id);
        //清理缓存数据(所有的)
        String key="dish_*";
        clearDishCache(key);

        return Result.success();
    }

    /**
     * 根据分类id查询菜品
     *
     * @return
     */
    @ApiOperation("根据分类id查询菜品")
    @GetMapping("/list")
    public Result<List<Dish>> getByCategoryId(Long categoryId)
    {
        List<Dish> l=dishService.getBycategoryId(categoryId);
        return Result.success(l);
    }


}
