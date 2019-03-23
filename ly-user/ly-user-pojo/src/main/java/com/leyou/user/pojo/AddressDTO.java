package com.leyou.user.pojo;

import lombok.Data;

@Data
public class AddressDTO {
    private Long id;
    private Long userId;
    private String receiver; // 收货人全名
    private String receiverMobile; // 移动电话
    private String receiverState; // 省份
    private String receiverCity; // 城市
    private String receiverDistrict; // 区/县
    private String receiverAddress; // 收货地址，如：xx路xx号
    private String receiverZip; // 邮政编码,如：310001
    private Boolean isDefault;
}
