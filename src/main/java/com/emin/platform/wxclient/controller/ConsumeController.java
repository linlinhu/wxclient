package com.emin.platform.wxclient.controller;

import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.emin.base.controller.BaseController;
import com.emin.base.exception.EminException;
import com.emin.platform.wxclient.interfaces.ConsumeApiFeign;
import com.emin.platform.wxclient.interfaces.EcmApiFeign;
import com.emin.platform.wxclient.interfaces.PersonApiFeign;
import com.emin.platform.wxclient.interfaces.WxToolApiFeign;
/**
 * 核销流程控制
 * @author kakadanica
 *
 */
@Controller
@RequestMapping("/consume")
public class ConsumeController  extends BaseController {
	
	private static Logger logger = LoggerFactory.getLogger(ConsumeController.class);

	@Autowired
	WxToolApiFeign wxToolApiFeign;

	@Autowired
	ConsumeApiFeign consumeApiFeign;
	
	@Autowired
	PersonApiFeign personApiFeign;

	@Autowired
	EcmApiFeign ecmApiFeign;
	
	@RequestMapping("/index")
	@ResponseBody
	public ModelAndView goManage(String state, String code) {
		ModelAndView mv = new ModelAndView("modules/consume/manage");
		getRequest().getSession().setAttribute("base", getBasePath());
		if (state.indexOf("_") <= 0) {
			throw new EminException("WXCLIENT_0.0.0");
		}
		try {
			String[] ids = state.split("_");
			Long ecmId = Long.valueOf(ids[0]);
			Long woaId = Long.valueOf(ids[1]);
			mv.addObject("ecmId", ecmId);
			String res = wxToolApiFeign.codeUser(ecmId, woaId, code);
			if (res != null) {
				JSONObject json = JSONObject.parseObject(res);
				String openId = json.getString("openid");
				mv.addObject("openId", openId);
				String cbRes = consumeApiFeign.checkBind(ecmId, openId);
				if (cbRes != null) {
					JSONObject cb = JSONObject.parseObject(cbRes);
					if (!cb.getBooleanValue("success")) {
						throw new EminException(cb.getString("code"));
					}
					mv.addObject("checkBind", cb.getBooleanValue("result"));
				}
				
			}
			mv.addObject("wxConfig", wxToolApiFeign.wxConfig(ecmId, woaId, URLEncoder.encode(getFullRequestUrl(), "UTF-8")));
		} catch (EminException ee) {
			logger.info(ee.getLocalizedMessage());
			ee.printStackTrace();
			mv.addObject("eMsg", ee.getLocalizedMessage() == null ? "服务器离家出走了" : ee.getLocalizedMessage());
		} catch (Exception e) {
			logger.info(e.getLocalizedMessage());
			e.printStackTrace();
			mv.addObject("eMsg", "和服务器失去了连接！");
		}
		return mv;
    }
	
	@RequestMapping("/awardInfo")
	@ResponseBody
	public ModelAndView awardInfo() {
		ModelAndView mv = new ModelAndView("modules/consume/awardInfo");
		return mv;
    }
	
	@RequestMapping("/getAwardInfo")
	@ResponseBody
	public JSONObject getAwardInfo(Long ecmId, String awardCode){
		JSONObject json = new JSONObject();
		try {
			if (awardCode.length() != 12 || !awardCode.matches("[0-9]{1,}")) {
				json.put("success", false);
				json.put("message", "检测到券码非法！");
			} else {
				String res = consumeApiFeign.awardInfo(ecmId, awardCode);
				if (res != null) {
					json = JSONObject.parseObject(res);
					if (!json.getBooleanValue("success")) {
						throw new EminException(json.getString("code"));
					}
				}
			}
		} catch (EminException ee) {
			logger.info(ee.getLocalizedMessage());
			ee.printStackTrace();
			json.put("message", ee.getLocalizedMessage() == null ? "服务器离家出走了" : ee.getLocalizedMessage());
		} catch (Exception e) {
			logger.info(e.getLocalizedMessage());
			e.printStackTrace();
			json.put("message", "和服务器失去了连接！");
		}
		return json;
    }
	
