package com.leyou.order.mapper;

import com.leyou.common.mapper.BaseMapper;
import com.leyou.order.pojo.OrderDetail;
import tk.mybatis.mapper.common.special.InsertListMapper;
public interface OrderDetailMapper extends InsertListMapper<OrderDetail>, BaseMapper<OrderDetail> {
}