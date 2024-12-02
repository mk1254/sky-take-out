package com.sky.controller.admin;


import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@Api("分类管理接口")
@RequestMapping("/admin/category")
public class CategoryController {
@Autowired
CategoryService categoryService;

    /**
     * 新增分类
     * @return
     */
    @ApiOperation("新增分类")
@PostMapping
 public Result add(@RequestBody CategoryDTO categoryDTO){
log.info("新增分类:{}",categoryDTO);
       categoryService.add(categoryDTO);
        return Result.success();
 }

    /**
     * 分类的分页查询
     * @return
     */
 @ApiOperation("分类的分页查询")
 @GetMapping("/page")
 public Result<PageResult> categoryPage(CategoryPageQueryDTO categoryPageQueryDTO){//Query 参数不用@RequestBody，就能直接封装到实体类中
   log.info("分类的分页查询：{}",categoryPageQueryDTO);

     PageResult pr=   categoryService.page(categoryPageQueryDTO);

        return Result.success(pr);
 }

    /**
     * 启用、禁用分类
     * @param status
     * @param id
     * @return
     */
 @ApiOperation("启用、禁用分类")
 @PostMapping("/status/{status}")
 public Result StartOrStop(@PathVariable Integer status,Long id){
log.info("启用、禁用分类:{},{}",status,id);

categoryService.startOrStop(status,id);


     return Result.success();
 }

    /**
     * 修改分类信息
     * @return
     */
    @ApiOperation("修改分类信息")
 @PutMapping
 public Result update(@RequestBody CategoryDTO categoryDTO){
log.info("修改分类信息,{}",categoryDTO);
categoryService.update(categoryDTO);

return Result.success();
 }

    /**
     * 根据id删除分类
     * @param id
     * @return
     */
 @ApiOperation("根据id删除分类")
@DeleteMapping
 public Result delete(Long id){

    categoryService.delete(id);
        return Result.success();
 }

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
 @ApiOperation("根据类型查询分类")
@GetMapping("/list")
 public Result<List<Category>> list(Integer type){
  List<Category> l=  categoryService.list(type);

     return Result.success(l);
 }


}
