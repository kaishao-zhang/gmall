package com.atguigu.gmall.ums.feign;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.ums.entity.MemberEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author 凯少
 * @create 2019-12-16 19:46
 */
public interface GmallUmsApi {
    @GetMapping("ums/member/query")
    public Resp<MemberEntity> query(@RequestParam("username")String username, @RequestParam("password")String password);
}
