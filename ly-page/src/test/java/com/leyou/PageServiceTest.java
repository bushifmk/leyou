package com.leyou;

import com.leyou.page.service.PageService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;
@RunWith(SpringRunner.class)
@SpringBootTest
public class PageServiceTest {

    @Autowired
    private PageService pageService;
    @Test
    public void createItemHtml() throws InterruptedException {
        Long[] arr={141L,114L,96L};
        for (Long id : arr) {
            pageService.createItemHtml(id);
            Thread.sleep(1000);
        }
    }
}