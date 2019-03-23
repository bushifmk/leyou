package com.leyou.user.client;

import com.leyou.user.pojo.AddressDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("user-service")
public interface AddressClient {

    @GetMapping("/address/{id}")
    AddressDTO queryAddressById(@PathVariable("id") Long id);
}
