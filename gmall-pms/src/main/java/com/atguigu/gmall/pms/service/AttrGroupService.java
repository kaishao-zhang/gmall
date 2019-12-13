package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.AttrGroupVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;

import java.util.List;


/**
 * 属性分组
 *
 * @author zyk
 * @email kaishao@atguigu.com
 * @date 2019-12-02 17:51:54
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageVo queryPage(QueryCondition params);
    //根据分组的id
    PageVo queryAttrGroupById(QueryCondition condition, Long catId);
    //根据分组的id查询一个AttrGroupController
    AttrGroupVO withAttrs(Long gid);
    //根据分类id查询查询所有的group信息
    List<AttrGroupVO> queryGroupAttrByCatId(Long catId);
}

