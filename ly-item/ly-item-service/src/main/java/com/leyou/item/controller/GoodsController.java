package com.leyou.item.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/9 16:53
 * @description TODO
 **/
@RestController
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    @GetMapping("spu/page")
    public ResponseEntity<PageResult<Spu>> querySpuByPage(
            @RequestParam(value = "page",defaultValue = "1")Integer page,
            @RequestParam(value = "rows",defaultValue = "5")Integer rows,
            @RequestParam(value = "saleable",required = false)Boolean saleable,
            @RequestParam(value = "key",required = false)String key
    ){
        return ResponseEntity.ok(goodsService.querySpuByPage(page,rows,key,saleable));
    }
    @PostMapping("goods")
    public ResponseEntity<Void> saveGoods(@RequestBody Spu spu){
        this.goodsService.saveGoods(spu);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PutMapping("goods/{id}/{saleable}")
    public ResponseEntity<Void> updateSaleable(@PathVariable("id") Long id,@PathVariable("saleable") Boolean saleable){
        goodsService.updateSaleable(id,saleable);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @GetMapping("/spu/detail/{id}")
    public ResponseEntity<SpuDetail> querySpuDetailById(@PathVariable("id") Long id){
        return ResponseEntity.ok(goodsService.queryDetailById(id));
    }
    @GetMapping("sku/list")
    public ResponseEntity<List<Sku>> querySkuBySpuId(@RequestParam("id") Long id){
        return ResponseEntity.ok(this.goodsService.querySkuListBySpuId(id));
    }
    @PutMapping
    public ResponseEntity<Void> updateGoods(@RequestBody Spu spu){
        this.goodsService.updateGoods(spu);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @GetMapping("spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long spuId){
        return ResponseEntity.ok(goodsService.querySpuById(spuId));
    }

}
