package com.emin.platform.wxclient.interfaces;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@FeignClient(value = "zuul")
public interface AwardApiFeign {
	
	@RequestMapping(value = "/api-wxact/award/{id}/info",method = RequestMethod.GET)
	String awardInfo(@RequestHeader(value="ecmId") Long ecmId,
			@PathVariable("id") Long id);
}
