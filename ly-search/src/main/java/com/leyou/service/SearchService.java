package com.leyou.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.utils.JsonUtils;
import com.leyou.common.utils.NumberUtils;
import com.leyou.item.api.BrandClient;
import com.leyou.item.api.CategoryClient;
import com.leyou.item.api.GoodsClient;
import com.leyou.item.api.SpecClient;
import com.leyou.item.pojo.*;
import com.leyou.pojo.Goods;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
}
