package com.emin.platform.wxclient.controller;

import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.controller.BaseController;
import com.emin.base.exception.EminException;
import com.emin.platform.wxclient.interfaces.CodeApiFeign;
import com.emin.platform.wxclient.interfaces.ConsumeApiFeign;
import com.emin.platform.wxclient.interfaces.FansApiFeign;
import com.emin.platform.wxclient.interfaces.PrizeDrawApiFeign;
import com.emin.platform.wxclient.interfaces.WxToolApiFeign;
import com.emin.platform.wxclient.interfaces.WxactivityApiFeign;
import com.emin.platform.wxclient.interfaces.WxoaApiFeign;

/**
 * 扫码刮奖流程控制
 * @author kakadanica
 *
 */
@Controller
@RequestMapping("/scanResult")
public class ScanResultController  extends BaseController {
	private static Logger logger = LoggerFactory.getLogger(ScanResultController.class);
	@Autowired
	CodeApiFeign codeApiFeign;
	@Autowired
	WxactivityApiFeign wxactivityApiFeign;
	@Autowired
	WxToolApiFeign wxToolApiFeign;
	@Autowired	
	PrizeDrawApiFeign prizeDrawApiFeign;
	@Autowired
	WxoaApiFeign wxoaApiFeign;
	@Autowired
	ConsumeApiFeign consumeApiFeign;
	@Autowired
	FansApiFeign fansApiFeign;
	
