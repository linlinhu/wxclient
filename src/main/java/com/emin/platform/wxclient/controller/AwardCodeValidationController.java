/**
 * 
 */
package com.emin.platform.wxclient.controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.emin.platform.wxclient.AwardCodeHandler;

/**
 * @author jim.lee
 *
 */
@Controller
@RequestMapping("/awardCode")
public class AwardCodeValidationController {

	private static Logger LOGGER = LoggerFactory.getLogger(AwardCodeValidationController.class);
	
	/**
	 * 此方法通过HTML5的 EventSource方法调用 自动实现了轮询  可以结合Web Worker实现后台轮询<br>
	 * e.g: <br>var source=new EventSource("http://127.0.0.1:8202/awardCode/valid?awardCode=awardCode");<br>
	 * source.onmessage = function(event){<br>
	 * 		if(event.data=="success"){<br>
     *			//已核销<br>
     *			<br>
     *          //前端处理代码...<br>
     *          <br>
     *          //关闭连接<br>
     *          source.close()<br>
     *		}<br>
	 * }
	 * @param awardCode
	 * @param response
	 * @throws IOException
	 */
	@RequestMapping(value="/valid",method=RequestMethod.GET)
	public void valid(@RequestParam(required=true) String awardCode,HttpServletResponse response) throws IOException {
		LOGGER.info("验证兑换码:"+awardCode);
		//指定返回的内容格式 必须是 text/event-stream 否则客户端接收不到
		response.setContentType(MediaType.TEXT_EVENT_STREAM_VALUE);
		response.setCharacterEncoding("UTF-8");
		PrintWriter pw = response.getWriter();
		//HTML5 EventSource接受消息的固定前缀 data:
		StringBuffer sb = new StringBuffer("data:");
		if(AwardCodeHandler.hasCode(awardCode)) {//说明此码刚才被核销了			
			//从缓存中清除
			
			sb.append(AwardCodeHandler.getCode(awardCode).toJSONString());
			
			AwardCodeHandler.removeCode(awardCode);
		}else {//还没有核销
			
			sb.append("false");
		}
		//HTML5 EventSource接受消息的固定后缀 nn
		sb.append("\n\n");
		pw.write(sb.toString());
		pw.flush();
	}
	
	
	
}
