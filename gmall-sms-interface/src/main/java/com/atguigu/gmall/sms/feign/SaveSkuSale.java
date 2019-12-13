package com.atguigu.gmall.sms.feign;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.sms.vo.SkuSaleInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author 凯少
 * @create 2019-12-04 23:08
 */


public interface SaveSkuSale {
    @PostMapping("/sms/skuladder/skuSale/save")
    public Resp<Object> saveSkuSaleInfo(@RequestBody SkuSaleInfoVO skuSaleInfoVO);
}
