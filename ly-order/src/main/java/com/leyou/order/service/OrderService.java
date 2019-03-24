package com.leyou.order.service;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.common.dto.CartDTO;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.api.GoodsClient;
import com.leyou.item.pojo.Sku;
import com.leyou.order.enums.OrderStatusEnum;
import com.leyou.order.enums.PayState;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.pojo.*;
import com.leyou.order.utils.PayHelper;
import com.leyou.order.utils.UserContainer;
import com.leyou.user.client.AddressClient;
import com.leyou.user.pojo.AddressDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/23 22:01
 * @description TODO
 **/
@Service
@Slf4j
public class OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private OrderStatusMapper statusMapper;
    @Autowired
    private IdWorker idWorker;
    @Autowired
    private AddressClient addressClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private PayHelper payHelper;
    @Autowired
    private AmqpTemplate amqpTemplate;
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
            detail.setSkuId(sku.getId());
            detail.setTitle(sku.getTitle());
            detailList.add(detail);
        }
        order.setTotalPay(total);
        order.setPaymentType(orderDTO.getPaymentType());
        order.setActualPay(total+order.getPostFee()/*减去促销金额*/);
        int count = orderMapper.insertSelective(order);
        if(count!=1){
            throw new LyException(ExceptionEnum.INSERT_DATA_ERROR);
        }
        count = orderDetailMapper.insertList(detailList);
        if(count!=detailList.size()){
            throw new LyException(ExceptionEnum.INSERT_DATA_ERROR);
        }
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCreateTime(new Date());
        orderStatus.setStatus(OrderStatusEnum.INIT.value());
        count = statusMapper.insertSelective(orderStatus);
        if(count!=1){
            throw new LyException(ExceptionEnum.INSERT_DATA_ERROR);
        }
        goodsClient.decreaseStock(carts);
        HashMap<Long, List<Long>> map = new HashMap<>();
        map.put(user.getId(), idList);
        amqpTemplate.convertAndSend("ly.order.exchange","order.verify.code",map);
        return orderId;
    }

    public Order queryById(Long id) {
        Order order = orderMapper.selectByPrimaryKey(id);
        if(order==null){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        List<OrderDetail> detailList = queryDetailByOrderId(id);
        order.setOrderDetails(detailList);
        OrderStatus status = statusMapper.selectByPrimaryKey(id);
        if(status==null){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        order.setOrderStatus(status);
        return order;
    }

    private List<OrderDetail> queryDetailByOrderId(Long id) {
        OrderDetail d = new OrderDetail();
        d.setOrderId(id);
        List<OrderDetail> list = orderDetailMapper.select(d);
        if(CollectionUtils.isEmpty(list)){
            throw new LyException(ExceptionEnum.ORDER_NOT_FOUND);
        }
        return list;
    }

    public String createPayUrl(Long orderId) {
        Order order = queryById(orderId);
        Integer status = order.getOrderStatus().getStatus();
        if(!status.equals(OrderStatusEnum.INIT.value())){
            throw new LyException(ExceptionEnum.ORDER_STATUS_ERROR);
        }
        Long actualPay=/*order.getActualPay*/1L;
        OrderDetail detail = order.getOrderDetails().get(0);
        String desc = detail.getTitle();
        return payHelper.createOrder(orderId,actualPay,desc);
    }

    @Transactional
    public void handleNotify(Map<String, String> result) {
        // 1 数据校验
        payHelper.isSuccess(result);

        // 2 校验签名
        payHelper.isValidSign(result);

        // 3 校验金额
        String totalFeeStr = result.get("total_fee");
        String tradeNo = result.get("out_trade_no");
        if(StringUtils.isEmpty(totalFeeStr) || StringUtils.isEmpty(tradeNo)){
            throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM);
        }
        // 3.1 获取结果中的金额
        Long totalFee = Long.valueOf(totalFeeStr);
        // 3.2 获取订单金额
        Long orderId = Long.valueOf(tradeNo);
        Order order = orderMapper.selectByPrimaryKey(orderId);
        if(totalFee != /*order.getActualPay()*/ 1){
            // 金额不符
            throw new LyException(ExceptionEnum.INVALID_ORDER_PARAM);
        }

        // 4 修改订单状态
        OrderStatus status = new OrderStatus();
        status.setStatus(OrderStatusEnum.PAY_UP.value());
        status.setOrderId(orderId);
        status.setPaymentTime(new Date());
        int count = statusMapper.updateByPrimaryKeySelective(status);
        if(count != 1){
            throw new LyException(ExceptionEnum.UPDATE_ORDER_STATUS_ERROR);
        }

        log.info("[订单回调], 订单支付成功! 订单编号:{}", orderId);
    }
    public Integer queryPayStatus(Long orderId) {
        // 查询订单状态
        OrderStatus orderStatus = statusMapper.selectByPrimaryKey(orderId);
        Integer status = orderStatus.getStatus();
        // 判断是否支付
        if(!status.equals(OrderStatusEnum.INIT.value()) ){
            // 如果已支付,真的是已支付
            return PayState.SUCCESS.getValue();
        }

        // 如果未支付,但其实不一定是未支付,必须去微信查询支付状态
        int value = payHelper.queryPayState(orderId).getValue();
        return value;
    }
}
