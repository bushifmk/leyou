package com.leyou.common.enums;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * @author bushifeng
 * @version v1.0
 * @date 2019/3/3 16:45
 * @description TODO
 **/
@NoArgsConstructor
@AllArgsConstructor
public enum  ExceptionEnum {
    GOODS_STOCK_FOND(404,"商品没找到"),
    CATEGORY_NOT_FOND(404,"找不到类型"),
    INTERNAL_SERVER_ERROR(204,"新增品牌失败"),
    INVALID_FILE_TYPE(404,"图片类型错误"),
    UPLOAD_FILE_ERROR(500,"文件上传失败"),
    SPEC_GROUP_NOT_FOUND(404,"分组查不到"),
    SPEC_PARAM_NOT_FOUND(404,"规格参数找不到"),
    GOODS_NOT_FOUND(404,"Good找不到"),
    BRAND_NOT_FOUND(404,"商品类别找不到"),
    GOODS_SAVE_ERROR(500,"保存错误"),
    GOODS_EDIT_ERROR(500,"goods修改错误"),
    GOODS_DETAIL_NOT_FOND(404,"商品Detail没找到"),
    GOODS_SKU_NOT_FOND(404,"商品sku没有找到"),
    GOODS_STOCK_NOT_FOND(404,"商品库存找不到"),
    GOODS_ID_CANNOT_BE_NULL(500,"id不能为空"),
    GOODS_UPDATE_ERROR(500,"修改错误"),
    INVALID_PARAM_ERROR(400,"请求参数有误"),
    INSERT_DATA_ERROR(500,"注册失败"),
    INVALID_LOGIN_TOKEN(400,"无效的登陆状态"),
    FORBBIDEN(403,"禁止访问"),
    CART_NOT_FOUND(404,"购物车为空"),
    STOCK_NOT_FOUND(500,"库存不足"),
    ORDER_NOT_FOUND(404,"订单未找到"),
    WX_PAY_ORDER_FAIL(500,"微信下单失败"),
    ORDER_STATUS_ERROR(500,"订单状态异常"),
    INVALID_ORDER_PARAM(500,"金额不对"),
    UPDATE_ORDER_STATUS_ERROR(500,"订单状态错误"),
    ;
    private int status;
    private String message;
    public int status(){
        return this.status;
    }
    public String msg(){
        return this.message;
    }

}
