package com.yupi.yuaiagent.demo.invoke;
//大部分可以实验JDK实现api的调用，但是少部分不行的要用cura，这里使用cura转化为hutool形式调用api
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONArray;

public class HttpAiInvoke {
    public static void main(String[] args) {
        String apiKey = TestApiKey.API_KEY; // 替换为你的实际API密钥
        
        // 构建请求体JSON
        JSONObject requestBody = new JSONObject();
        requestBody.set("model", "qwen-plus");
        
        JSONObject input = new JSONObject();
        JSONArray messages = new JSONArray();
        
        JSONObject systemMessage = new JSONObject();
        systemMessage.set("role", "system");
        systemMessage.set("content", "You are a helpful assistant.");
        messages.add(systemMessage);
        
        JSONObject userMessage = new JSONObject();
        userMessage.set("role", "user");
        userMessage.set("content", "你是谁？");
        messages.add(userMessage);
        
        input.set("messages", messages);
        requestBody.set("input", input);
        
        JSONObject parameters = new JSONObject();
        parameters.set("result_format", "message");
        requestBody.set("parameters", parameters);
        
        // 发送HTTP请求
        String response = HttpRequest.post("https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .execute()
                .body();
        
        System.out.println(response);
    }
}
