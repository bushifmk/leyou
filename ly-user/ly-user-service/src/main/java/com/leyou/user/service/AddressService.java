package com.leyou.user.service;

import com.leyou.user.pojo.AddressDTO;
import org.springframework.stereotype.Service;

@Service
public class AddressService {
    public AddressDTO queryAddressById(Long id) {
        AddressDTO address = new AddressDTO();
        address.setId(1L);
        address.setReceiverAddress("航头镇航头路18号传智播客 3号楼");
        address.setReceiverCity("上海");
        address.setReceiverDistrict("浦东新区");
        address.setReceiver("虎哥");
        address.setReceiverMobile("15800000000");
        address.setReceiverState("上海");
        address.setReceiverZip("210000");
        address.setIsDefault(true);
        return address;
    }
}