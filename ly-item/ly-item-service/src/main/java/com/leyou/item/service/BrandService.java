package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.vo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Category;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.awt.*;
import java.util.List;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/4 18:44
 * @description TODO
 **/
@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryByPage(Integer page, Integer rows, String sortBy, Boolean desc, String key) {
        // 分页
        PageHelper.startPage(page, rows);
        // 搜索过滤
        Example example = new Example(Brand.class);
        if (StringUtils.isNotBlank(key)) {
            Example.Criteria criteria = example.createCriteria();
            // 对名称模糊查询
            criteria.orLike("name", "%" + key + "%");
            if (key.length() == 1) {
                // 对首字母精确匹配
                criteria.orEqualTo("letter", key);
            }
        }
        // 排序
        if (StringUtils.isNotBlank(sortBy)) {
            String orderByClause = sortBy + (desc ? " DESC" : " ASC");
            example.setOrderByClause(orderByClause);
        }
        // 查询结果
        List<Brand> brands = brandMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(brands)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOND);
        }
        // 封装并返回
        PageInfo<Brand> info = new PageInfo<>(brands);
        return new PageResult<>(info.getTotal(), brands);
    }

    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        brand.setId(null);
        int count = brandMapper.insert(brand);
        if (count==0) {
            throw new LyException(ExceptionEnum.INTERNAL_SERVER_ERROR);
        }
        for (Long cid : cids) {
            brandMapper.saveCategoryBrand(cid,brand.getId());
            if(count==0){
                throw new LyException(ExceptionEnum.INTERNAL_SERVER_ERROR);
            }
        }
    }


    public void updateBrandById(Brand brand) {
        int count = brandMapper.updateByPrimaryKey(brand);
        if(count==0){
            throw new LyException(ExceptionEnum.INTERNAL_SERVER_ERROR);
        }
    }

    public Brand queryById(Long id) {
        Brand brand = brandMapper.selectByPrimaryKey(id);
        if(brand==null){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return brand;
    }

    public List<Brand> queryBrandByCid(Long cid) {
        List<Brand> list= brandMapper.queryByCategoryId(cid);
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return list;
    }

    public List<Brand> queryByIds(List<Long> idList) {
        List<Brand> list = brandMapper.selectByIdList(idList);
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.BRAND_NOT_FOUND);
        }
        return list;
    }
}
