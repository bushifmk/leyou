package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/6 21:55
 * @description TODO
 **/
@RestController
@RequestMapping("spec")
public class SpecController {
    @Autowired
    private SpecService specService;
    @GetMapping("/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupByCid(@PathVariable("cid")Long cid){
        return ResponseEntity.ok(specService.queryGroupCid(cid));
    }

    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> queryParamByGid(@RequestParam(value = "gid",required = false) Long gid,
    @RequestParam(value = "cid",required = false)Long cid,
    @RequestParam(value = "searching",required = false)Boolean searching
    ){
        return ResponseEntity.ok(specService.queryParams(gid,cid,searching));
    }
    @GetMapping("list")
    public ResponseEntity<List<SpecGroup>> queryListByCid(@RequestParam("cid") Long cid){
        return ResponseEntity.ok(specService.queryListByCid(cid));
    }

}
