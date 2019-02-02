package com.emin.platform.wxclient.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import com.emin.base.controller.BaseController;

@RestController
public class IndexController extends BaseController {
	
	
	@ResponseBody
	@RequestMapping(value = "/",method = RequestMethod.GET)
	public ModelAndView index(HttpServletRequest request) throws UnsupportedEncodingException{
		ModelAndView mv = new ModelAndView("index");
		mv.addObject("base", getBasePath());
		return mv;
	}
	
}
