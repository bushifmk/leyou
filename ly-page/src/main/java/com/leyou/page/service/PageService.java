package com.leyou.page.service;

import com.leyou.item.api.BrandClient;
import com.leyou.item.api.CategoryClient;
import com.leyou.item.api.GoodsClient;
import com.leyou.item.api.SpecClient;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.Spu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/15 18:51
 * @description TODO
 **/
@Service
@Slf4j
public class PageService {
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecClient specClient;
    @Autowired
    private SpringTemplateEngine templateEngine;

    public Map<String, Object> loadItemData(Long spuId) {
        //查询spu
        Spu spu = goodsClient.querySpuById(spuId);
        //查询分类
        List<Category> categories = categoryClient.queryByIdList(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //查询品牌
        Brand brand = brandClient.queryById(spu.getBrandId());
        //查询规格参数
        List<SpecGroup> specs = specClient.queryListByCid(spu.getCid3());
        //准备模型数据
        Map<String, Object> map = new HashMap<>();
        map.put("categories", categories);
        map.put("brand",brand );
        map.put("title", spu.getTitle());
        map.put("subTitle", spu.getSubTitle());
        map.put("detail",spu.getSpuDetail() );
        map.put("skus", spu.getSkus());
        map.put("specs",specs );
        return map;

    }

    public void createItemHtml(Long spuId){
        Context context = new Context();
        context.setVariables(loadItemData(spuId));
        File file = getFilePath(spuId);
        try (PrintWriter writer=new PrintWriter(file,"UTF-8")){
            templateEngine.process("item",context,writer);
        }catch (IOException e){
            log.error("【静态页服务】创建商品静态页失败，商品id：{}", spuId,e);
            throw new RuntimeException(e);
        }

    }

    private File getFilePath(Long spuId) {
        File dir = new File("C:\\nginx-1.14.0\\nginx-1.14.0\\nginx-1.14.0\\html\\item");
        if(!dir.exists()){
            dir.mkdirs();
        }
        return new File(dir, spuId + ".html");
    }


    public void deleteItemHtml(Long id) {
        File file = getFilePath(id);
        if(file.exists()){
            file.delete();
        }
    }
}
