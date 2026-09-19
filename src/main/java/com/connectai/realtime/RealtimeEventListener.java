package com.connectai.realtime;

import com.connectai.model.Message;
import com.connectai.model.MessageReaction;
import com.connectai.model.PollVote;

public interface RealtimeEventListener {
    default void onNewMessage(Message message) {}
    default void onMessageEdited(Message message) {}
    default void onMessageDeleted(String messageId) {}
    default void onReactionUpdated(MessageReaction reaction) {}
    default void onPollVoted(PollVote vote) {}
    default void onTypingStatusChanged(String conversationId, String userId, boolean isTyping) {}
    default void onUserPresenceChanged(String userId, boolean isOnline) {}
    default void onConnectionStateChanged(boolean isConnected) {}
}
