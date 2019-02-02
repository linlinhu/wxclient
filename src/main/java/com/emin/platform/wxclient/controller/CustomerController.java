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
import com.emin.platform.wxclient.interfaces.AwardApiFeign;
import com.emin.platform.wxclient.interfaces.AwardRecordApiFeign;
import com.emin.platform.wxclient.interfaces.FansApiFeign;
import com.emin.platform.wxclient.interfaces.WxToolApiFeign;
import com.emin.platform.wxclient.interfaces.WxoaApiFeign;

/**
 * 客户中心流程控制
 * @author kakadanica
 *
 */
@Controller
@RequestMapping("/customer")
public class CustomerController  extends BaseController {
	
	private static Logger logger = LoggerFactory.getLogger(CustomerController.class);

	@Autowired
	WxToolApiFeign wxToolApiFeign;

	@Autowired
	FansApiFeign fansApiFeign;
	

	@Autowired
	AwardRecordApiFeign awardRecordApiFeign;

	@Autowired
	AwardApiFeign awardApiFeign;
	
	@Autowired
	WxoaApiFeign wxoaApiFeign;
	
	/**
	 * 客户中心
	 * @param state
	 * @param code
	 * @return 用户基本信息，微信配置信息
	 */
	@RequestMapping("/index")
	@ResponseBody
	public ModelAndView goManage(String state, String code) {
		ModelAndView mv = new ModelAndView("modules/customer/manage");
		getRequest().getSession().setAttribute("base", getBasePath());
		if (state.indexOf("_") <= 0) {
			throw new EminException("WXCLIENT_0.0.0");
		}
		try {
			String[] ids = state.split("_");
			Long ecmId = Long.valueOf(ids[0]);
			Long woaId = Long.valueOf(ids[1]);
			mv.addObject("ecmId", ecmId);
			mv.addObject("woaId", woaId);
			String res = wxToolApiFeign.codeUser(ecmId, woaId, code);
			if (res != null) {
				JSONObject json = JSONObject.parseObject(res);
				String openId = json.getString("openid");
				mv.addObject("openId", openId);
				res = fansApiFeign.detailByOpenId(ecmId, woaId, openId);
				if (res != null) {
					json = JSONObject.parseObject(res);
					if (!json.getBooleanValue("success")) {
						throw new EminException(json.getString("code"));
					}
					mv.addObject("userinfo", json.getJSONObject("result"));
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
	
	
	/**
	 * 我的优惠券页面跳转
	 * @return
	 */
	@RequestMapping("/myCoupons")
	@ResponseBody
	public ModelAndView myCoupons(){
		ModelAndView mv = new ModelAndView("modules/customer/myCoupons");
		return mv;
    }
	
	/**
	 * 我的奖券页面跳转
	 * @return
	 */
	@RequestMapping("/myLotteries")
	@ResponseBody
	public ModelAndView myLotteries(){
		ModelAndView mv = new ModelAndView("modules/customer/myLotteries");
		return mv;
    }
	
	/**
	 * 我的已使用券页面跳转
	 * @return
	 */
	@RequestMapping("/myExchangeds")
	@ResponseBody
	public ModelAndView myExchangeds(){
		ModelAndView mv = new ModelAndView("modules/customer/myExchangeds");
		return mv;
    }
	
	/**
	 * 奖券详情页面跳转
	 * @return
	 */
	@RequestMapping("/awardDetail")
	@ResponseBody
	public ModelAndView awardDetail(){
		ModelAndView mv = new ModelAndView("modules/customer/benefitDetail");
		return mv;
    }
	
	/**
	 * 奖券列表分页查询
	 * @param ecmId
	 * @param openId
	 * @param awardType
	 * @return
	 */
	@RequestMapping("/awardList")
	@ResponseBody
	public JSONObject awardList(Long ecmId, String openId, Integer awardType){
		JSONObject json = new JSONObject();
		try {
			String res = "";
			Integer page = getPageRequestData().getCurrentPage();
			Integer awardStatus = 0;
			if (awardType == 0) {
				awardType = null;
				awardStatus = 1;
			}
			res = awardRecordApiFeign.awardList(ecmId, openId, awardType, awardStatus, page, getPageRequestData().getLimit());
			if (res != null) {
				json = JSONObject.parseObject(res);
				if (!json.getBooleanValue("success")) {
					throw new EminException(json.getString("code"));
				}
			}
			json.getJSONObject("result").put("ecmId", ecmId);
	
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
	
	/**
	 * 获取奖券详情
	 * @param ecmId
	 * @param id
	 * @param woaId
	 * @return
	 */
	@RequestMapping("/getAwardInfo")
	@ResponseBody
	public JSONObject getAwardInfo(Long ecmId, Long id, Long woaId){
		JSONObject json = new JSONObject();
		JSONObject woa = new JSONObject();
		try {
			String res = awardRecordApiFeign.awardInfo(ecmId, id);
			if (res != null) {
				json = JSONObject.parseObject(res);
				if (!json.getBooleanValue("success")) {
					throw new EminException(json.getString("code"));
				}
			}
			String woaRes = wxoaApiFeign.detail(ecmId, woaId);
			if (woaRes != null) {
				woa = JSONObject.parseObject(woaRes);
				if (!woa.getBooleanValue("success")) {
					throw new EminException(woa.getString("code"));
				}
			}
			json.getJSONObject("result").put("woa", woa.getJSONObject("result"));
		
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
