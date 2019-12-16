package com.atguigu.gmall.auth.feign;

import com.atguigu.gmall.ums.feign.GmallUmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 凯少
 * @create 2019-12-16 20:47
 */
@FeignClient("ums-service")
public interface GmallUmsClient extends GmallUmsApi {

}
