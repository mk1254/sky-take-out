package com.sky.mapper;



import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper

public interface CategoryMapper {
    /**
     * 新增分类
     * @param category
     */
    @Insert("insert into category(type, name, sort, status, create_time, update_time, create_user, update_user)" +
            "VALUES (#{type}, #{name}, #{sort}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
   @AutoFill(value = OperationType.INSERT)
    void insert(Category category);

    /**
     * 分类分页
     * @param categoryPageQueryDTO
     * @return
     */
    Page<Category> pageSelect(CategoryPageQueryDTO categoryPageQueryDTO);

    /**
     * 分类修改
     * @param c
     */
    @AutoFill(value = OperationType.UPDATE)
    void update(Category c);

    /**
     * 根据id删除分类
     * @param id
     */
    @Delete("delete from category where id=#{id}")
    void delete(Long id);

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    List<Category> list(Integer type);
}
