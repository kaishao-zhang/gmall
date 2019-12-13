package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuImagesEntity;
import com.atguigu.gmall.pms.entity.SpuInfoEntity;
import lombok.Data;

import java.util.List;

/**
 * @author 凯少
 * @create 2019-12-04 19:04
 */
@Data
public class SpuInfoVO extends SpuInfoEntity {
    //json中的spuImages属性，对应pms_spu_images
    private List<String> spuImages;
    //json中的baseAttrs属性，对应pms_product_attr_value表但是valueSelect的属性不对应表中的attr_value字段
    //应该扩展pms_product_attr_value表对应的实体类
    private List<ProductAttrValueVO> baseAttrs;
    //json中的skus属性，对应pms_sku_info表，但是其中多出很对与销售略相关的实体类，应该扩展
    private List<SkuInfoVO> skus;
}
