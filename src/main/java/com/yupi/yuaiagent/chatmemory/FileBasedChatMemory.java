package com.yupi.yuaiagent.chatmemory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import org.objenesis.strategy.StdInstantiatorStrategy;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class FileBasedChatMemory implements ChatMemory {

    private static final Kryo kryo = new Kryo();

    static {
        kryo.setRegistrationRequired(false);
        //定义实例化策略
        kryo.setInstantiatorStrategy(new StdInstantiatorStrategy());
    }

    private final String BASE_DIR;

    public FileBasedChatMemory(String dir) {
        this.BASE_DIR = dir;
        File baseDir = new File(BASE_DIR);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
    }

    public void add(String conversationId, List<Message> message) {
        List<Message> conversationMessages = getOrCreateConversation(conversationId);
        conversationMessages.addAll(message);
        saveConversation(conversationId, conversationMessages);
    }

    public List<Message> get(String conversationId, int lastN) {
        List<Message> conversationMessages = getOrCreateConversation(conversationId);
        return conversationMessages.stream().skip(Math.max(0, conversationMessages.size() - lastN)).toList();
    }

    public void clear(String conversationId) {
        File file = getConversationFile(conversationId);
        if (file.exists()) {
            file.delete();
        }
    }

    private List<Message> getOrCreateConversation(String conversationId) {
        List<Message> messages = new ArrayList<>();
        File file = getConversationFile(conversationId);
        if (file.exists()) {
            try (Input input = new Input(new FileInputStream(file))) {
                messages = kryo.readObject(input, ArrayList.class);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return messages;
    }

    private File getConversationFile(String conversationId) {
        return new File(BASE_DIR, conversationId + ".kryo");
    }

    private void saveConversation(String conversationId, List<Message> conversationMessages) {
        File file = getConversationFile(conversationId);
        try (Output output = new Output(new FileOutputStream(file))) {
            kryo.writeObject(output, conversationMessages);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}