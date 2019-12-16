package com.atguigu.gmall.index.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.annotation.GmallCache;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.IndexVO;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author 凯少
 * @create 2019-12-13 13:02
 */
@Service
public class IndexServiceImpl implements IndexService {
    @Autowired
    private GmallPmsClient gmallPmsClient;
    @Autowired
    private StringRedisTemplate redisTemplate;
    private final String KEY_PREFIX="index:cates:";
    @Autowired
    private RedissonClient redissonClient;
    @Override
    public List<CategoryEntity> queryLevl1Categroy() {
        Resp<List<CategoryEntity>> listResp = gmallPmsClient.queryCategory(1, 0l);
        return listResp.getData();
    }

    @Override
    @GmallCache(prefix = KEY_PREFIX,timeout = 7*24*60)
    public List<IndexVO> querySubCategory(Long id) {

        //无数据从数据库中查询数据
        Resp<List<IndexVO>> listResp = this.gmallPmsClient.querySubCategory(id);

        return listResp.getData();
    }
}
