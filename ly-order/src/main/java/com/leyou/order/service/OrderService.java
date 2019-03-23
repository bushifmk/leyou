package com.leyou.order.service;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.api.GoodsClient;
import com.leyou.item.pojo.Sku;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.CartDTO;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDTO;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.utils.UserContainer;
import com.leyou.user.client.AddressClient;
import com.leyou.user.pojo.AddressDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/23 22:01
 * @description TODO
 **/
@Service
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderStatusMapper orderStatusMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private AddressClient addressClient;
    @Autowired
    private GoodsClient goodsClient;
    @Transactional
    public Long createOrder(OrderDTO orderDTO) {
        Order order = new Order();
        Long addressId = orderDTO.getAddressId();
        AddressDTO addressDTO = addressClient.queryAddressById(addressId);
        BeanUtils.copyProperties(addressDTO,order);
        long orderId = idWorker.nextId();
        order.setOrderId(orderId);
        order.setCreateTime(new Date());
        UserInfo user = UserContainer.getUser();
        order.setUserId(user.getId());
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);
        List<CartDTO> carts = orderDTO.getCarts();
        Map<Long, Integer> numMap = carts.stream().collect(Collectors.toMap(CartDTO::getSkuId, CartDTO::getNum));
        List<Long> idList = carts.stream().map(CartDTO::getSkuId).collect(Collectors.toList());
        List<Sku> skus = goodsClient.querySkuByIds(idList);
        long total=0;
        ArrayList<OrderDetail> detailList = new ArrayList<>();
        for (Sku sku : skus) {
            Integer num = numMap.get(sku.getId());
            total+=sku.getPrice()*num;
            OrderDetail detail = new OrderDetail();
            detail.setId(null);
            detail.setImage(StringUtils.substringBetween(sku.getImages(),","));
            detail.setNum(num);
            detail.setOrderId(orderId);
            detail.setOwnSpec(sku.getOwnSpec());
            detail.setPrice(sku.getPrice());
            detail.setId(sku.getId());
            detail.setTitle(sku.getTitle());
            detailList.add(detail);
        }
        order.setTotalPay(total);
        order.setPaymentType(orderDTO.getPaymentType());
        order.setActualPay(total+order.getPostFee()/*减去促销金额*/);
        int count = orderMapper.insertSelective(order);
        count = orderDetailMapper.insertList(detailList);

        return null;
    }
}
