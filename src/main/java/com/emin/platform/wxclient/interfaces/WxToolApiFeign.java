package com.emin.platform.wxclient.interfaces;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(value = "zuul")
public interface WxToolApiFeign {
	
	@RequestMapping(value = "/api-wx/wxTool/{woaId}/code_user/{code}",method = RequestMethod.GET)
	String codeUser(@RequestHeader(value="ecmId") Long ecmId,
			@PathVariable("woaId") Long woaId,
			@PathVariable("code") String code);
	

	@RequestMapping(value = "/api-wx/wxTool/{woaId}/oauthUrl",method = RequestMethod.POST)
	String oauthUrl(@RequestHeader(value="ecmId") Long ecmId,
			@PathVariable("woaId") Long woaId,
			@RequestParam(value="url") String url);
	
	
	/**
	 * 获得js-sdk的配置信息
	 * @param companyCode 公众号code
	 * @param encodedURLWithParams 其它参数
	 * @return
	 */
	@RequestMapping(value = "/api-wx/wxTool/{companyCode}/configByCode",method = RequestMethod.POST)
	String wxConfig(@RequestHeader(value="ecmId") Long ecmId, @PathVariable("companyCode") String companyCode, @RequestParam(value="encodedURLWithParams") String encodedURLWithParams);
	
	/**
	 * 获得js-sdk的配置信息
	 * @param woaId 公众号id
	 * @param encodedURLWithParams 其它参数
	 * @return
	 */
	@RequestMapping(value = "/api-wx/wxTool/{woaId}/config",method = RequestMethod.POST)
	String wxConfig(@RequestHeader(value="ecmId") Long ecmId, @PathVariable(value="woaId") Long woaId, @RequestParam(value="encodedURLWithParams") String encodedURLWithParams);
	
	
	/**
	 * 将普通URL转换成微信SNSbaseUrl
	 * @param woaId 公众号ID
	 * @param url 普通url
	 * @return
	 */
	@RequestMapping(value = "/api-wx/wxTool/{woaId}/snsBaseUrl",method = RequestMethod.GET)
	String snsBaseURL(@PathVariable(value="woaId") Long woaId, @RequestParam(value="url") String url);
	
	

	
}
