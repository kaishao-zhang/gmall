package com.atguigu.gmall.item.vo;

import com.atguigu.gmall.pms.entity.BrandEntity;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.entity.SkuImagesEntity;
import com.atguigu.gmall.pms.entity.SkuSaleAttrValueEntity;
import com.atguigu.gmall.pms.vo.ItemGroupVO;
import com.atguigu.gmall.sms.vo.SkuSaleVO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author 凯少
 * @create 2019-12-14 17:03
 */
@Data
public class ItemVO {
    private Long skuId;

    private CategoryEntity categoryEntity;
    private BrandEntity brandEntity;

    private String skuTitle;
    private String skuSubtitle;
    private BigDecimal weight;
    private BigDecimal price;

    private Long spuId;
    private String spuName;

    private List<SkuImagesEntity> pics;//sku的图片列表
    private List<SkuSaleVO> sales;//营销信息

    private Boolean store;//是否有货

    private List<SkuSaleAttrValueEntity> saleAttrs;//销售属性

    private List<String> images;//spu海报图片，

    private List<ItemGroupVO> groups;//规格参数组，及参数值



}
