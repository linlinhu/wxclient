package com.emin.platform.wxclient.interfaces;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(value = "zuul")
public interface FansApiFeign {

	@RequestMapping(value = "/api-wx/fans/{woaId}/{openId}/info",method = RequestMethod.GET)
	String detailByOpenId(@RequestHeader(value="ecmId") Long ecmId,
			@PathVariable("woaId") Long woaId,
			@PathVariable("openId") String openId);
	

	@RequestMapping(value = "/api-wx/fans/{woaId}/{unionId}/info_unionId",method = RequestMethod.GET)
	String detailByUnionId(@RequestHeader(value="ecmId") Long ecmId,
			@PathVariable("woaId") Long woaId,
			@PathVariable("unionId") String unionId);
	
}
