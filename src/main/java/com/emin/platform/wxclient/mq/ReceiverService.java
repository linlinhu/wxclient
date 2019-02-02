package com.emin.platform.wxclient.mq;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.emin.platform.wxclient.AwardCodeHandler;
import com.emin.platform.wxclient.Constant;

/**
 * 接受兑换码核销通知的消息队列
 * @author jim.lee
 *
 */
@Component
@RabbitListener(queues = Constant.CONSUME_QUEUE_NAME)
public class ReceiverService {

   
    /**
     * 
     * @param awardCode 被核销的兑换码
     */
    @RabbitHandler
    private void awardConsumed(String result) {
    	//将被核销的兑换码加入缓存队列 等待验证
    	JSONObject res = JSONObject.parseObject(result);
        AwardCodeHandler.addCode(res.getString("awardCode"),res);
    }
}
