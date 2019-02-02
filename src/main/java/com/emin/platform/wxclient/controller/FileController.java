package com.emin.platform.wxclient.controller;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.alibaba.fastjson.JSONObject;
import com.emin.base.exception.EminException;
import com.emin.platform.wxclient.interfaces.FileApiFeign;
/***
 * 文件工具类
 * @author Danica
 */
@Controller
@RequestMapping("/file")
public class FileController {

	private Logger logger = LoggerFactory.getLogger(FileController.class);

	@Autowired
	FileApiFeign fileApiFeign;//主体数据接口实现
	
	@RequestMapping("/upload.do")
	@ResponseBody
	private JSONObject upload(MultipartFile file, String type){
		JSONObject json = new JSONObject();
		String originalFilename = file.getOriginalFilename();
		String suffix = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
		boolean isLegal = true;
		//类型判断
		if(type.equalsIgnoreCase("img")){
			isLegal = !suffix.equalsIgnoreCase("jpg")&&!suffix.equalsIgnoreCase("jpeg")&&!suffix.equalsIgnoreCase("png")&&!suffix.equalsIgnoreCase("gif");
		} else if (type.equalsIgnoreCase("apk")) {
			isLegal = !suffix.equalsIgnoreCase("apk")&&!suffix.equalsIgnoreCase("wgt")&&!suffix.equalsIgnoreCase("wgtu");
		} else if (type.equalsIgnoreCase("doc")) {
			isLegal = !suffix.equalsIgnoreCase("doc")&&!suffix.equalsIgnoreCase("docx");
		} else if (type.equalsIgnoreCase("excel")) {
			isLegal = !suffix.equalsIgnoreCase("xls")&&!suffix.equalsIgnoreCase("xlsx");
		}
		
		if (isLegal) {//类型合法的情况下上传文件
			json = uploadFile(file);
		} else {
			json.put("success", false);
			json.put("message", "上传文件类型与实际需求不符");
		}
		
		return json;
	}
	/***
	 * 上传文件核心方法
	 * @param file 文件流
	 * @return
	 */
	private JSONObject uploadFile(MultipartFile file){
		JSONObject json = new JSONObject();
		try {	 
			String resStr = fileApiFeign.upload(file);
			json = JSONObject.parseObject(resStr);
			if (!json.getBooleanValue("success")) {
				throw new EminException(json.getString("code"));
			}
		} catch (EminException e) {
			logger.error(e.getLocalizedMessage(),e);
			json.put("message", e.getLocalizedMessage());
			json.put("success", false);
		}catch (Exception e) {
			logger.error(e.getMessage(),e);
			json.put("message", "文件上传失败");
			json.put("success", false);
		}
		
		return json;
	}
	
	
	
	
}
