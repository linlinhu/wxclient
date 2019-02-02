package com.emin.platform.wxclient.interfaces;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


/***
 * 主体接口桥梁定义
 * @author kakadanica
 */
@FeignClient(value = "zuul")
public interface EcmApiFeign {
	
	/**
	 * 分页查询主体信息
	 * @param pageRequest 分页基本字段
	 * @param ecmIndustories 行业编号
	 * @param keyword 关键字
	 * @return
	 */
	@RequestMapping(value = "/api-ecm/ecm/queryPage",method = RequestMethod.GET)
	String getPages(@RequestParam(value="page") Integer page,
			@RequestParam(value="limit") Integer limit,
			@RequestParam(value="industryId") String industryId,
			@RequestParam(value="keyword") String keyword);
	
	/**
	 * 查询主体详情
	 * @param id 主体id
	 * @return
	 */
	@RequestMapping(value = "/api-ecm/ecm/queryDetail",method = RequestMethod.GET)
	String detail(@RequestParam(value="id") Long id);
	

	/**
	 * 保存主体信息
	 * @param ecmStr 主体信息对象json字符串
	 * @return
	 */
	@RequestMapping(value = "/api-ecm/ecm/save",method = RequestMethod.POST)
	String save(@RequestParam(value="ecmStr") String ecmStr);
	
	/**
	 * 禁用主体
	 * @param ids 主体id，多个用逗号分隔，必填
	 * @return
	 */
	@RequestMapping(value = "/api-ecm/ecm/disable",method = RequestMethod.GET)
	String disable(@RequestParam(value="ids") String ids);
	
	/**
	 * 启用主体信息
	 * @param ids 主体id，多个用逗号分隔，必填
	 * @return
	 */
	@RequestMapping(value = "/api-ecm/ecm/activate",method = RequestMethod.GET)
	String activate(@RequestParam(value="ids") String ids);
	
	
	@RequestMapping(value = "/api-ecm/ecm/findByKeyword",method = RequestMethod.GET)
	String getList(@RequestParam(value="keyword") String keyword);
	

	@RequestMapping(value = "/api-ecm/ecm/deleteEcmById",method = RequestMethod.GET)
	String delete(@RequestParam(value="ecmId") Long ecmId);
	
	
	
	
}
