package com.yupi.yuaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;

//@Component
public class OllamaAiInvoke implements CommandLineRunner {
    @Resource(name = "ollamaChatModel")
    private ChatModel ollamaChatModel;
    @Override
    public void run(String... args) throws Exception{
        AssistantMessage assistantMessage = ollamaChatModel.call(new Prompt("我是ollama代码校园")).getResult().getOutput();
        System.out.println(assistantMessage.getText());
    }

}