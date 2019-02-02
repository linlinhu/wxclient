package com.emin.platform.wxclient.interfaces;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "zuul")
public interface AwardRecordApiFeign {
	
	@RequestMapping(value = "/api-wxact/awardRecord/{openId}/list",method = RequestMethod.GET)
	String awardList(@RequestHeader(value="ecmId") Long ecmId,
			@PathVariable("openId") String openId,
			@RequestParam("awardType") Integer awardType,
			@RequestParam("awardStatus") Integer awardStatus,
			@RequestParam("page") Integer page,
			@RequestParam("limit") Integer limit);
	
	

	@RequestMapping(value = "/api-wxact/awardRecord/{id}/info",method = RequestMethod.GET)
	String awardInfo(@RequestHeader(value="ecmId") Long ecmId,
			@PathVariable("id") Long id);
}
