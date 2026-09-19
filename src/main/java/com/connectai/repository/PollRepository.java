package com.connectai.repository;

import com.connectai.model.Poll;
import com.connectai.model.PollOption;
import com.connectai.model.PollVote;
import com.connectai.util.HttpClientUtil;
import com.connectai.util.JsonUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PollRepository {

    public CompletableFuture<Poll> createPoll(Poll poll, List<String> optionTexts, String accessToken) {
        Map<String, Object> body = new HashMap<>();
        body.put("message_id", poll.getMessageId());
        body.put("conversation_id", poll.getConversationId());
        body.put("created_by", poll.getCreatedBy());
        body.put("question", poll.getQuestion());
        body.put("allows_multiple_answers", poll.isAllowsMultipleAnswers());
        body.put("is_anonymous", poll.isAnonymous());
        if (poll.getClosesAt() != null) body.put("closes_at", poll.getClosesAt());

        return HttpClientUtil.postAsync("/rest/v1/polls?select=*", JsonUtil.toJson(body), accessToken)
                .thenCompose(response -> {
                    if (response.statusCode() >= 400) {
                        throw new RuntimeException("Failed to create poll: " + response.body());
                    }
                    List<Poll> list = JsonUtil.fromJsonList(response.body(), Poll.class);
                    Poll createdPoll = list.get(0);

                    // Create poll options
                    List<Map<String, Object>> optionsBody = new ArrayList<>();
                    for (int i = 0; i < optionTexts.size(); i++) {
                        optionsBody.add(Map.of(
                                "poll_id", createdPoll.getId(),
                                "option_text", optionTexts.get(i),
                                "position", i + 1
                        ));
                    }

                    return HttpClientUtil.postAsync("/rest/v1/poll_options?select=*", JsonUtil.toJson(optionsBody), accessToken)
                            .thenApply(optRes -> {
                                List<PollOption> opts = JsonUtil.fromJsonList(optRes.body(), PollOption.class);
                                createdPoll.setOptions(opts);
                                return createdPoll;
                            });
                });
    }

    public CompletableFuture<Poll> getPollDetails(String pollId, String userId, String accessToken) {
        String path = "/rest/v1/polls?id=eq." + pollId + "&select=*,options:poll_options(*),votes:poll_votes(*)";
        return HttpClientUtil.getAsync(path, accessToken)
                .thenApply(response -> {
                    if (response.statusCode() >= 400) {
                        throw new RuntimeException("Failed to load poll details");
                    }
                    List<Poll> list = JsonUtil.fromJsonList(response.body(), Poll.class);
                    if (list.isEmpty()) return null;
                    Poll poll = list.get(0);
                    calculatePollPercentagesAndVotes(poll, userId);
                    return poll;
                });
    }

    public CompletableFuture<Void> castVote(String pollId, String optionId, String userId, boolean allowsMultipleAnswers, String accessToken) {
        if (!allowsMultipleAnswers) {
            // Delete previous vote for this user on single-choice poll
            String deletePath = "/rest/v1/poll_votes?poll_id=eq." + pollId + "&user_id=eq." + userId;
            return HttpClientUtil.deleteAsync(deletePath, accessToken)
                    .thenCompose(r -> insertVote(pollId, optionId, userId, accessToken));
        } else {
            return insertVote(pollId, optionId, userId, accessToken);
        }
    }

    public CompletableFuture<Void> retractVote(String pollId, String optionId, String userId, String accessToken) {
        String path = "/rest/v1/poll_votes?poll_id=eq." + pollId + "&option_id=eq." + optionId + "&user_id=eq." + userId;
        return HttpClientUtil.deleteAsync(path, accessToken).thenApply(r -> null);
    }

    private CompletableFuture<Void> insertVote(String pollId, String optionId, String userId, String accessToken) {
        Map<String, Object> body = Map.of(
                "poll_id", pollId,
                "option_id", optionId,
                "user_id", userId
        );
        return HttpClientUtil.postAsync("/rest/v1/poll_votes", JsonUtil.toJson(body), accessToken)
                .thenApply(r -> null);
    }

    private void calculatePollPercentagesAndVotes(Poll poll, String userId) {
        if (poll.getOptions() == null) return;
        // Calculation logic for vote counts & percentages
    }
}
