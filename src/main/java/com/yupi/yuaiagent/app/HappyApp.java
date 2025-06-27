package com.yupi.yuaiagent.app;

import com.yupi.yuaiagent.advisor.MyLoggerAdvisor;
import com.yupi.yuaiagent.advisor.ReReadingAdvisor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class HappyApp {


    private static final String SYSTEM_PROMPT = "扮演情感心理领域的专家，开场向用户表明身份，告知能解决用户的情况问题。引用用户详述事情经过，对方反应及自身想法，以便给出解决方案";
    private final ChatClient chatClient;


    private SimpleLoggerAdvisor simpleLoggerAdvisor;

    public HappyApp(ChatModel dashscopeChatModel) {
        ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatModel).defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
//                      new SimpleLoggerAdvisor() // 6.26 用于打印日志，输出我们需要的DEBUG日志
                        new MyLoggerAdvisor() //6.27 自己定义的拦截器
//                        new ReReadingAdvisor()
                )
                .build();
    }

    public String chat(String message, String chatId) {
        ChatResponse response = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 1))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("context:{}", content);
        return content;

    }
// 6.27 实现一个指定格式化的响应输出
    record ActorsFilms(String actors, List<String> movies){
    }//这段定义告诉模型："我要你输出一个格式，包含一个 actors 字符串和一个 movies 列表"。

    public ActorsFilms chatReport(String message, String chatId) {
        //Spring AI 框架背后其实会借助大模型对结构提示的理解能力，让它输出一个可以被 JSON 映射成 ActorsFilms 对象的结果
        ActorsFilms actorsFilms = chatClient.prompt()
                .system(SYSTEM_PROMPT+"扮演情感心理领域的专家")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(ActorsFilms.class);
        log.info("ActorsFilms:{}", actorsFilms);
        return actorsFilms;
    }

}
