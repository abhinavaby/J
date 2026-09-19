package com.connectai.mock;

import com.connectai.model.*;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides comprehensive in-memory mock data and state management for offline exploration.
 */
public class MockDataProvider {
    private static final MockDataProvider instance = new MockDataProvider();

    private final User currentUser;
    private final Profile currentProfile;
    private final Map<String, Profile> profiles = new LinkedHashMap<>();
    private final Map<String, Conversation> conversations = new LinkedHashMap<>();
    private final Map<String, List<Message>> messagesByConversation = new ConcurrentHashMap<>();
    private final Map<String, Poll> polls = new ConcurrentHashMap<>();

    private MockDataProvider() {
        // 1. Initialize Current User & Profile (Alex Rivera)
        String currentUserId = "user-alex-123";
        this.currentUser = new User(currentUserId, "alex.rivera@connectai.io", "mock-access-token", "mock-refresh-token");
        this.currentProfile = new Profile(currentUserId, "alexrivera", "Alex Rivera (You)", null);
        this.currentProfile.setAbout("Senior Product Manager | Building AI-first desktop experiences 🚀");
        this.currentProfile.setOnline(true);
        this.currentProfile.setLastSeen(Instant.now().toString());

        profiles.put(currentUserId, currentProfile);

        // 2. Initialize Teammate Profiles
        Profile botProfile = new Profile("bot-connectai", "connectai_bot", "ConnectAI Assistant 🤖", null);
        botProfile.setAbout("Your AI Companion for summarizing, rephrasing & chat intelligence ⚡");
        botProfile.setOnline(true);
        profiles.put(botProfile.getId(), botProfile);

        Profile sarahProfile = new Profile("user-sarah-456", "sjenkins", "Sarah Jenkins", null);
        sarahProfile.setAbout("Lead UI/UX Designer 🎨 | Java Swing & FlatLaf Fanatic");
        sarahProfile.setOnline(true);
        profiles.put(sarahProfile.getId(), sarahProfile);

        Profile davidProfile = new Profile("user-david-789", "dchen", "David Chen", null);
        davidProfile.setAbout("Principal Backend Engineer 🛠️ | Supabase & Realtime WebSockets");
        davidProfile.setOnline(true);
        profiles.put(davidProfile.getId(), davidProfile);

        Profile elenaProfile = new Profile("user-elena-101", "elena_r", "Elena Rostova", null);
        elenaProfile.setAbout("AI/ML Researcher 🤖 | Prompt Engineering & LLMs");
        elenaProfile.setOnline(false);
        profiles.put(elenaProfile.getId(), elenaProfile);

        Profile marcusProfile = new Profile("user-marcus-202", "marcus_v", "Marcus Vance", null);
        marcusProfile.setAbout("VP of Engineering 💼");
        marcusProfile.setOnline(true);
        profiles.put(marcusProfile.getId(), marcusProfile);

        // 3. Initialize Conversations & Messages
        initAiBotChat();
        initTechTeamGroupChat();
        initSarahDirectChat();
        initDavidDirectChat();
        initDesignGuildGroupChat();
    }

