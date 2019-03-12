package com.leyou;

import com.leyou.item.api.CategoryClient;
import com.leyou.item.pojo.Category;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/12 18:18
 * @description TODO
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class CategoryClientTest {
    @Autowired
    private CategoryClient categoryClient;
    @Test
    public void queryByIds(){
        List<Category> categories = categoryClient.queryByIdList(Arrays.asList(1L, 2L, 3L));
        for (Category category : categories) {
            System.out.println("category = " + category);
        }
    }

}
