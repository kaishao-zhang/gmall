package com.atguigu.gmall.pms.feign;

import com.atguigu.gmall.sms.feign.SaveSkuSale;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 凯少
 * @create 2019-12-07 10:30
 */
@FeignClient("sms-service")
public interface GmallSmsClient extends SaveSkuSale {
}
