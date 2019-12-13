package com.atguigu.gmall.search.repository;

import com.atguigu.gmall.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author 凯少
 * @create 2019-12-10 18:27
 */
public interface GoodsRepository extends ElasticsearchRepository<Goods,Long> {
}
