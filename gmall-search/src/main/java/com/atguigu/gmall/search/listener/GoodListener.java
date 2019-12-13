package com.atguigu.gmall.search.listener;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.search.feign.GmallPmsClient;
import com.atguigu.gmall.search.feign.GmallWmsClient;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchAttr;
import com.atguigu.gmall.search.repository.GoodsRepository;
import com.atguigu.gmall.wms.entity.WareSkuEntity;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 凯少
 * @create 2019-12-11 20:17
 */
@Component
public class GoodListener {


    @Autowired
    private GmallPmsClient gmallPmsClient;
    @Autowired
    private GmallWmsClient gmallWmsClient;
    @Autowired
    private GoodsRepository goodsRepository;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "gmall-search-queue", declare = "true"),
            exchange = @Exchange(value = "GMALL-PMS-EXCHANGE", type = ExchangeTypes.TOPIC, ignoreDeclarationExceptions = "true"),
            key = {"item.insert", "item.update"}
    )
    )
    public void listener(Long spuId) {
        Resp<SpuInfoEntity> spuInfoEntityResp = this.gmallPmsClient.querySpuInfoById(spuId);
        SpuInfoEntity spuInfoEntity = spuInfoEntityResp.getData();
        if (spuInfoEntity == null) {
            return;
        }
        Resp<List<SkuInfoEntity>> skuResp = this.gmallPmsClient.querySkuInfoBySpuId(spuId);
        List<SkuInfoEntity> skuInfoEntities = skuResp.getData();
        if (!CollectionUtils.isEmpty(skuInfoEntities)) {
            //根据sku查询属性集合
            List<Goods> goodsList = skuInfoEntities.stream().map(skuInfoEntity -> {
                Goods goods = new Goods();
                goods.setSkuId(skuInfoEntity.getSkuId());
                goods.setPrice(skuInfoEntity.getPrice().doubleValue());
                goods.setTitle(skuInfoEntity.getSkuTitle());
                goods.setBrandId(skuInfoEntity.getBrandId());
                //根据brandId查询所有的brand集合
                Resp<BrandEntity> brandEntityResp = this.gmallPmsClient.queryBrandInfoById(skuInfoEntity.getBrandId());
                BrandEntity brandEntity = brandEntityResp.getData();
                if (brandEntity!=null) {
                    goods.setBrandName(brandEntity.getName());
                }
                //根据categoryId查询所有的category集合
                goods.setCategoryId(skuInfoEntity.getCatalogId());
                goods.setCreateTime(spuInfoEntity.getCreateTime());
                goods.setPic(skuInfoEntity.getSkuDefaultImg());
                Resp<CategoryEntity> categoryEntityResp = this.gmallPmsClient.queryCateGoryById(skuInfoEntity.getCatalogId());
                CategoryEntity categoryEntity = categoryEntityResp.getData();
                if (categoryEntity != null) {
                    goods.setCategoryName(categoryEntity.getName());
                }
                //根据sku的id查询所有的库存信息
                Resp<List<WareSkuEntity>> listResp = this.gmallWmsClient.queryWareSkuBySkuId(skuInfoEntity.getSkuId());
                List<WareSkuEntity> wareSkuEntities = listResp.getData();
                if (!CollectionUtils.isEmpty(wareSkuEntities)) {
                    boolean flag = wareSkuEntities.stream().anyMatch(wareSkuEntity -> wareSkuEntity.getStock() > 0);
                    goods.setStore(flag);
                }
                goods.setStore(false);
                goods.setSale(1l);
                Resp<List<ProductAttrValueEntity>> procutListResp = this.gmallPmsClient.queryAttrValueBySpuId(spuInfoEntity.getId());
                List<ProductAttrValueEntity> productAttrValueEntities = procutListResp.getData();
                if (!CollectionUtils.isEmpty(productAttrValueEntities)) {
                    List<SearchAttr> searchAtrrList = productAttrValueEntities.stream().map(productAttrValueEntity -> {
                        SearchAttr searchAttr = new SearchAttr();
                        searchAttr.setAttrId(productAttrValueEntity.getAttrId());
                        searchAttr.setAttrName(productAttrValueEntity.getAttrName());
                        searchAttr.setAttrValue(productAttrValueEntity.getAttrValue());
                        return searchAttr;
                    }).collect(Collectors.toList());
                    //根据selectType和spuid联合查询搜索属性
                    goods.setAttrs(searchAtrrList);
                }
                return goods;
            }).collect(Collectors.toList());
            this.goodsRepository.saveAll(goodsList);
        }
    }
}