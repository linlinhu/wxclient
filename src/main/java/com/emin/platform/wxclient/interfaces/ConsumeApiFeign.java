package com.emin.platform.wxclient.interfaces;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "zuul")
public interface ConsumeApiFeign {
	
	@RequestMapping(value = "/api-wxact/consume/{openId}/checkBind",method = RequestMethod.GET)
	String checkBind(@RequestHeader(value="ecmId") Long ecmId,
			@PathVariable("openId") String openId);
	

	@RequestMapping(value = "/api-wxact/consume/{awardCode}/awardInfo",method = RequestMethod.GET)
	String awardInfo(@RequestHeader(value="ecmId") Long ecmId,
			@PathVariable("awardCode") String awardCode);
	
	@RequestMapping(value = "/api-wxact/consume/consumeAward",method = RequestMethod.POST)
	String consumeAward(@RequestHeader(value="ecmId") Long ecmId,
			@RequestParam("awardCode") String awardCode,
			@RequestParam("openId") String openId);
	

	@RequestMapping(value = "/api-wxact/consume/bind",method = RequestMethod.POST,consumes= {"application/json"})
	String bind(@RequestHeader(value="ecmId") Long ecmId,
			@RequestBody String awardConsumer);
	
	@RequestMapping(value = "/api-wxact/consume/unBind",method = RequestMethod.POST,consumes= {"application/json"})
	String unBind(@RequestHeader(value="ecmId") Long ecmId,
			@RequestParam("openId") String openId);
}
