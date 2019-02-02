package com.emin.platform.wxclient;

import org.springframework.amqp.core.Queue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class WxclientApplication {
	public static void main(String[] args) {
		SpringApplication.run(WxclientApplication.class, args);
	}
	
	@Bean
    public Queue createAwardCodeQueue() {
        return new Queue(Constant.CONSUME_QUEUE_NAME);
    }
}
