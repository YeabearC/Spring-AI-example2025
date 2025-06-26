package app;

import com.yupi.yuaiagent.app.HappyApp;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
public class HappyAppTest {
    @Resource
    private HappyApp happyApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();

        //第一轮
        String message = "你好，我是代码校园";
        String response = happyApp.chat(message, chatId);
        Assertions.assertNotNull(response);


        //第二轮
        message = "我想让我的另一半（编程校园），更喜欢我";
        response = happyApp.chat(message, chatId);
        Assertions.assertNotNull(response);


        //第三轮
        message = "我的另一半叫什么名字，刚刚我讲过的，你帮我回忆一下";
        response = happyApp.chat(message, chatId);
        Assertions.assertNotNull(response);

    }
}