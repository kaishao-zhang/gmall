package com.atguigu.gmall.search.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.search.pojo.SearchParam;
import com.atguigu.gmall.search.pojo.SearchResponseVO;
import com.atguigu.gmall.search.service.SearchParamService;
import org.elasticsearch.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author 凯少
 * @create 2019-12-10 20:15
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchParamService searchParamService;
    @GetMapping
    public Resp<SearchResponseVO> search(SearchParam searchParam) throws IOException {
        SearchResponseVO search = searchParamService.search(searchParam);
        return Resp.ok(search);
    }

}
