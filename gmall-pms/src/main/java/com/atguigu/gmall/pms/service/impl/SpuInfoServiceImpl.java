package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.dao.*;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.GmallSmsClient;
import com.atguigu.gmall.pms.service.ProductAttrValueService;
import com.atguigu.gmall.pms.service.SkuImagesService;
import com.atguigu.gmall.pms.service.SkuSaleAttrValueService;
import com.atguigu.gmall.pms.vo.ProductAttrValueVO;
import com.atguigu.gmall.pms.vo.SkuInfoVO;
import com.atguigu.gmall.pms.vo.SpuInfoVO;
import com.atguigu.gmall.sms.vo.SkuSaleInfoVO;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.Query;
import com.atguigu.core.bean.QueryCondition;

import com.atguigu.gmall.pms.service.SpuInfoService;
import org.springframework.util.CollectionUtils;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {
    @Autowired
    private SpuInfoDescDao spuInfoDescDao;
    @Autowired
    private ProductAttrValueService productAttrValueService;
    @Autowired
    private SkuInfoDao skuInfoDao;
    @Autowired
    private SkuImagesService skuImagesService;
    @Autowired
    private AttrDao attrDao;
    @Autowired
    private SkuSaleAttrValueService saleAttrValueService;
    @Autowired
    private GmallSmsClient gmallSmsClient;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Value("${item.rabbitmq.exchange}")
    private  String  EXCHANGE_NAME;
    @Override
    public PageVo queryPage(QueryCondition params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageVo(page);
    }

    @Override
    public PageVo querySpuInfoByCatId(QueryCondition queryCondition, Long catId) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        //判断分类id是否等于0
        if (catId != 0) {
            //表示查询本类
            wrapper.eq("catalog_id",catId);
        }
        //判断是否添加了条件
        String key = queryCondition.getKey();
        if (!StringUtils.isBlank(key)) {
            wrapper.and(t->t.eq("id",key).or().like("spu_name",key));
        }
        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(queryCondition)
                , wrapper);
        return new PageVo(page);
    }


    @GlobalTransactional
    @Override
    public void bigSave(SpuInfoVO spuInfoVO) {
        //1.保存spu对象，返回自增的id
        spuInfoVO.setCreateTime(new Date());
        spuInfoVO.setUodateTime(spuInfoVO.getCreateTime());
        this.save(spuInfoVO);
        Long spuId = spuInfoVO.getId();
        //2.保存spu_info_desc表
        if (!StringUtils.isBlank(spuInfoVO.getSpuDescription())) {
            SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
            spuInfoDescEntity.setSpuId(spuId);
            spuInfoDescEntity.setDecript(StringUtils.join(spuInfoVO.getSpuImages(),","));
            spuInfoDescDao.insert(spuInfoDescEntity);
        }
        //3.保存pms_product_attr_value
        List<ProductAttrValueVO> baseAttrs = spuInfoVO.getBaseAttrs();
        if(!CollectionUtils.isEmpty(baseAttrs)){
            List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(productAttrValueVO -> {
                productAttrValueVO.setAttrName(this.attrDao.selectById(productAttrValueVO.getAttrId()).getAttrName());
                productAttrValueVO.setSpuId(spuId);
                productAttrValueVO.setAttrSort(0);
                productAttrValueVO.setQuickShow(0);
                return productAttrValueVO;
            }).collect(Collectors.toList());
            this.productAttrValueService.saveBatch(productAttrValueEntities);
        }
        //4.保存sku_info信息
        List<SkuInfoVO> skus = spuInfoVO.getSkus();
        if (!CollectionUtils.isEmpty(skus)) {
            skus.forEach(skuInfoVO -> {
                skuInfoVO.setSpuId(spuId);
                skuInfoVO.setSkuCode(UUID.randomUUID().toString());
                skuInfoVO.setCatalogId(spuInfoVO.getCatalogId());
                skuInfoVO.setBrandId(spuInfoVO.getBrandId());
                List<String> images = skuInfoVO.getImages();
                if (!CollectionUtils.isEmpty(images) && skuInfoVO.getSkuDefaultImg() == null) {
                    skuInfoVO.setSkuDefaultImg(images.get(0));
                }
                this.skuInfoDao.insert(skuInfoVO);
                Long skuId = skuInfoVO.getSkuId();
                //5.保存sku图片信息
                if (!CollectionUtils.isEmpty(images)) {
                    List<SkuImagesEntity> skuImagesEntities = images.stream().map(image -> {
                        SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                        skuImagesEntity.setSkuId(skuId);
                        skuImagesEntity.setImgUrl(image);
                        skuImagesEntity.setDefaultImg(skuInfoVO.getSkuDefaultImg().equals(image) ? 1 : 0);
                        return skuImagesEntity;
                    }).collect(Collectors.toList());
                    skuImagesService.saveBatch(skuImagesEntities);
                }
                //6.保存sku的规格参数（销售属性）
                List<SkuSaleAttrValueEntity> saleAttrs = skuInfoVO.getSaleAttrs();
                if (!CollectionUtils.isEmpty(saleAttrs)) {
                    saleAttrs.forEach(skuSaleAttrValueEntity -> {
                        skuSaleAttrValueEntity.setSkuId(skuId);
                        skuSaleAttrValueEntity.setAttrName(this.attrDao.selectById(skuSaleAttrValueEntity.getAttrId()).getAttrName());
                    });
                    this.saleAttrValueService.saveBatch(saleAttrs);
                }
                //保存营销相关信息，需要远程调用gmall-sms
                // 7. 积分优惠
                // 8. 满减优惠
                // 9. 数量折扣
                SkuSaleInfoVO skuSaleInfoVO = new SkuSaleInfoVO();
                BeanUtils.copyProperties(skuInfoVO,skuSaleInfoVO);
                gmallSmsClient.saveSkuSaleInfo(skuSaleInfoVO);
                sendMsg("insert",spuId);
            });
        }

    }
    private void sendMsg(String type,Long spuId){
        //三个参数，1.交换机名称，2.什么类型的消息，3.传输的消息内容
        this.amqpTemplate.convertAndSend(EXCHANGE_NAME,"item."+type,spuId);
    }

}