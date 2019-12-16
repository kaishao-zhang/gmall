package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.IndexVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 商品三级分类
 *
 * @author zyk
 * @email kaishao@atguigu.com
 * @date 2019-12-02 17:51:54
 */
public interface CategoryService extends IService<CategoryEntity> {

    PageVo queryPage(QueryCondition params);
    //查询分类维护的菜单信息
    List<CategoryEntity> queryCategory(Integer level, Long parentCid);
    //根据1级分类的id查询2,3级分类的信息
    List<IndexVO> querySubCategory(Long id);
}

