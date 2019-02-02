package com.emin.platform.wxclient.interfaces;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "zuul")
public interface PrizeDrawApiFeign {
	
	@RequestMapping(value = "/api-wxact/prizeDraw/{code}/{openId}/draw",method = RequestMethod.GET)
	String awardDraw(@RequestHeader(value="ecmId") Long ecmId,
			@PathVariable("code") String code,
			@PathVariable("openId") String openId,
			@RequestParam(value="wxActivityId") Long wxActivityId);
	

	@RequestMapping(value = "/api-wxact/prizeDraw/getAward",method = RequestMethod.POST)
	String detail(@RequestHeader(value="ecmId") Long ecmId,
			@RequestParam(value="awardId") Long awardId);
	
}
