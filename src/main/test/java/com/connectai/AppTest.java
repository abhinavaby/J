package com.connectai;

import com.connectai.config.AppConfig;
import com.connectai.model.ConversationType;
import com.connectai.model.Message;
import com.connectai.model.MessageType;
import com.connectai.model.User;
import com.connectai.util.JsonUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AppTest {

    @Test
    public void testConfigLoader() {
        assertNotNull(AppConfig.getAppName());
        assertEquals("ConnectAI", AppConfig.getAppName());
    }

    @Test
    public void testUserJsonSerialization() {
        User user = new User("u-123", "test@connectai.com", "token-abc", "refresh-xyz");
        String json = JsonUtil.toJson(user);
        assertNotNull(json);

        User deserialized = JsonUtil.fromJson(json, User.class);
        assertEquals(user.getId(), deserialized.getId());
        assertEquals(user.getEmail(), deserialized.getEmail());
    }

    @Test
    public void testMessageModel() {
        Message msg = new Message();
        msg.setId("msg-1");
        msg.setMessageType(MessageType.TEXT);
        msg.setContent("Hello ConnectAI");

        assertEquals("msg-1", msg.getId());
        assertEquals(MessageType.TEXT, msg.getMessageType());
        assertFalse(msg.isDeleted());
    }
}
