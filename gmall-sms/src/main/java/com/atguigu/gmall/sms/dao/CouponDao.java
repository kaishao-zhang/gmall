package com.atguigu.gmall.sms.dao;

import com.atguigu.gmall.sms.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author zyk
 * @email kaishao@atguigu.com
 * @date 2019-12-02 20:41:53
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
