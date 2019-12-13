package com.atguigu.gmall.pms.api;

import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.pms.entity.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
}
