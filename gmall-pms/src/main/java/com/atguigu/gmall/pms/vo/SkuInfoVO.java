package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.entity.SkuInfoEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 凯少
 * @create 2019-12-04 19:18
 */
@Data
public class SkuInfoVO extends SkuInfoEntity {

    //sku中的saleAttrs对应pms_sku_sale_attr_value表
    private List<SkuSaleAttrValueEntity> saleAttrs;
    //sku中的images的属性，对应pms_sku_images表
    private List<String> images;
    //sms_spu_bounds表中的字段（积分活动表）
    /**
     * 成长积分
     */
    private BigDecimal growBounds;
    /**
     * 购物积分
     */
    private BigDecimal buyBounds;
    /**
     * 优惠生效情况[1111（四个状态位，从右到左）;0 - 无优惠，成长积分是否赠送;1 - 无优惠，购物积分是否赠送;2 - 有优惠，成长积分是否赠送;3 - 有优惠，购物积分是否赠送【状态位0：不赠送，1：赠送】]
     */
    private List<Integer> work;
    //sms_spu_full_reduction表中的字段（// 满减活动表）
    /**
     * 满多少
     */
    private BigDecimal fullPrice;
    /**
     * 减多少
     */
    private BigDecimal reducePrice;
    /**
     * 是否参与其他优惠
     */
    private Integer fullAddOther;
    //sms_spu_ladder表中的字段（折扣表）
    /**
     * 满几件
     */
    private Integer fullCount;
    /**
     * 打几折
     */
    private BigDecimal discount;
    /**
     * 折后价
     */
    @ApiModelProperty(name = "price",value = "折后价")
    private BigDecimal price;
    /**
     * 是否叠加其他优惠[0-不可叠加，1-可叠加]
     */
    private Integer ladderAddOther;
}
