package com.atguigu.gmall.index.feign;

import com.atguigu.gmall.pms.api.GmallPmsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 凯少
 * @create 2019-12-13 12:41
 */
@FeignClient("pms-service")
public interface GmallPmsClient extends GmallPmsApi {

}