	/**
	 * 扫码赢取奖品
	 * @param productCode 产品码（首次和第二次进入必需参数）
	 * 测试码：
	 * 1512371170730144504G=EUo86671
	 * 1512371170730144504lYPd162992
	 * @param ecmId 根据产品码获得主体编号（第二次进入必需参数）
	 * @param woaId 根据产品码获得公众号编号（第二次进入必需参数）
	 * @param wxActivityId 根据产品码获得活动编号（第二次进入获取奖品详情信息的必需参数）
	 * @param code 微信生成的唯一标志（第二次进入获取用户信息的必需参数）
	 * @return
	 */
	@RequestMapping("/index/{productCode}")
	@ResponseBody
	public ModelAndView goManage(
			@PathVariable(required=true) String productCode, // 产品码
			Long ecmId, // 主体编号
			Long wxActivityId, // 活动编号
			Long woaId, // 公众号编号
			String code // oauthUrl唯一标志
	) {
		ModelAndView mv = new ModelAndView("modules/scanResult/manage");
		getRequest().getSession().setAttribute("base", getBasePath());
		try {
			if (ecmId== null || wxActivityId == null || code == null || woaId == null) {
				//根据瓶盖二维码获取产品所属主体信息，产品发货单信息
				String logisticsRes = codeApiFeign.queryLogisticsByCode(productCode);
				if (logisticsRes != null) {
					JSONObject logisticsJSON = JSONObject.parseObject(logisticsRes);
					if (!logisticsJSON.getBooleanValue("success")) {
						throw new EminException(logisticsJSON.getString("code"));
					}
					JSONArray logistic = logisticsJSON.getJSONArray("result");
					
					if (logistic != null) {
						String receiveSN = "";
						for (int i = 0; i < logistic.size(); i++) {
							ecmId = logistic.getJSONObject(i).getLong("ecmId");
							receiveSN = logistic.getJSONObject(i).getString("taskNumber");
							if (ecmId != null && receiveSN != null) {
								//根据产品主体和发货单信息获取产品活动信息，产品活动所属公众号
								String waRes = wxactivityApiFeign.getInfoByReceiveSN(ecmId, receiveSN);
								if (waRes != null) {
									JSONObject waResJSON = JSONObject.parseObject(waRes);
									if (!waResJSON.getBooleanValue("success")) {
										throw new EminException(waResJSON.getString("code"));
									}
									JSONObject wxActivity = waResJSON.getJSONObject("result");
									if (wxActivity != null) {
										wxActivityId = wxActivity.getLong("id");
										woaId = wxActivity.getLong("woaId");
										ecmId = wxActivity.getLong("ecmId");
									} else {
										throw new EminException("SR_0.0.1");
									}
									
									//跳转到前端页面根据公众号编号，主体编号，将抽奖url路径转换为微信可识别的路径（该路径携带生成的code参数）
									mv.addObject("productCode", productCode); // 产品二维码
									mv.addObject("wxActivityId", wxActivityId); //微信活动id
									break;
								}
							}
						}
						
					}
				}
			} else {
				mv.addObject("wxConfig", wxToolApiFeign.wxConfig(ecmId, woaId, URLEncoder.encode(getFullRequestUrl(), "UTF-8")));
				// 根据主体信息，公众号信息，微信跳转路径的code，取得用户基本信息（openid）
				String codeUserRes = wxToolApiFeign.codeUser(ecmId, woaId, code);
				if (codeUserRes != null) {
					JSONObject codeUser = JSONObject.parseObject(codeUserRes);
					
					String openId = null;
					if (codeUser != null) {
						openId = codeUser.getString("openid");
					}
					if (openId != null) {
						// 获取刮奖信息
						String  srRes = prizeDrawApiFeign.awardDraw(ecmId, productCode, openId, wxActivityId);
						if (srRes != null) {
							JSONObject srJSON = JSONObject.parseObject(srRes);
							if (!srJSON.getBooleanValue("success")) {
								throw new EminException(srJSON.getString("code"));
							}
							JSONObject sr = srJSON.getJSONObject("result");
							boolean isLucky = false;
							String scratchUrl = getBasePath() + "img/unlucky.png";
							String luckyFlag = "unLucky";
							if (sr != null) {
								isLucky = sr.getBooleanValue("isLucky");
							}
							if (isLucky) {
								luckyFlag = "lucky";
								JSONObject srPicUrls = sr.getJSONObject("awardInfo").getJSONObject("awardInfo").getJSONObject("picUrls");
								if (srPicUrls != null) {
									JSONArray storages = srPicUrls.getJSONArray("storage");
									JSONObject normalPic = storages.getJSONObject(2);
									scratchUrl = normalPic.getString("fileStorageUrl");
								} else {
									mv.addObject("eMsg", "奖品图片不存在！");
								}
							} else {
								
								String reason = sr.getString("reason");
								if (reason.equals("ACTIVITY_NOT_START")) {
									mv.addObject("eMsg", "活动未开始！");
								} else if (reason.equals("ACTIVITY_ALREADY_FINISHED")) {
									mv.addObject("eMsg", "活动已结束！");
								} else if (reason.equals("PRIZE_PULLED_OUT")) {
									mv.addObject("eMsg", "奖品已被抽完！");
								}
							}

							mv.addObject("awardId", sr.getLong("awardId"));
							// 奖品图片
							mv.addObject("luckyFlag", luckyFlag);
							mv.addObject("scratchUrl", scratchUrl);
						}
						

						String bindRes = fansApiFeign.detailByOpenId(ecmId, woaId, openId);
						if (bindRes != null) {
							JSONObject bindJson = JSONObject.parseObject(bindRes);
							if (!bindJson.getBooleanValue("success")) {
								throw new EminException(bindJson.getString("code"));
							}
							boolean checkBind = bindJson.getJSONObject("result").getBooleanValue("subscribe");
							if (!checkBind) {
								String woaRes = wxoaApiFeign.detail(ecmId, woaId); // 获取公众号信息
								if (woaRes != null) {
									JSONObject woaJson = JSONObject.parseObject(woaRes);
									if (!woaJson.getBooleanValue("success")) {
										throw new EminException(woaJson.getString("code"));
									}
									mv.addObject("woa", woaJson.getJSONObject("result"));
								}
							}
							mv.addObject("checkBind", checkBind);
							
						}
					}
				}
			}
		} catch (EminException ee) {
			logger.info(ee.getLocalizedMessage());
			ee.printStackTrace();
			mv.addObject("eMsg", ee.getLocalizedMessage() == null ? "服务器离家出走了" : ee.getLocalizedMessage());
		} catch (Exception e) {
			logger.info(e.getLocalizedMessage());
			e.printStackTrace();
			mv.addObject("eMsg", "和服务器失去了连接！");
		}
		mv.addObject("ecmId", ecmId);
		mv.addObject("woaId", woaId);
		return mv;
    }
	
	/**
	 * 奖品详情
	 * @param ecmId 主体编号
	 * @param awardId 奖品编号
	 * @param woaId 主体编号
	 * @return
	 */
	@RequestMapping("/detail")
	@ResponseBody
	public ModelAndView goDetail(Long ecmId, Long awardId, Long woaId, boolean checkBind){
		ModelAndView mv = new ModelAndView("modules/scanResult/detail");
		try {
			if (ecmId != null && awardId != null) {

				String res = prizeDrawApiFeign.detail(ecmId, awardId);// 获取奖品信息
				if (res != null) {
					JSONObject json = JSONObject.parseObject(res);
					if (!json.getBooleanValue("success")) {
						throw new EminException(json.getString("code"));
					}
					mv.addObject("sr", json.getJSONObject("result"));
				} else {
					mv.addObject("eMsg", "奖品信息获取失败！");
				}
				if (woaId != null) {
					String woaRes = wxoaApiFeign.detail(ecmId, woaId); // 获取公众号信息
					if (woaRes != null) {
						JSONObject woaJson = JSONObject.parseObject(woaRes);
						if (!woaJson.getBooleanValue("success")) {
							throw new EminException(woaJson.getString("code"));
						}
						mv.addObject("woa", woaJson.getJSONObject("result"));
					} else {
						mv.addObject("eMsg", "公众号信息获取失败！");
					}
					
				}
			}
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
}
