package com.atguigu.gmall.item.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.item.feign.GmallPmsClient;
import com.atguigu.gmall.item.feign.GmallSmsClient;
import com.atguigu.gmall.item.feign.GmallWmsClient;
import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.item.vo.ItemVO;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.ItemGroupVO;
import com.atguigu.gmall.sms.vo.SkuSaleVO;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author 凯少
 * @create 2019-12-14 18:23
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private GmallPmsClient gmallPmsClient;
    @Autowired
    private GmallSmsClient gmallSmsClient;
    @Autowired
    private GmallWmsClient gmallWmsClient;
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public ItemVO queryItemdsVO(Long skuId) {
        ItemVO itemVO = new ItemVO();
        //设置和sku相关的信息
        CompletableFuture<Object> skuCompletableFuture = CompletableFuture.supplyAsync(() -> {
            Resp<SkuInfoEntity> skuInfoResp = this.gmallPmsClient.querySkuinfoById(skuId);
            SkuInfoEntity skuInfoEntity = skuInfoResp.getData();
            if (skuInfoEntity == null) {
                return itemVO;
            }
            //设置字段包括
            //    private String skuTitle;
            //    private String skuSubtitle;
            //    private BigDecimal weight;
            //    private BigDecimal price;
            //    private Long skuId;
            //    private Long spuId;
            BeanUtils.copyProperties(skuInfoEntity, itemVO);
            return skuInfoEntity;
        },threadPoolExecutor);
        //设置private String spuName;
        skuCompletableFuture.thenAccept(skuInfo -> {
            if (skuInfo != null && skuInfo instanceof SkuInfoEntity) {
                SkuInfoEntity skuInfoEntity = (SkuInfoEntity) skuInfo;
                Resp<SpuInfoEntity> spuInfoResp = this.gmallPmsClient.querySpuInfoById(skuInfoEntity.getSpuId());
                SpuInfoEntity spuInfoEntity = spuInfoResp.getData();
                if (spuInfoEntity != null) {
                    itemVO.setSpuName(spuInfoEntity.getSpuName());
                }
            }
        });
        //设置private CategoryEntity categoryEntity;
        CompletableFuture<Void> categoryCompletableFuture = skuCompletableFuture.thenAccept(skuInfo -> {
            if (skuInfo != null && skuInfo instanceof SkuInfoEntity) {
                SkuInfoEntity skuInfoEntity = (SkuInfoEntity) skuInfo;
                Resp<CategoryEntity> categoryResp = this.gmallPmsClient.queryCateGoryById(skuInfoEntity.getCatalogId());
                CategoryEntity categoryEntity = categoryResp.getData();
                itemVO.setCategoryEntity(categoryEntity);
            }
        });
        //设置 private BrandEntity brandEntity;
        CompletableFuture<Void> brandCompletableFuture = skuCompletableFuture.thenAccept(skuInfo -> {
            if (skuInfo != null && skuInfo instanceof SkuInfoEntity) {
                SkuInfoEntity skuInfoEntity = (SkuInfoEntity) skuInfo;
                Resp<BrandEntity> brandResp = this.gmallPmsClient.queryBrandInfoById(skuInfoEntity.getBrandId());
                BrandEntity brandEntity = brandResp.getData();
                itemVO.setBrandEntity(brandEntity);
            }
        });

        //设置 private List<SkuImagesEntity> pics;
        CompletableFuture<Void> picsCompletableFuture = CompletableFuture.runAsync(() -> {
            Resp<List<SkuImagesEntity>> listResp = this.gmallPmsClient.querySkuImageBySkuId(skuId);
            List<SkuImagesEntity> skuImagesEntities = listResp.getData();
            itemVO.setPics(skuImagesEntities);
        });
//      设置private List<SkuSaleVO> sales;
        CompletableFuture<Void> salesCompletableFuture = CompletableFuture.runAsync(() -> {
            Resp<List<SkuSaleVO>> skuSaleResp = this.gmallSmsClient.querySkuSaleBySkuId(skuId);
            List<SkuSaleVO> saleVOS = skuSaleResp.getData();
            itemVO.setSales(saleVOS);
        });
        //设置private Boolean store;
        CompletableFuture<Void> storeCompletableFuture = CompletableFuture.runAsync(() -> {
            Resp<List<WareSkuEntity>> wareSkuEntitysResp = this.gmallWmsClient.queryWareSkuBySkuId(skuId);
            List<WareSkuEntity> wareSkuEntities = wareSkuEntitysResp.getData();
            itemVO.setStore(wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0));
        });
        //设置private List<SkuSaleAttrValueEntity>
        CompletableFuture<Void> attrValueCompletableFuture = skuCompletableFuture.thenAccept(skuInfo -> {
            if (skuInfo != null && skuInfo instanceof SkuInfoEntity) {
                SkuInfoEntity skuInfoEntity = (SkuInfoEntity) skuInfo;
                Resp<List<SkuSaleAttrValueEntity>> SkuSaleAttrValueResp = this.gmallPmsClient.querySkuSaleAttrValBySpuId(skuInfoEntity.getSpuId());
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntities = SkuSaleAttrValueResp.getData();
                itemVO.setSaleAttrs(skuSaleAttrValueEntities);
            }
        });
        //设置private List<String> images;
        CompletableFuture<Void> imagesCompletableFuture = skuCompletableFuture.thenAccept(skuInfo -> {
            if (skuInfo != null && skuInfo instanceof SkuInfoEntity) {
                SkuInfoEntity skuInfoEntity = (SkuInfoEntity) skuInfo;
                Resp<SpuInfoDescEntity> spuInfoDescEntityResp = this.gmallPmsClient.querySpuInfoDescById(skuInfoEntity.getSpuId());
                SpuInfoDescEntity spuInfoDescEntity = spuInfoDescEntityResp.getData();
                if (!StringUtils.isEmpty(spuInfoDescEntity.getDecript())) {
                    String[] images = StringUtils.split(spuInfoDescEntity.getDecript(), ",");
                    itemVO.setImages(Arrays.asList(images));
                }
            }
        });
        //设置private List<ItemGroupVO> group
        CompletableFuture<Void> groupCompletableFuture = skuCompletableFuture.thenAccept(skuInfo -> {
            if (skuInfo != null && skuInfo instanceof SkuInfoEntity) {
                SkuInfoEntity skuInfoEntity = (SkuInfoEntity) skuInfo;
                Resp<List<ItemGroupVO>> itemGroutpResp = this.gmallPmsClient.queryItemGroupVOByCidAndSpuId(skuInfoEntity.getCatalogId(),skuInfoEntity.getSpuId());
                List<ItemGroupVO> itemGroupVOS = itemGroutpResp.getData();
                itemVO.setGroups(itemGroupVOS);
            }
        });
        CompletableFuture.allOf(categoryCompletableFuture, brandCompletableFuture, picsCompletableFuture
                , salesCompletableFuture, storeCompletableFuture, attrValueCompletableFuture,
                groupCompletableFuture,imagesCompletableFuture).join();

        return itemVO;
    }
}
