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
    PRICE_CANNOT_BE_NULL(400, "价格不能为空！"),
    CATEGORY_NOT_FOND(404,"找不到类型"),
    INTERNAL_SERVER_ERROR(204,"新增品牌失败"),
    INVALID_FILE_TYPE(404,"图片类型错误"),
    FILE_UPLOAD_ERROR(404,"上传失败"),
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
    SEND_MSG_ERROR(500,"短信发送失败"),
    INVALID_PARAM_ERROR(400,"请求参数有误"),
    INSERT_DATA_ERROR(500,"注册失败"),
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
