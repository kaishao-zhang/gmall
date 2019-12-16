package com.atguigu.gmall.index.controller;

import com.atguigu.core.bean.Resp;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.IndexVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 凯少
 * @create 2019-12-13 11:31
 */
@RestController
@RequestMapping("/index")
public class IndexController {

    @Autowired
    private IndexService indexService;
    @GetMapping("/cates")
    public Resp<List<CategoryEntity>> queryLevel1Category(){
        List<CategoryEntity> categoryEntities = indexService.queryLevl1Categroy();
        return Resp.ok(categoryEntities);
    }
    @GetMapping("/cates/{id}")
    public Resp<List<IndexVO>> querySubCategory(@PathVariable("id") Long id){
        List<IndexVO> categoryEntities = indexService.querySubCategory(id);
        return Resp.ok(categoryEntities);
    }

}
