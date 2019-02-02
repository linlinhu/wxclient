package com.emin.platform.wxclient;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.exception.EminException;
@ControllerAdvice
public class ControllerExceptionAdvice {

	@ExceptionHandler({EminException.class})
	@ResponseBody
	public JSONObject eminExceptionHandler(EminException e){
		JSONObject json = new JSONObject();
		json.put("success", false);
		json.put("message", e.getLocalizedMessage());
		return json;
	}
	
	@ExceptionHandler({Exception.class})
	@ResponseBody
	public ModelAndView exceptionHandler(Exception e){
		ModelAndView mv = new ModelAndView("500");
		return mv;
	}
}
