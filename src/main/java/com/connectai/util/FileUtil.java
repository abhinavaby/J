package com.connectai.util;

import java.io.File;

public class FileUtil {
    public static String getMimeType(File file) {
        if (file == null) return "application/octet-stream";
        String name = file.getName().toLowerCase();
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "image/jpeg";
        if (name.endsWith(".png")) return "image/png";
        if (name.endsWith(".gif")) return "image/gif";
        if (name.endsWith(".webp")) return "image/webp";
        if (name.endsWith(".pdf")) return "application/pdf";
        if (name.endsWith(".docx") || name.endsWith(".doc")) return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        if (name.endsWith(".pptx") || name.endsWith(".ppt")) return "application/vnd.openxmlformats-officedocument.presentationml.presentation";
        if (name.endsWith(".xlsx") || name.endsWith(".xls")) return "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        if (name.endsWith(".txt")) return "text/plain";
        if (name.endsWith(".zip")) return "application/zip";
        if (name.endsWith(".wav")) return "audio/wav";
        if (name.endsWith(".mp3")) return "audio/mpeg";
        if (name.endsWith(".mp4")) return "video/mp4";
        return "application/octet-stream";
    }

    public static boolean isImage(String mimeType) {
        return mimeType != null && mimeType.startsWith("image/");
    }

    public static boolean isAudio(String mimeType) {
        return mimeType != null && mimeType.startsWith("audio/");
    }

    public static boolean isVideo(String mimeType) {
        return mimeType != null && mimeType.startsWith("video/");
    }
}
