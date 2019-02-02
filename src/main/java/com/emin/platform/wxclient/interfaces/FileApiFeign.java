package com.emin.platform.wxclient.interfaces;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import com.emin.platform.wxclient.config.FeignMultipartSupportConfig;


/***
 * 主体接口桥梁定义
 * @author kakadanica
 */
@FeignClient(value = "zuul",configuration = FeignMultipartSupportConfig.class)
public interface FileApiFeign {
	
	@RequestMapping(value = "/api-storage/storage/upload/uploadCompressImg",method = RequestMethod.POST,consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	String upload(@RequestPart(value="file") MultipartFile file);
	
	
	
}
