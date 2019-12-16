package com.atguigu.gmall.item.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 凯少
 * @create 2019-12-14 18:24
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {
}
