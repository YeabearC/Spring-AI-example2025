package com.yupi.yuaiagent.demo.invoke;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.boot.CommandLineRunner;

//注释掉则不会在启动时运行
//@Component
public class SpringAiInvoke implements CommandLineRunner {

    @Resource(name = "dashscopeChatModel")
    private ChatModel chatModel;

    @Override
    public void run(String... args) throws Exception {
        AssistantMessage output = chatModel.call(new Prompt("你好")).getResult().getOutput();
        System.out.println(output.getText());
    }
}
