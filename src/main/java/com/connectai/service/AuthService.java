package com.connectai.service;

import com.connectai.model.Profile;
import com.connectai.model.User;
import com.connectai.repository.AuthRepository;
import com.connectai.repository.ProfileRepository;
import com.connectai.util.JsonUtil;
import java.io.File;
import java.nio.file.Files;
import java.util.concurrent.CompletableFuture;

public class AuthService {
    private static final AuthService instance = new AuthService();
    private static final File SESSION_FILE = new File("local-session.json");

    private final AuthRepository authRepository;
    private final ProfileRepository profileRepository;

    private User currentUser;
    private Profile currentProfile;

    private AuthService() {
        this.authRepository = new AuthRepository();
        this.profileRepository = new ProfileRepository();
    }

    public static AuthService getInstance() {
        return instance;
    }

    public CompletableFuture<User> signUp(String email, String password, String username, String displayName, String avatarPath) {
        if (com.connectai.config.AppConfig.isMockMode()) {
            this.currentUser = com.connectai.mock.MockDataProvider.getInstance().getCurrentUser();
            this.currentProfile = com.connectai.mock.MockDataProvider.getInstance().getCurrentProfile();
            return CompletableFuture.completedFuture(this.currentUser);
        }
        return authRepository.signUp(email, password, username, displayName, avatarPath)
                .thenCompose(user -> {
                    this.currentUser = user;
                    saveSession(user);
                    return loadCurrentProfile().thenApply(p -> user);
                });
    }

    public CompletableFuture<User> signIn(String email, String password) {
        if (com.connectai.config.AppConfig.isMockMode()) {
            this.currentUser = com.connectai.mock.MockDataProvider.getInstance().getCurrentUser();
            this.currentProfile = com.connectai.mock.MockDataProvider.getInstance().getCurrentProfile();
            return CompletableFuture.completedFuture(this.currentUser);
        }
        return authRepository.signIn(email, password)
                .thenCompose(user -> {
                    this.currentUser = user;
                    saveSession(user);
                    return loadCurrentProfile().thenApply(p -> user);
                });
    }

    public CompletableFuture<User> restoreSession() {
        if (com.connectai.config.AppConfig.isMockMode()) {
            this.currentUser = com.connectai.mock.MockDataProvider.getInstance().getCurrentUser();
            this.currentProfile = com.connectai.mock.MockDataProvider.getInstance().getCurrentProfile();
            return CompletableFuture.completedFuture(this.currentUser);
        }

        if (!SESSION_FILE.exists()) {
            return CompletableFuture.failedFuture(new IllegalStateException("No saved session"));
        }

        try {
            String json = Files.readString(SESSION_FILE.toPath());
            User savedUser = JsonUtil.fromJson(json, User.class);
            if (savedUser == null || savedUser.getRefreshToken() == null) {
                return CompletableFuture.failedFuture(new IllegalStateException("Invalid session format"));
            }

            return authRepository.refreshSession(savedUser.getRefreshToken())
                    .thenCompose(newUser -> {
                        this.currentUser = newUser;
                        saveSession(newUser);
                        return loadCurrentProfile().thenApply(p -> newUser);
                    });
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<Profile> loadCurrentProfile() {
        if (com.connectai.config.AppConfig.isMockMode()) {
            this.currentProfile = com.connectai.mock.MockDataProvider.getInstance().getCurrentProfile();
            return CompletableFuture.completedFuture(this.currentProfile);
        }
        if (currentUser == null) return CompletableFuture.completedFuture(null);
        return profileRepository.getProfileById(currentUser.getId(), currentUser.getAccessToken())
                .thenApply(profile -> {
                    this.currentProfile = profile;
                    return profile;
                });
    }

    public void logout() {
        this.currentUser = null;
        this.currentProfile = null;
        if (SESSION_FILE.exists()) {
            SESSION_FILE.delete();
        }
    }

    public User getCurrentUser() {
        if (com.connectai.config.AppConfig.isMockMode() && currentUser == null) {
            this.currentUser = com.connectai.mock.MockDataProvider.getInstance().getCurrentUser();
        }
        return currentUser;
    }

    public Profile getCurrentProfile() {
        if (com.connectai.config.AppConfig.isMockMode() && currentProfile == null) {
            this.currentProfile = com.connectai.mock.MockDataProvider.getInstance().getCurrentProfile();
        }
        return currentProfile;
    }

    public String getAccessToken() {
        if (com.connectai.config.AppConfig.isMockMode()) {
            return "mock-access-token";
        }
        return currentUser != null ? currentUser.getAccessToken() : null;
    }

    private void saveSession(User user) {
        try {
            String json = JsonUtil.toJson(user);
            Files.writeString(SESSION_FILE.toPath(), json);
        } catch (Exception e) {
            System.err.println("Failed to save session locally: " + e.getMessage());
        }
    }
}
