package com.leyou.page.web;

import com.leyou.page.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/15 16:15
 * @description TODO
 **/
@Controller
public class PageController {

    @Autowired
    private PageService pageService;
    @GetMapping("item/{id}.html")
    public String toItemHtml(@PathVariable("id") Long spuId,Model model){
        Map<String,Object> itemData= pageService.loadItemData(spuId);
        model.addAllAttributes(itemData);
        return "item";
    }













    @GetMapping("hello")
    public String hello(Model model){
        model.addAttribute("msg","hello,thymeleaf");
        return "hello";
    }
}
