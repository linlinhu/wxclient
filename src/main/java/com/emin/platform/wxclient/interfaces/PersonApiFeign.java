package com.emin.platform.wxclient.interfaces;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.alibaba.fastjson.JSONObject;

@FeignClient(value = "zuul")
public interface PersonApiFeign {
	
	/**
	 * 用户登录
	 * @param username 账号
	 * @param password 密码
	 * @param code 验证码
	 */
	@RequestMapping(value = "/api-user/clientLogin",method = RequestMethod.POST)
	JSONObject clientLogin(@RequestParam(value="username") String username,
			@RequestParam(value="password") String password);
	
	
	/**
	 * 用户详情
	 * @param id 被查询的用户id
	 */
	@RequestMapping(value = "/api-user/member/user/detail",method = RequestMethod.GET)
	JSONObject detail(
			@RequestParam(value="id") Long id);
	
	/**
	 *用户退出登录
	 * @param token 用过户登录时获取到的token值
	 */
	@RequestMapping(value = "/api-user/outLogin",method = RequestMethod.POST)
	JSONObject outLogin(
			@RequestParam(value="token") String token);
	
	
}