	@RequestMapping("/consumeLogin")
	@ResponseBody
	public JSONObject consumeLogin(String name, String pwd, Long ecmId, String openId){
		JSONObject json = new JSONObject(); 
		try {
			String res = "";
			JSONObject awardConsumer = new JSONObject(); // 核销人信息
			String awardConsumeInfo = ""; // 转String
			Long personId = null; // 登录用户id
			
			json = personApiFeign.clientLogin(name, pwd); // 客户端登录
			if (json.getBooleanValue("success")) {
				personId = json.getJSONObject("data").getLong("id"); // 登录人员编号
				JSONObject person = personApiFeign.detail(personId);
				JSONObject consumerInfo = new JSONObject();
				consumerInfo.put("person", person.getJSONObject("data"));
				ecmId = person.getJSONObject("data").getLong("ecmId");
				res = ecmApiFeign.detail(ecmId);
				if (res != null) {
					if (JSONObject.parseObject(res).getBooleanValue("success")) {
						consumerInfo.put("ecm", JSONObject.parseObject(res).getJSONObject("result")); // 根据接口要求 数据库保存需要，主体详情
					}
				}
				// 根据接口定义组装核销人员信息
				awardConsumer.put("openId", openId);
				awardConsumer.put("ecmId", ecmId);
				awardConsumer.put("consumerInfo", consumerInfo); // 传递到后台log化
				awardConsumeInfo = JSONObject.toJSONString(awardConsumer);
				
				// 核销员绑定
				res = consumeApiFeign.bind(ecmId, awardConsumeInfo); 
				
				if (res != null) {
					json = JSONObject.parseObject(res);
					if (! json.getBooleanValue("success")) {
						throw new EminException(json.getString("code"));
					}
				}
			}
		} catch (EminException ee) {
			logger.info(ee.getLocalizedMessage());
			ee.printStackTrace();
			json.put("message", ee.getLocalizedMessage() == null ? "服务器离家出走了" : ee.getLocalizedMessage());
		} catch (Exception e) {
			logger.info(e.getLocalizedMessage());
			e.printStackTrace();
			json.put("message", "和服务器失去了连接！");
		}
		return json;
    }
	
	@RequestMapping("/consumeLogout")
	@ResponseBody
	public JSONObject consumeLogout(Long ecmId, String openId){
		JSONObject json = new JSONObject(); 
		String res = "";
		try {
			res = consumeApiFeign.unBind(ecmId, openId);
			if (res != null) {
				json = JSONObject.parseObject(res);
				if (!json.getBooleanValue("success")) {
					throw new EminException(json.getString("code"));
				}
			}
		} catch (EminException ee) {
			logger.info(ee.getLocalizedMessage());
			ee.printStackTrace();
			json.put("message", ee.getLocalizedMessage() == null ? "服务器离家出走了" : ee.getLocalizedMessage());
		} catch (Exception e) {
			logger.info(e.getLocalizedMessage());
			e.printStackTrace();
			json.put("message", "和服务器失去了连接！");
		}
		return json;
    }
	
	@RequestMapping("/consumeAward")
	@ResponseBody
	public JSONObject consumeAward(Long ecmId, String openId, String awardCode){
		JSONObject json = new JSONObject();
		try {
			String res = consumeApiFeign.consumeAward(ecmId, awardCode, openId);
			if (res != null) {
				json = JSONObject.parseObject(res);
				if (!json.getBooleanValue("success")) {
					throw new EminException(json.getString("code"));
				}
			}
		} catch (EminException ee) {
			logger.info(ee.getLocalizedMessage());
			ee.printStackTrace();
			json.put("message", ee.getLocalizedMessage() == null ? "服务器离家出走了" : ee.getLocalizedMessage());
		} catch (Exception e) {
			logger.info(e.getLocalizedMessage());
			e.printStackTrace();
			json.put("message", "和服务器失去了连接！");
		}
		return json;
    }
}
