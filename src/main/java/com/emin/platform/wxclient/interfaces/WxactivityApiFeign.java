package com.emin.platform.wxclient.interfaces;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(value = "zuul")
public interface WxactivityApiFeign {
	
	@RequestMapping(value = "/api-wxact/wxactivity/{receiveSN}/info_by_receiveSN",method = RequestMethod.GET)
	String getInfoByReceiveSN(@RequestHeader(value="ecmId") Long ecmId,
			@PathVariable("receiveSN") String receiveSN);
	

	
}
