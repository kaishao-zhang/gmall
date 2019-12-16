package com.atguigu.gmall.auth.service.impl;

import com.atguigu.core.bean.Resp;
import com.atguigu.core.utils.CookieUtils;
import com.atguigu.core.utils.JwtUtils;
import com.atguigu.gmall.auth.feign.GmallUmsClient;
import com.atguigu.gmall.auth.propertites.JwtProperties;
import com.atguigu.gmall.auth.service.AuthService;
import com.atguigu.gmall.ums.entity.MemberEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 凯少
 * @create 2019-12-16 20:56
 */
@Service
@EnableConfigurationProperties(JwtProperties.class)
public class AuthServiceImpl implements AuthService {

    @Autowired
    private GmallUmsClient gmallUmsClient;
    @Autowired
    private JwtProperties jwtProperties;
    @Override
    public String accredit(String username, String password) throws Exception {
        Resp<MemberEntity> memberEntityResp = gmallUmsClient.query(username, password);
        MemberEntity memberEntity = memberEntityResp.getData();
        if (memberEntity == null) {
            return null;
        }
        Map<String,Object> map = new HashMap<>();
        map.put("id",memberEntity);
        map.put("username",memberEntity.getUsername());
        String token = JwtUtils.generateToken(map, jwtProperties.getPrivateKey(), jwtProperties.getExpire());
        return token;
    }
}
