package com.emin.platform.wxclient.interfaces;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "zuul")
public interface CodeApiFeign {
	
	@RequestMapping(value = "/api-code/queryLogisticsByCode",method = RequestMethod.GET)
	String queryLogisticsByCode(@RequestParam(value="code") String code);
	
}
