package com.emin.platform.wxclient.interfaces;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "zuul")
public interface WxoaApiFeign {
	
	@RequestMapping(value = "/api-wx/woa/woalist",method = RequestMethod.GET)
	String list(@RequestHeader(value="ecmId") Long ecmId);
	
	
	@RequestMapping(value = "/api-wx/woa/{id}/info",method = RequestMethod.GET)
	String detail(@RequestHeader(value="ecmId") Long ecmId,
			@PathVariable("id") Long id);
	

	@RequestMapping(value = "/api-wx/woa/disable",method = RequestMethod.GET)
	String disable(@RequestHeader(value="ecmId") Long ecmId,
			@RequestParam(value="id") Long id);
	
	

	@RequestMapping(value = "/api-wx/woa/enable",method = RequestMethod.POST)
	String enable(@RequestHeader(value="ecmId") Long ecmId,
			@RequestParam(value="id") Long id);
	

	@RequestMapping(value = "/api-wx/woa/createOrUpdate",method = RequestMethod.POST,consumes= {"application/json"})
	String save(@RequestHeader(value="ecmId") Long ecmId,
			@RequestBody String wxOfficialAccount);
}
