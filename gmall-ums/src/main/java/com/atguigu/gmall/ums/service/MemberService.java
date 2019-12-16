package com.atguigu.gmall.ums.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.gmall.ums.entity.MemberEntity;
import com.atguigu.core.bean.PageVo;
import com.atguigu.core.bean.QueryCondition;


/**
 * 会员
 *
 * @author zyk
 * @email kaishao@atguigu.com
 * @date 2019-12-02 20:56:59
 */
public interface MemberService extends IService<MemberEntity> {

    PageVo queryPage(QueryCondition params);
    //判断信息是否合格，1：用户名，2：手机，3：邮箱
    Boolean check(String data, Integer type);
    //注册账号信息
    void register(MemberEntity memberEntity, String code);
    // 根据用户名和密码查询用户信息
    MemberEntity query(String username, String password);
}

