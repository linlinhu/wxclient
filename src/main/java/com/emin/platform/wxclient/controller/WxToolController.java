package com.emin.platform.wxclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.controller.BaseController;
import com.emin.base.exception.EminException;
import com.emin.platform.wxclient.interfaces.WxToolApiFeign;

@Controller
@RequestMapping("/wxTool")
public class WxToolController  extends BaseController {
	@Autowired
	WxToolApiFeign wxToolApiFeign;
	
	@RequestMapping("/{woaId}/oauthUrl")
	@ResponseBody
	public JSONObject oauthUrl(@PathVariable(required=true) Long woaId, 
			Long ecmId,
			String url){
		JSONObject json = new JSONObject();
		if (ecmId != null) {
			String res = wxToolApiFeign.oauthUrl(ecmId, woaId, url);
			
			if (res != null) {
				json = JSONObject.parseObject(res);
				if (!json.getBooleanValue("success")) {
					throw new EminException(json.getString("code"));
				}
			}
		}
		return json;
    }
}
