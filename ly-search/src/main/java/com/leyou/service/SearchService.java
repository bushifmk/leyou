package com.leyou.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.common.vo.PageResult;
import com.leyou.item.api.BrandClient;
import com.leyou.item.api.CategoryClient;
import com.leyou.item.api.GoodsClient;
import com.leyou.item.api.SpecClient;
import com.leyou.item.pojo.*;
import com.leyou.pojo.Goods;
import com.leyou.pojo.SearchRequest;
import com.leyou.pojo.SearchResult;
import com.leyou.repository.GoodsRepository;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/12 18:51
 * @description TODO
 **/
@Service
public class SearchService {
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecClient specClient;
    @Autowired
    private GoodsRepository repository;
    @Autowired
    private ElasticsearchTemplate template;

    public Goods buildGoods(Spu spu) {
        String categoryNames = categoryClient.queryByIdList(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()))
                .stream().map(Category::getName).collect(Collectors.joining(","));
        Brand brand = brandClient.queryById(spu.getBrandId());
        String all = spu.getTitle() + categoryNames + brand.getName();
        List<Sku> skuList = goodsClient.querySkuListBySpuId(spu.getId());
        List<Map<String, Object>> skuMap = new ArrayList<>();
        for (Sku sku : skuList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            map.put("price", sku.getPrice());
            map.put("image", StringUtils.substringBefore(sku.getImages(), ","));
            skuMap.add(map);
        }
        Set<Long> price = skuList.stream().map(Sku::getPrice).collect(Collectors.toSet());
        Map<String, Object> specs = new HashMap<>();
        List<SpecParam> specParams = specClient.queryParam(null, spu.getCid3(), true);
        SpuDetail spuDetail = goodsClient.queryDetailBySpuId(spu.getId());
        Map<Long, Object> genericSpec = JsonUtils.toMap(spuDetail.getGenericSpec(), Long.class, Object.class);
        Map<Long, List<String>> specialSpec = JsonUtils.nativeRead(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<String>>>() {
        });
        for (SpecParam param : specParams) {
            String key = param.getName();
            Object value = null;
            if (param.getGeneric()) {
                value = genericSpec.get(param.getId());
            } else {
                value = specialSpec.get(param.getId());
            }
            if (param.getNumeric()) {
                value = chooseSegment(value, param);
            }
            specs.put(key, value);
        }

        Goods goods = new Goods();
        //把spu中的和goods中属性名相同的拷贝
        BeanUtils.copyProperties(spu, goods);
        goods.setCreateTime(spu.getCreateTime().getTime());
        goods.setSpecs(specs);
        goods.setSkus(JsonUtils.toString(skuMap));
        goods.setPrice(price);

        goods.setAll(all);
        return goods;
    }

    private String chooseSegment(Object value, SpecParam p) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其它";
        }
        double val = NumberUtils.toDouble(value.toString());
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    public PageResult<Goods> search(SearchRequest request) {
        String key = request.getKey();
        if (StringUtils.isBlank(key)) {
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        //原生查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //控制返回结果
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "subTitle", "skus"}, null));
        //分页
        int page = request.getPage() - 1;
        int size = request.getSize();
        queryBuilder.withPageable(PageRequest.of(page, size));
        //查询条件
        QueryBuilder basicQuery = QueryBuilders.matchQuery("all", key);
        queryBuilder.withQuery(basicQuery);
        //添加聚合条件
        //对分类聚合
        String categoryAggName = "categoryAgg";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        //对品牌聚合
        String brandAggName = "brandAggName";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        //获取结果
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
        //解析结果
        long total = result.getTotalElements();
        int totalPages = result.getTotalPages();
        List<Goods> list = result.getContent();
        //解析聚合结果
        List<Map<String, Object>> filterList = new ArrayList<>();
        Aggregations aggs = result.getAggregations();
        //处理分类聚合
        LongTerms categoryTerms = aggs.get(categoryAggName);
        List<Long> idList = handleCategoryAgg(categoryTerms, filterList);
        //处理品牌聚合
        LongTerms brandTerms = aggs.get(brandAggName);
        handleBrandAgg(brandTerms, filterList);
        //处理规格参数聚合
        if(idList!=null && idList.size()==1){
            //分类只有一个，可以对规格参数聚合
            handleSpecAgg(idList.get(0),basicQuery,filterList);
        }
        //封装并返回
        return new SearchResult(total, totalPages, list, filterList);
    }

    private void handleSpecAgg(Long cid, QueryBuilder basicQuery, List<Map<String, Object>> filterList) {
        //查询当前分类下的需要搜索的规格参数
        List<SpecParam> specParams = specClient.queryParam(null, cid, true);
        //构建查询条件对象
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withQuery(basicQuery);
        //添加分页属性，保证只返回聚合结果，不返回查询结果
        queryBuilder.withPageable(PageRequest.of(0,1));
        //添加聚合条件
        for (SpecParam specParam : specParams) {
            //取出规格参数名称，作为聚合名称
            String name = specParam.getName();
            //添加聚合
            queryBuilder.addAggregation(AggregationBuilders.terms(name).field("specs." +name));
        }
        //查询结果
        AggregatedPage<Goods> result = template.queryForPage(queryBuilder.build(), Goods.class);
        //解析聚合结果，封装并返回
        Aggregations aggs = result.getAggregations();
        for (SpecParam specParam : specParams) {
            String name = specParam.getName();
            //以规格名称作为聚合名称，获取聚合结果
            StringTerms terms = aggs.get(name);
            //取出bucket中的结果
            List<String> options = terms.getBuckets().stream()
                    .map(bucket -> bucket.getKeyAsString())
                    .filter(val->StringUtils.isNotBlank(val) && !StringUtils.equals(val,"其他"))
                    .collect(Collectors.toList());
            Map<String, Object> map = new HashMap<>();
            map.put("k", name);
            map.put("options", options);
            filterList.add(map);
        }
    }

    private List<Long> handleBrandAgg(LongTerms terms, List<Map<String, Object>> filterList) {
        List<Long> idList = terms.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue())
                .collect(Collectors.toList());
        List<Brand> brands = brandClient.queryByIdList(idList);
        Map<String, Object> map = new HashMap<>();
        map.put("k", "品牌");
        map.put("options", brands);
        filterList.add(map);
        return idList;
    }

    private List<Long> handleCategoryAgg(LongTerms terms, List<Map<String, Object>> filterList) {
        List<Long> idList = terms.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue())
                .collect(Collectors.toList());
        List<Category> categories = categoryClient.queryByIdList(idList);
        Map<String, Object> map = new HashMap<>();
        map.put("k", "分类");
        map.put("options", categories);
        filterList.add(map);
        return idList;
    }
}
