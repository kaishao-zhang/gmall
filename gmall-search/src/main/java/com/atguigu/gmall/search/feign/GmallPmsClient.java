package com.atguigu.gmall.search.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 凯少
 * @create 2019-12-10 11:38
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {

}
