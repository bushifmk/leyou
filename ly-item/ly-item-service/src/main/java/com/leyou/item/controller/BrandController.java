package com.leyou.item.controller;

import com.leyou.common.vo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/4 17:26
 * @description TODO
 **/
@RestController
@RequestMapping("brand")
public class BrandController {
    @Autowired
    private BrandService brandService;
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy",required = false) String sortBy,
            @RequestParam(value = "desc",defaultValue = "false") Boolean desc,
            @RequestParam(value = "key",required = false) String key){

        return ResponseEntity.ok(brandService.queryByPage(page,rows,sortBy,desc,key));
    }
    @PostMapping
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam("cids")List<Long> cids){
        this.brandService.saveBrand(brand,cids);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Void> updateBrand(Brand brand){
        this.brandService.updateBrandById(brand);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @GetMapping("cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid")Long cid){
        return ResponseEntity.ok(brandService.queryBrandByCid(cid));
    }

    @GetMapping("{id}")
    public ResponseEntity<Brand> queryById(@PathVariable("id") Long id){
        return ResponseEntity.ok(brandService.queryById(id));
    }
    @GetMapping("list")
    public ResponseEntity<List<Brand>> queryByIds(@RequestParam("ids")List<Long> idList){
        return ResponseEntity.ok(brandService.queryByIds(idList));
    }
}




