package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import com.atguigu.gmall.wms.api.GmallWmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 凯少
 * @create 2019-12-10 11:38
 */
@FeignClient("wms-service")
public interface GmallWmsClient extends GmallWmsApi {
}
