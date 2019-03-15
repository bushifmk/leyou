package com.leyou.item.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/6 21:57
 * @description TODO
 **/
@Service
public class SpecService {
    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamMapper paramMapper;

    public List<SpecGroup> queryGroupCid(Long cid){
        SpecGroup g = new SpecGroup();
        g.setCid(cid);
        List<SpecGroup> list = specGroupMapper.select(g);
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOUND);
        }
        return list;
    }

    public List<SpecParam> queryParams(Long gid, Long cid, Boolean searching) {
        // 查询条件
        SpecParam param = new SpecParam();
        param.setGroupId(gid);
        param.setCid(cid);
        param.setSearching(searching);

        // 查询
        List<SpecParam> list = paramMapper.select(param);
        if(CollectionUtils.isEmpty(list)){
            // 没查到
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOUND);
        }
        return list;
    }

    public List<SpecGroup> queryListByCid(Long cid) {
        List<SpecGroup> specGroups = queryGroupCid(cid);
        List<SpecParam> specParams = queryParams(null, cid, null);
        Map<Long, List<SpecParam>> map = specParams.stream().collect(Collectors.groupingBy(SpecParam::getGroupId));
        for (SpecGroup specGroup : specGroups) {
            specGroup.setParams(map.get(specGroup.getId()));
        }
        return specGroups;
    }
}
