package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.AttrVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 商品属性
 *
 * @author zyk
 * @email kaishao@atguigu.com
 * @date 2019-12-02 17:51:54
 */
public interface AttrService extends IService<AttrEntity> {

    PageVo queryPage(QueryCondition params);
    //通过catid（所属分类的id）查找属性信息
    PageVo queryAttrByCatId(QueryCondition condition, Long catId, Integer type);
    //新增属性时，若为基本属性则维护中间表信息，添加分组和属性的关联
    void saveAttrVO(AttrVO attr);
}