    public static MockDataProvider getInstance() {
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public List<ConversationMember> getUserMemberships() {
        List<ConversationMember> members = new ArrayList<>();
        for (Conversation conv : conversations.values()) {
            ConversationMember cm = new ConversationMember();
            cm.setConversationId(conv.getId());
            cm.setUserId(currentUser.getId());
            cm.setProfile(conv.getDirectPartnerProfile() != null ? conv.getDirectPartnerProfile() : null);
            cm.setConversation(conv);
            cm.setRole(Role.MEMBER);
            members.add(cm);
        }
        return members;
    }

    public Conversation getConversation(String id) {
        return conversations.get(id);
    }

    public List<Message> getMessages(String conversationId) {
        return messagesByConversation.getOrDefault(conversationId, new ArrayList<>());
    }

    public synchronized Message sendMessage(String conversationId, String content, String replyToId) {
        Message msg = new Message();
        msg.setId(UUID.randomUUID().toString());
        msg.setConversationId(conversationId);
        msg.setSenderId(currentUser.getId());
        msg.setSenderProfile(currentProfile);
        msg.setMessageType(MessageType.TEXT);
        msg.setContent(content);
        msg.setReplyToMessageId(replyToId);
        msg.setStatus(MessageStatus.SENT);
        msg.setCreatedAt(Instant.now().toString());

        if (replyToId != null) {
            List<Message> list = messagesByConversation.get(conversationId);
            if (list != null) {
                list.stream().filter(m -> m.getId().equals(replyToId)).findFirst().ifPresent(msg::setReplyMessage);
            }
        }

        List<Message> list = messagesByConversation.computeIfAbsent(conversationId, k -> new ArrayList<>());
        list.add(0, msg); // Add at top (most recent first)

        Conversation conv = conversations.get(conversationId);
        if (conv != null) {
            conv.setLatestMessage(msg);
            conv.setUpdatedAt(msg.getCreatedAt());
        }

        // Auto-reply for AI Bot conversation
        if ("conv-ai-bot".equals(conversationId)) {
            triggerAiBotReply(content);
        }

        return msg;
    }

    private void triggerAiBotReply(String userPrompt) {
        new Thread(() -> {
            try {
                Thread.sleep(600);
            } catch (InterruptedException ignored) {}

            Profile bot = profiles.get("bot-connectai");
            Message botReply = new Message();
            botReply.setId(UUID.randomUUID().toString());
            botReply.setConversationId("conv-ai-bot");
            botReply.setSenderId(bot.getId());
            botReply.setSenderProfile(bot);
            botReply.setMessageType(MessageType.TEXT);
            botReply.setContent(generateAiBotResponse(userPrompt));
            botReply.setStatus(MessageStatus.SENT);
            botReply.setCreatedAt(Instant.now().toString());

            List<Message> list = messagesByConversation.get("conv-ai-bot");
            if (list != null) {
                list.add(0, botReply);
            }

            Conversation conv = conversations.get("conv-ai-bot");
            if (conv != null) {
                conv.setLatestMessage(botReply);
                conv.setUpdatedAt(botReply.getCreatedAt());
            }
        }).start();
    }

    private String generateAiBotResponse(String prompt) {
        String lower = prompt.toLowerCase();
        if (lower.contains("summary") || lower.contains("summarize")) {
            return "💡 You can generate an AI summary for any chat thread by clicking the '⚡ AI Summary' button in the top header bar!";
        } else if (lower.contains("rephrase") || lower.contains("tone")) {
            return "✨ To rephrase text into Professional, Casual, or Concise tone, click the '✨ AI Rephrase' button in the composer bar below.";
        } else if (lower.contains("poll") || lower.contains("vote")) {
            return "📊 You can create live polls in group chats! Click the '📊' icon next to the composer text field to publish a custom poll.";
        } else if (lower.contains("voice") || lower.contains("audio")) {
            return "🎤 ConnectAI supports voice notes! Click the microphone button '🎤' to record and play audio messages inline.";
        } else {
            return "🤖 I'm your ConnectAI assistant! Feel free to test sending text messages, recording voice notes, creating polls, or clicking AI rephrase options to explore the layout.";
        }
    }

    public synchronized Poll votePoll(String pollId, String optionId, boolean allowsMultiple) {
        Poll poll = polls.get(pollId);
        if (poll == null) return null;

        String userId = currentUser.getId();
        boolean hasVotedThisOption = false;

        for (PollOption opt : poll.getOptions()) {
            if (opt.getId().equals(optionId)) {
                if (opt.getUserVotes() != null && opt.getUserVotes().contains(userId)) {
                    hasVotedThisOption = true;
                    opt.getUserVotes().remove(userId);
                    opt.setVoteCount(Math.max(0, opt.getVoteCount() - 1));
                } else {
                    if (opt.getUserVotes() == null) opt.setUserVotes(new ArrayList<>());
                    opt.getUserVotes().add(userId);
                    opt.setVoteCount(opt.getVoteCount() + 1);
                }
            } else if (!allowsMultiple && opt.getUserVotes() != null && opt.getUserVotes().contains(userId)) {
                opt.getUserVotes().remove(userId);
                opt.setVoteCount(Math.max(0, opt.getVoteCount() - 1));
            }
        }

        // Recalculate total votes and percentages
        int totalVotes = 0;
        for (PollOption opt : poll.getOptions()) {
            totalVotes += opt.getVoteCount();
        }
        poll.setTotalVotes(totalVotes);

        for (PollOption opt : poll.getOptions()) {
            opt.setPercentage(totalVotes > 0 ? (double) opt.getVoteCount() / totalVotes * 100.0 : 0.0);
            opt.setHasVoted(opt.getUserVotes() != null && opt.getUserVotes().contains(userId));
        }

        poll.setUserVoted(!hasVotedThisOption);
        return poll;
    }

    public synchronized Message createPoll(String conversationId, String question, List<String> options, boolean allowsMultiple, boolean isAnonymous) {
        String messageId = UUID.randomUUID().toString();
        String pollId = UUID.randomUUID().toString();

        Poll poll = new Poll();
        poll.setId(pollId);
        poll.setMessageId(messageId);
        poll.setConversationId(conversationId);
        poll.setQuestion(question);
        poll.setCreatedBy(currentUser.getId());
        poll.setAllowsMultipleAnswers(allowsMultiple);
        poll.setAnonymous(isAnonymous);
        poll.setTotalVotes(0);

        List<PollOption> optionList = new ArrayList<>();
        for (int i = 0; i < options.size(); i++) {
            PollOption opt = new PollOption();
            opt.setId("opt-" + pollId + "-" + (i + 1));
            opt.setPollId(pollId);
            opt.setOptionText(options.get(i));
            opt.setVoteCount(0);
            opt.setPercentage(0.0);
            opt.setUserVotes(new ArrayList<>());
            optionList.add(opt);
        }
        poll.setOptions(optionList);
        polls.put(pollId, poll);

        Message msg = new Message();
        msg.setId(messageId);
        msg.setConversationId(conversationId);
        msg.setSenderId(currentUser.getId());
        msg.setSenderProfile(currentProfile);
        msg.setMessageType(MessageType.POLL);
        msg.setContent("Poll: " + question);
        msg.setPoll(poll);
        msg.setStatus(MessageStatus.SENT);
        msg.setCreatedAt(Instant.now().toString());

        List<Message> list = messagesByConversation.computeIfAbsent(conversationId, k -> new ArrayList<>());
        list.add(0, msg);

        return msg;
    }

    public synchronized Conversation createGroup(String name, String description, boolean isPrivate) {
        String convId = "conv-group-" + UUID.randomUUID().toString().substring(0, 6);
        Conversation c = new Conversation();
        c.setId(convId);
        c.setName(name);
        c.setDescription(description);
        c.setType(ConversationType.GROUP);
        c.setPrivate(isPrivate);
        c.setInviteCode(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        c.setCreatedBy(currentUser.getId());
        c.setCreatedAt(Instant.now().toString());

        List<ConversationMember> members = new ArrayList<>();
        ConversationMember m = new ConversationMember();
        m.setConversationId(convId);
        m.setUserId(currentUser.getId());
        m.setProfile(currentProfile);
        m.setRole(Role.OWNER);
        members.add(m);

        c.setMembers(members);
        conversations.put(convId, c);

        Message welcomeMsg = new Message();
        welcomeMsg.setId(UUID.randomUUID().toString());
        welcomeMsg.setConversationId(convId);
        welcomeMsg.setSenderId(currentUser.getId());
        welcomeMsg.setSenderProfile(currentProfile);
        welcomeMsg.setMessageType(MessageType.SYSTEM);
        welcomeMsg.setContent("Group created: " + name);
        welcomeMsg.setStatus(MessageStatus.SENT);
        welcomeMsg.setCreatedAt(Instant.now().toString());

        messagesByConversation.computeIfAbsent(convId, k -> new ArrayList<>()).add(welcomeMsg);
        c.setLatestMessage(welcomeMsg);

        return c;
    }

    public synchronized Conversation joinGroupByInviteCode(String code) {
        for (Conversation c : conversations.values()) {
            if (c.getType() == ConversationType.GROUP && c.getInviteCode() != null && c.getInviteCode().equalsIgnoreCase(code.trim())) {
                return c;
            }
        }
        // Fallback: create mock joined group
        return createGroup("Joined Group (" + code.toUpperCase() + ")", "Joined via invite code " + code, false);
    }

    public List<ConversationMember> getConversationMembers(String conversationId) {
        Conversation c = conversations.get(conversationId);
        if (c != null && c.getMembers() != null) {
            return c.getMembers();
        }

        List<ConversationMember> members = new ArrayList<>();
        for (Profile p : profiles.values()) {
            ConversationMember m = new ConversationMember();
            m.setConversationId(conversationId);
            m.setUserId(p.getId());
            m.setProfile(p);
            m.setRole(p.getId().equals(currentUser.getId()) ? Role.OWNER : Role.MEMBER);
            members.add(m);
        }
        return members;
    }

    public AISummaryResponse summarize(String conversationId) {
        Conversation c = conversations.get(conversationId);
        String name = c != null ? c.getDisplayTitle() : "Conversation";

        AISummaryResponse res = new AISummaryResponse();
        res.setSummary("Executive Summary for " + name + ":\n\n" +
                "1. Key Discussion Points:\n" +
                "   • Evaluated AI model options (GPT-4o mini, Claude 3.5 Sonnet, Llama 3) for real-time text rephrasing.\n" +
                "   • Validated Java Swing FlatLaf Dark palette theme design and glassmorphic info sidebar.\n" +
                "   • Completed Supabase Realtime schema migration and tested WebSocket voice notes.\n\n" +
                "2. Decisions Made:\n" +
                "   • GPT-4o mini selected as primary rephrasing model due to sub-200ms latency.\n" +
                "   • Approved rounded layout theme with 12px component border radius.\n\n" +
                "3. Action Items:\n" +
                "   • David: Deploy Supabase Edge Functions with OpenAI API key.\n" +
                "   • Sarah: Finalize asset package for attachment dialogs.\n" +
                "   • Alex: Perform end-to-end UX layout testing across macOS & Windows.");
        res.setBulletPoints(List.of(
                "Selected GPT-4o mini for fast text rephrasing",
                "Finalized FlatLaf dark theme & component tokens",
                "Supabase WebSocket migration completed successfully",
                "Voice note recording & attachment dialogs verified"
        ));
        return res;
    }

    public AIRephraseResponse rephrase(String text, Tone tone) {
        String tName = tone != null ? tone.getDisplayName() : "Professional";
        AIRephraseResponse res = new AIRephraseResponse();
        res.setOriginalText(text);

        if (tone == Tone.CASUAL) {
            res.setRephrasedText("Hey! " + text + " Let me know what you think!");
        } else if (tone == Tone.CONCISE) {
            res.setRephrasedText(text.replaceAll("(?i)would you mind if|i was thinking that|as per our discussion", "").trim());
        } else if (tone == Tone.ENTHUSIASTIC) {
            res.setRephrasedText("🚀 " + text + " Really excited about this update!");
        } else if (tone == Tone.POLITE) {
            res.setRephrasedText("Kindly note: " + text + ". Thank you for your support!");
        } else {
            res.setRephrasedText("Regarding your inquiry: " + text + ". Please review at your earliest convenience.");
        }

        res.setAlternatives(List.of(
                res.getRephrasedText(),
                "Alternative Option 1: " + text,
                "Alternative Option 2: " + text + " (Optimized)"
        ));
        return res;
    }

    // --- Private Helper Seeding Methods ---

    private void initAiBotChat() {
        String id = "conv-ai-bot";
        Profile bot = profiles.get("bot-connectai");

        Conversation c = new Conversation();
        c.setId(id);
        c.setType(ConversationType.DIRECT);
        c.setDirectPartnerProfile(bot);
        c.setCreatedAt(Instant.now().toString());
        conversations.put(id, c);

        List<Message> list = new ArrayList<>();

        Message m1 = createMsg(id, bot, "Hello Alex! 👋 Welcome to **ConnectAI** desktop messenger. I am your built-in AI Assistant companion.\n\nYou can chat with me, test sending messages, or try the **AI Rephrase** and **AI Summary** features without needing a Supabase backend connection!", "2026-09-19T10:00:00Z");
        Message m2 = createMsg(id, currentProfile, "Thanks! How do I rephrase text or summarize discussions?", "2026-09-19T10:01:00Z");
        Message m3 = createMsg(id, bot, "It's super easy!\n\n1. **AI Rephrase**: Click the '✨ AI Rephrase' icon in the message composer toolbar to rewrite draft messages into Professional, Casual, or Concise tone.\n2. **AI Summary**: Click '⚡ AI Summary' at the top of any chat header to generate bulleted highlights.", "2026-09-19T10:02:00Z");

        list.add(m3);
        list.add(m2);
        list.add(m1);

        messagesByConversation.put(id, list);
        c.setLatestMessage(m3);
    }

    private void initTechTeamGroupChat() {
        String id = "conv-tech-team";

        Conversation c = new Conversation();
        c.setId(id);
        c.setName("Tech & AI Product Team ⚡");
        c.setDescription("Core architecture, Java Swing UI components & Supabase Edge Functions");
        c.setType(ConversationType.GROUP);
        c.setPrivate(false);
        c.setInviteCode("AI-TECH26");
        c.setPinned(true);
        c.setCreatedAt(Instant.now().toString());

        List<ConversationMember> members = new ArrayList<>();
        for (Profile p : profiles.values()) {
            ConversationMember cm = new ConversationMember();
            cm.setConversationId(id);
            cm.setUserId(p.getId());
            cm.setProfile(p);
            cm.setRole(p.getId().equals("user-marcus-202") ? Role.OWNER : Role.MEMBER);
            members.add(cm);
        }
        c.setMembers(members);
        conversations.put(id, c);

        List<Message> list = new ArrayList<>();

        Profile marcus = profiles.get("user-marcus-202");
        Profile elena = profiles.get("user-elena-101");
        Profile david = profiles.get("user-david-789");
        Profile sarah = profiles.get("user-sarah-456");

        Message m1 = createMsg(id, marcus, "Team, we need to finalize our default AI rephrasing model for the v1.0 release.", "2026-09-19T11:00:00Z");
        Message m2 = createMsg(id, elena, "I've benchmarked Latency vs Quality across GPT-4o mini, Claude 3.5 Sonnet, and self-hosted Llama 3 70B.", "2026-09-19T11:02:00Z");

        // Poll message
        String pollId = "poll-ai-model-1";
        Poll poll = new Poll();
        poll.setId(pollId);
        poll.setQuestion("Which AI model should we default to for fast text rephrasing?");
        poll.setConversationId(id);
        poll.setCreatedBy(elena.getId());
        poll.setTotalVotes(6);

        PollOption opt1 = new PollOption("opt-1", pollId, "GPT-4o Mini (Ultra low latency ~180ms)", 3, 50.0);
        opt1.setUserVotes(new ArrayList<>(List.of(currentProfile.getId(), marcus.getId(), sarah.getId())));
        opt1.setHasVoted(true);

        PollOption opt2 = new PollOption("opt-2", pollId, "Claude 3.5 Sonnet (Highest tone accuracy)", 2, 33.3);
        opt2.setUserVotes(new ArrayList<>(List.of(elena.getId(), david.getId())));

        PollOption opt3 = new PollOption("opt-3", pollId, "Llama 3 70B (Self-hosted privacy option)", 1, 16.7);
        opt3.setUserVotes(new ArrayList<>(List.of(david.getId())));

        poll.setOptions(List.of(opt1, opt2, opt3));
        poll.setUserVoted(true);
        polls.put(pollId, poll);

        Message mPoll = new Message();
        mPoll.setId(UUID.randomUUID().toString());
        mPoll.setConversationId(id);
        mPoll.setSenderId(elena.getId());
        mPoll.setSenderProfile(elena);
        mPoll.setMessageType(MessageType.POLL);
        mPoll.setContent("Poll: " + poll.getQuestion());
        mPoll.setPoll(poll);
        mPoll.setStatus(MessageStatus.SENT);
        mPoll.setCreatedAt("2026-09-19T11:05:00Z");

        // Voice note message from David
        Message mAudio = new Message();
        mAudio.setId(UUID.randomUUID().toString());
        mAudio.setConversationId(id);
        mAudio.setSenderId(david.getId());
        mAudio.setSenderProfile(david);
        mAudio.setMessageType(MessageType.AUDIO);
        mAudio.setContent("Voice note: Supabase WebSocket Latency test results");
        mAudio.setCreatedAt("2026-09-19T11:10:00Z");

        Attachment attAudio = new Attachment();
        attAudio.setOriginalFilename("voice_note_1110.wav");
        attAudio.setMimeType("audio/wav");
        attAudio.setDurationSeconds(14);
        attAudio.setFileSize(224000);
        mAudio.setAttachment(attAudio);

        // Code snippet message from Sarah
        Message mCode = createMsg(id, sarah, "Here is our custom FlatLaf dark palette property initialization code:\n\n```java\nFlatDarkLaf.setup();\nUIManager.put(\"Component.accentColor\", ThemeColors.PRIMARY_ACCENT);\nUIManager.put(\"Panel.background\", ThemeColors.MAIN_BG);\n```", "2026-09-19T11:15:00Z");

        // Outgoing message from Alex with reactions
        Message mOut = createMsg(id, currentProfile, "Looks clean and responsive! I've pinned this group for quick access.", "2026-09-19T11:20:00Z");
        addReaction(mOut, "👍", 4, true);
        addReaction(mOut, "🔥", 5, false);

        list.add(mOut);
        list.add(mCode);
        list.add(mAudio);
        list.add(mPoll);
        list.add(m2);
        list.add(m1);

        messagesByConversation.put(id, list);
        c.setLatestMessage(mOut);
    }

    private void initSarahDirectChat() {
        String id = "conv-sarah";
        Profile sarah = profiles.get("user-sarah-456");

        Conversation c = new Conversation();
        c.setId(id);
        c.setType(ConversationType.DIRECT);
        c.setDirectPartnerProfile(sarah);
        c.setCreatedAt(Instant.now().toString());
        conversations.put(id, c);

        List<Message> list = new ArrayList<>();

        Message m1 = createMsg(id, sarah, "Hey Alex! Have you had a chance to test the dark glassmorphic layout for the right info side panel?", "2026-09-19T09:15:00Z");
        Message m2 = createMsg(id, currentProfile, "Yes! The contrast and spacing look super clean. Can you send over the updated mockup image?", "2026-09-19T09:18:00Z");
        m2.setReplyMessage(m1);

        Message mImage = new Message();
        mImage.setId(UUID.randomUUID().toString());
        mImage.setConversationId(id);
        mImage.setSenderId(sarah.getId());
        mImage.setSenderProfile(sarah);
        mImage.setMessageType(MessageType.IMAGE);
        mImage.setContent("RightInfoPanel_Mockup_v2.png");
        mImage.setCreatedAt("2026-09-19T09:22:00Z");

        Attachment attImg = new Attachment();
        attImg.setOriginalFilename("RightInfoPanel_Mockup_v2.png");
        attImg.setMimeType("image/png");
        attImg.setFileSize(1450000);
        mImage.setAttachment(attImg);

        addReaction(mImage, "❤️", 3, true);
        addReaction(mImage, "👍", 2, false);

        list.add(mImage);
        list.add(m2);
        list.add(m1);

        messagesByConversation.put(id, list);
        c.setLatestMessage(mImage);
    }

    private void initDavidDirectChat() {
        String id = "conv-david";
        Profile david = profiles.get("user-david-789");

        Conversation c = new Conversation();
        c.setId(id);
        c.setType(ConversationType.DIRECT);
        c.setDirectPartnerProfile(david);
        c.setCreatedAt(Instant.now().toString());
        conversations.put(id, c);

        List<Message> list = new ArrayList<>();

        Message m1 = createMsg(id, david, "Hey Alex, all Supabase database tables and RLS security policies have been deployed to staging.", "2026-09-19T08:30:00Z");
        Message m2 = createMsg(id, david, "Recorded a short 8-second update on the PostgREST query indexing strategy.", "2026-09-19T08:32:00Z");
        m2.setMessageType(MessageType.AUDIO);

        Attachment att = new Attachment();
        att.setOriginalFilename("db_indexes_summary.wav");
        att.setMimeType("audio/wav");
        att.setDurationSeconds(8);
        att.setFileSize(128000);
        m2.setAttachment(att);

        Message m3 = createMsg(id, currentProfile, "Awesome work David! Realtime sync is working flawlessly.", "2026-09-19T08:40:00Z");

        list.add(m3);
        list.add(m2);
        list.add(m1);

        messagesByConversation.put(id, list);
        c.setLatestMessage(m3);
    }

    private void initDesignGuildGroupChat() {
        String id = "conv-design-guild";

        Conversation c = new Conversation();
        c.setId(id);
        c.setName("Design System Guild 🎨");
        c.setDescription("Swing UI, FlatLaf custom tokens, glassmorphism & desktop UX guidelines");
        c.setType(ConversationType.GROUP);
        c.setPrivate(false);
        c.setInviteCode("DESIGN2026");
        c.setCreatedAt(Instant.now().toString());

        List<ConversationMember> members = new ArrayList<>();
        members.add(new ConversationMember(id, currentUser.getId(), Role.OWNER, currentProfile));
        members.add(new ConversationMember(id, profiles.get("user-sarah-456").getId(), Role.ADMIN, profiles.get("user-sarah-456")));
        c.setMembers(members);
        conversations.put(id, c);

        List<Message> list = new ArrayList<>();
        Profile sarah = profiles.get("user-sarah-456");

        Message m1 = createMsg(id, sarah, "Welcome to the Design System Guild! We use custom HSL palette values for subtle borders & glass overlays.", "2026-09-18T16:00:00Z");
        Message m2 = createMsg(id, currentProfile, "FlatLaf integrated smoothly with our Swing component hierarchy.", "2026-09-18T16:10:00Z");

        list.add(m2);
        list.add(m1);

        messagesByConversation.put(id, list);
        c.setLatestMessage(m2);
    }

    private Message createMsg(String convId, Profile sender, String content, String timeIso) {
        Message m = new Message();
        m.setId(UUID.randomUUID().toString());
        m.setConversationId(convId);
        m.setSenderId(sender.getId());
        m.setSenderProfile(sender);
        m.setMessageType(MessageType.TEXT);
        m.setContent(content);
        m.setStatus(MessageStatus.SENT);
        m.setCreatedAt(timeIso);
        return m;
    }

    private void addReaction(Message msg, String emoji, int count, boolean userReacted) {
        MessageReaction rx = new MessageReaction();
        rx.setId(UUID.randomUUID().toString());
        rx.setMessageId(msg.getId());
        rx.setUserId(currentUser.getId());
        rx.setEmoji(emoji);

        if (msg.getReactions() == null) msg.setReactions(new ArrayList<>());
        msg.getReactions().add(rx);
    }
}
