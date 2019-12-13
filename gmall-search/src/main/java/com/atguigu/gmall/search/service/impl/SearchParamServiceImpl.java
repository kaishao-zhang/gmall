package com.atguigu.gmall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.search.pojo.Goods;
import com.atguigu.gmall.search.pojo.SearchParam;
import com.atguigu.gmall.search.pojo.SearchResponseAttrVO;
import com.atguigu.gmall.search.pojo.SearchResponseVO;
import com.atguigu.gmall.search.service.SearchParamService;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author 凯少
 * @create 2019-12-10 20:21
 */
@Service
public class SearchParamServiceImpl implements SearchParamService {
    @Autowired
    @Qualifier("restClient")
    private RestHighLevelClient restClient;
    @Override
    public SearchResponseVO search(SearchParam searchParam) throws IOException {

        SearchSourceBuilder sourceBuilder = this.sourceBuilder(searchParam);
        SearchRequest searchRequest = new SearchRequest("goods");
        searchRequest.types("info");
        searchRequest.source(sourceBuilder);
        SearchResponse search = restClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchResponseVO searchResponseVO = this.parseSearchResponse(search);
        searchResponseVO.setPageNum(searchParam.getPageNum());
        searchResponseVO.setPageSize(searchParam.getPageSize());
        System.out.println(search);
        return searchResponseVO;
    }
    private SearchResponseVO parseSearchResponse(SearchResponse search){
        SearchResponseVO searchResponseVO = new SearchResponseVO();
        SearchHits hits = search.getHits();
        // 解析品牌的聚合结果集
        SearchResponseAttrVO brand = new SearchResponseAttrVO();
        brand.setName("品牌");
        // 获取品牌的聚合结果集
        // 获取品牌的聚合结果集
        Map<String, Aggregation> aggregationMap = search.getAggregations().asMap();
        ParsedLongTerms brandIdAgg = (ParsedLongTerms)aggregationMap.get("brandIdAgg");
        List<String> brandValues = brandIdAgg.getBuckets().stream().map(bucket -> {
            Map<String, String> map = new HashMap<>();
            // 获取品牌id
            map.put("id", bucket.getKeyAsString());
            // 获取品牌名称：通过子聚合来获取
            Map<String, Aggregation> brandIdSubMap = bucket.getAggregations().asMap();
            ParsedStringTerms brandNameAgg = (ParsedStringTerms)brandIdSubMap.get("brandNameAgg");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            map.put("name", brandName);
            return JSON.toJSON(map).toString();
        }).collect(Collectors.toList());
        brand.setValue(brandValues);
        searchResponseVO.setBrand(brand);
        SearchResponseAttrVO cate = new SearchResponseAttrVO();
        cate.setName("分类");
        ParsedLongTerms categoryIdAgg = (ParsedLongTerms)aggregationMap.get("categoryIdAgg");
        List<String> cateValues = categoryIdAgg.getBuckets().stream().map(bucket -> {
            Map<String, String> map = new HashMap<>();
            map.put("id", bucket.getKeyAsString());
            Map<String, Aggregation> cateIdSubMap = bucket.getAggregations().asMap();
            ParsedStringTerms categoryNameAgg = (ParsedStringTerms) cateIdSubMap.get("categoryNameAgg");
            String cateName = categoryNameAgg.getBuckets().get(0).getKeyAsString();
            map.put("name", cateName);
            return JSON.toJSON(map).toString();
        }).collect(Collectors.toList());
        cate.setValue(cateValues);
        searchResponseVO.setCatelog(cate);
        searchResponseVO.setTotal(hits.getTotalHits());
        ParsedNested attrAgg = (ParsedNested)aggregationMap.get("attrAgg");
        ParsedLongTerms attrIdAgg =(ParsedLongTerms) attrAgg.getAggregations().get("attrIdAgg");
        List<? extends Terms.Bucket> buckets = attrIdAgg.getBuckets();
        if (!CollectionUtils.isEmpty(buckets)) {
            List<SearchResponseAttrVO> attrs = buckets.stream().map(bucket -> {
                SearchResponseAttrVO searchResponseAttrVO = new SearchResponseAttrVO();
                searchResponseAttrVO.setProductAttributeId(((Terms.Bucket) bucket).getKeyAsNumber().longValue());
                Map<String, Aggregation> stringAggregationMap = bucket.getAggregations().asMap();
                ParsedStringTerms attrNameAgg = (ParsedStringTerms) stringAggregationMap.get("attrNameAgg");
                String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
                searchResponseAttrVO.setName(attrName);
                List<? extends Terms.Bucket> valueBuckets = ((ParsedStringTerms) stringAggregationMap.get("attrValueAgg")).getBuckets();
                List<String> attrValues = valueBuckets.stream().map(Terms.Bucket::getKeyAsString).collect(Collectors.toList());
                searchResponseAttrVO.setValue(attrValues);
                return searchResponseAttrVO;
            }).collect(Collectors.toList());
            searchResponseVO.setAttrs(attrs);
        }
        SearchHit[] searchHits = hits.getHits();
        List<Goods> goodsList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(Arrays.asList(searchHits))) {
            for (SearchHit searchHit : searchHits) {
                Goods goods = JSON.parseObject(searchHit.getSourceAsString(), Goods.class);
                goods.setTitle(searchHit.getHighlightFields().get("title").getFragments()[0].toString());
                goodsList.add(goods);
            }
        }

