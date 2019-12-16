package com.atguigu.gmall.index.service;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.IndexVO;

import java.util.List;

/**
 * @author 凯少
 * @create 2019-12-13 12:46
 */
public interface IndexService {

    //查询所有的1级分类信息
    List<CategoryEntity> queryLevl1Categroy();
    //查询一级分类下的二级和三级分类
    List<IndexVO> querySubCategory(Long id);
}
