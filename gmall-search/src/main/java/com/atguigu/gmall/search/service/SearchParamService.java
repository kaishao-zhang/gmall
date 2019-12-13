package com.atguigu.gmall.search.service;

import com.atguigu.gmall.search.pojo.SearchParam;
import com.atguigu.gmall.search.pojo.SearchResponseVO;

import java.io.IOException;

/**
 * @author 凯少
 * @create 2019-12-10 20:20
 */
public interface SearchParamService {
    //根据查询条件得到搜索结果集
    SearchResponseVO search(SearchParam searchParam) throws IOException;
}