        searchResponseVO.setProducts(goodsList);
        return searchResponseVO;
    }
    private SearchSourceBuilder sourceBuilder(SearchParam searchParam){
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        //1.构建bool查询条件
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //1.1构建bool子查询must搜索条件,即搜索条件，比如按照手机查询
        //获得搜索的条件
        String keyword = searchParam.getKeyword();
        //若没有查询关键字，则直接返回空，不用查询
        if (StringUtils.isBlank(keyword)) {
            return null;
        }
        boolQueryBuilder.must(QueryBuilders.matchQuery("title",keyword).operator(Operator.AND));
        //1.2构建bool子查询filter过滤条件
        //1.2.1构建品牌过滤条件
        //获取brand的值
        String[] brand = searchParam.getBrand();
        if (brand != null && brand.length != 0) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId",brand));
        }
        //1.2.2构建分类的过滤条件
        String[] catelog3 = searchParam.getCatelog3();
        if (catelog3 != null && catelog3.length != 0) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("categoryId",catelog3));
        }
        //1.2.3构建属性的过滤条件
        String[] props = searchParam.getProps();
        if (props != null && props.length !=0 ) {

            for (String prop : props) {
                //构建属时的bool查询
                BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
                BoolQueryBuilder subBoolQuery = QueryBuilders.boolQuery();
                String[] split = StringUtils.split(prop, ":");
                if ((split == null) ||  split.length !=2)  {
                    continue;
                }
                subBoolQuery.must(QueryBuilders.termsQuery("attrs.attrId",split[0]));
                subBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue",StringUtils.split(split[1],"-")));
                boolQuery.must(QueryBuilders.nestedQuery("attrs", subBoolQuery, ScoreMode.None));
                boolQueryBuilder.filter(boolQuery);
            }

        }
        //1.2.4构建价格区间的过滤条件
        RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("price");
        Integer priceFrom = searchParam.getPriceFrom();
        Integer priceTo = searchParam.getPriceTo();
        if (priceFrom !=null ) {
            rangeQueryBuilder.from(priceFrom);
        }
        if (priceTo != null) {
            rangeQueryBuilder.to(priceTo);
        }
        boolQueryBuilder.filter(rangeQueryBuilder);
        //将1.1的bool查询放入查询中
        sourceBuilder.query(boolQueryBuilder);

        //2.构建分页
        Integer pageNum = searchParam.getPageNum();
        Integer pageSize = searchParam.getPageSize();
        sourceBuilder.from((pageNum-1)*pageSize);
        sourceBuilder.size(pageSize);
        //3.构建排序
        String order = searchParam.getOrder();
        if (StringUtils.isNotEmpty(order)) {
            String feild = null;
            String[] split = StringUtils.split(order, ":");
            switch (split[0]) {
                case "1": feild="sale"; break;
                case "2": feild="price"; break;
            }
            sourceBuilder.sort(feild,StringUtils.equals("asc",split[1])? SortOrder.ASC:SortOrder.DESC);
        }
        //4.构建高亮
        sourceBuilder.highlighter(new HighlightBuilder().field("title").preTags("<em>").postTags("</em>"));
        //5.构建聚合
        //5.1创建品牌聚合
        //5.1.1创建品牌id聚合词条
        //5.1.2创建品牌name的子聚合词条
        sourceBuilder.aggregation(AggregationBuilders.terms("brandIdAgg").field("brandId")
                .subAggregation(AggregationBuilders.terms("brandNameAgg").field("brandName")));
        //5.2创建分类聚合
        //5.2.1创建分类id聚合词条
        //5.2.2创建分类name的子聚合词条
        sourceBuilder.aggregation(AggregationBuilders.terms("categoryIdAgg").field("categoryId")
            .subAggregation(AggregationBuilders.terms("categoryNameAgg").field("categoryName")));
        //5.3创建属性聚合
        sourceBuilder.aggregation(AggregationBuilders.nested("attrAgg","attrs")
                .subAggregation(AggregationBuilders.terms("attrIdAgg").field("attrs.attrId")
                        .subAggregation(AggregationBuilders.terms("attrNameAgg").field("attrs.attrName"))
                .subAggregation(AggregationBuilders.terms("attrValueAgg").field("attrs.attrValue"))));
        System.out.println(sourceBuilder.toString());
        sourceBuilder.fetchSource(new String[]{"skuId","pic","title","price"},null);
        return sourceBuilder;
    }
}
