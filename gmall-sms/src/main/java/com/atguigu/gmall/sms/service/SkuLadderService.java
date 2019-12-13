package com.atguigu.gmall.sms.service;

import com.atguigu.gmall.sms.vo.SkuSaleInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.sms.entity.SkuLadderEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 商品阶梯价格
 *
 * @author zyk
 * @email kaishao@atguigu.com
 * @date 2019-12-02 20:41:53
 */
public interface SkuLadderService extends IService<SkuLadderEntity> {

    PageVo queryPage(QueryCondition params);
    //保存sku的销售信息
    void saveSkuSaleInfo(SkuSaleInfoVO skuSaleInfoVO);
}

