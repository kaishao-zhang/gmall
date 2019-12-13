package com.atguigu.gmall.pms.service;

import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import org.springframework.data.domain.Page;


/**
 * spu信息
 *
 * @author zyk
 * @email kaishao@atguigu.com
 * @date 2019-12-02 17:51:54
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageVo queryPage(QueryCondition params);
    //根据catId查询spu的信息
    PageVo querySpuInfoByCatId(QueryCondition queryCondition, Long catId);
    //将创建的spu对象保存到多个数据库中
    void bigSave(SpuInfoVO spuInfoVO);
}

