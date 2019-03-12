package com.leyou;

import com.leyou.common.vo.PageResult;
import com.leyou.item.api.GoodsClient;
import com.leyou.item.pojo.Spu;
import com.leyou.pojo.Goods;
import com.leyou.repository.GoodsRepository;
import com.leyou.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoadDataTest {

    @Autowired
    private SearchService searchService;

    @Autowired
    private GoodsRepository repository;

    @Autowired
    private GoodsClient goodsClient;

    @Test
    public void loadData() {
        int page = 1, rows = 100;
        do {
            try {

                // 查询spu
                PageResult<Spu> result = goodsClient.querySpuByPage(page, rows, true, null);
                // 取出spu
                List<Spu> items = result.getItems();
                // 转换
                List<Goods> goodsList = items
                        .stream().map(searchService::buildGoods)
                        .collect(Collectors.toList());

                repository.saveAll(goodsList);
                page++;


            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        } while (true);
    }
}