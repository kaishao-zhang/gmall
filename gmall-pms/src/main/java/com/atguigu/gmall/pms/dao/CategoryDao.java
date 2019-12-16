package com.atguigu.gmall.pms.dao;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.IndexVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品三级分类
 * 
 * @author zyk
 * @email kaishao@atguigu.com
 * @date 2019-12-02 17:51:54
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
    //根据1级分类的id查询所有的2,3级分类
    List<IndexVO> querySubCategory(Long id);
}
