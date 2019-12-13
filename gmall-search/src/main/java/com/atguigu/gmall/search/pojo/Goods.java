package com.atguigu.gmall.search.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * @author 凯少
 * @create 2019-12-09 19:42
 * 用来在前台展示信息，封装elasticsearch需要的字段信息的实体类
 */
@Data
@Document(indexName = "goods",type = "info",shards = 3,replicas = 2)
public class Goods {

    @Id
    private Long skuId;//进入页面时点击图片展示商品详情时的skuId
    @Field(type = FieldType.Keyword ,index = false)
    private String pic;//展示的图片的地址
    @Field(type = FieldType.Text,analyzer = "ik_max_word")
    private String title;//展示的标题
    @Field(type = FieldType.Double)
    private Double price;//展示的价格
    @Field(type = FieldType.Long)
    private Long sale;//以后根据销售量排序
    @Field(type = FieldType.Date)
    private Date createTime;//以后根据时间展示新品，排序
    @Field(type = FieldType.Long)
    private Long brandId;//筛选时的字段，表面是品牌名，其实点击传参是品牌id
    @Field(type = FieldType.Keyword)
    private String brandName;//筛选时品牌名称字段

    @Field(type = FieldType.Long)
    private Long categoryId;//筛选时的分类id
    @Field(type = FieldType.Keyword)
    private String categoryName;//筛选时分类的名称

    @Field(type = FieldType.Nested)
    private List<SearchAttr> attrs;//筛选时的属性信息

    @Field(type = FieldType.Boolean)
    private Boolean store;


}
