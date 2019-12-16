package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.vo.IndexVO;
import com.atguigu.gmall.pms.vo.ItemGroupVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 凯少
 * @create 2019-12-09 20:39
 */

public interface GmallPmsApi {
    //1.根据spuid查询所有的spu信息
    @PostMapping("pms/spuinfo/info")
    public Resp<List<SpuInfoEntity>> queryInfoByCondition(@RequestBody QueryCondition queryCondition);
    //2.根据spu的id来查询所有的sku信息
    @GetMapping("pms/skuinfo/{spuId}")
    public Resp<List<SkuInfoEntity>> querySkuInfoBySpuId(@PathVariable("spuId") Long spuId);
    //3.根据brandid查询所有的brand信息
    @GetMapping("pms/brand/info/{brandId}")
    public Resp<BrandEntity> queryBrandInfoById(@PathVariable("brandId") Long brandId);
    //4.根据categoryid查询所有的category信息
    @GetMapping("pms/category/info/{catId}")
    public Resp<CategoryEntity> queryCateGoryById(@PathVariable("catId") Long catId);
    //5.根据selectType和spuid联合查询搜索属性
    @GetMapping("pms/productattrvalue/{spuId}")
    public Resp<List<ProductAttrValueEntity>> queryAttrValueBySpuId(@PathVariable("spuId")Long spuId);
    //根据spuid获取所有的spuentity
    @GetMapping("pms/spuinfo/info/{id}")
    public Resp<SpuInfoEntity> querySpuInfoById(@PathVariable("id") Long id);
    //查询分级的菜单信息
    @GetMapping("pms/category")
    public Resp<List<CategoryEntity>> queryCategory(@RequestParam(value = "level", defaultValue = "0") Integer level,
                                                    @RequestParam(value = "parentCid", required = false) Long parentCid);
    //根据一级分类id查询所有的2,3级分类信息
    @GetMapping("pms/category/{id}")
    public Resp<List<IndexVO>> querySubCategory(@PathVariable("id") Long id);
    //根据skuid查询所有的sku信息
    @GetMapping("pms/skuinfo/info/{skuId}")
    public Resp<SkuInfoEntity> querySkuinfoById(@PathVariable("skuId") Long skuId);
    //根据sku的id查询所有的sku图片
    @GetMapping("pms/skuimages/{skuId}")
    public Resp<List<SkuImagesEntity>> querySkuImageBySkuId(@PathVariable("skuId")Long skuId);
    //根据spuId查询spu下的所有sku和属性值信息
    @GetMapping("pms/skusaleattrvalue/{spuId}")
    public Resp<List<SkuSaleAttrValueEntity>> querySkuSaleAttrValBySpuId(@PathVariable("spuId")Long spuId);
    //根据spuId查询所有的spu描述信息
    @GetMapping("pms/spuinfodesc/info/{spuId}")
    public Resp<SpuInfoDescEntity> querySpuInfoDescById(@PathVariable("spuId") Long spuId);

    @GetMapping("pms/attrgroup/item/group/{cid}/{spuId}")
    public Resp<List<ItemGroupVO>> queryItemGroupVOByCidAndSpuId(@PathVariable("cid")Long cid, @PathVariable("spuId")Long spuId);
}
