package com.yupi.yuaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

@Slf4j
public class MyLoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    private static final Logger logger = LoggerFactory.getLogger(MyLoggerAdvisor.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 0;
    }

    /**
     * 拦截的业务处理
     *
     * @param advisedRequest
     * @return
     */

    private AdvisedRequest before(AdvisedRequest advisedRequest) {
        //处理拦截的业务逻辑
        log.info("AI Request: {}", advisedRequest.userText());
        return advisedRequest;
    }

    private void observeAfter(AdvisedResponse advisedResponse) {
        //处理执行完成后的业务逻辑
        if (advisedResponse.response() != null) {
            log.info("AI Response--------------:{}", advisedResponse.response().getResult().getOutput().getText());
        }
    }


    @Override
    public AdvisedResponse aroundCall(AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        advisedRequest = before(advisedRequest);//调用拦截的业务

        AdvisedResponse advisedResponse = chain.nextAroundCall(advisedRequest);

        observeAfter(advisedResponse);//调用执行完成后的业务

        return advisedResponse;
    }

    @Override
    public Flux<AdvisedResponse> aroundStream(AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {

        logger.debug("BEFORE: {}", advisedRequest);

        Flux<AdvisedResponse> advisedResponses = chain.nextAroundStream(advisedRequest);

        return new MessageAggregator().aggregateAdvisedResponse(advisedResponses,
                advisedResponse -> logger.debug("AFTER: {}", advisedResponse));
    }
}