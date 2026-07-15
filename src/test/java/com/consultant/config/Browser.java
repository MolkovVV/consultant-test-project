package com.consultant.config;

public enum Browser {
    CHROME,
    EDGE,
    FIREFOX,
    REMOTE_CHROME,
    REMOTE_EDGE,
    REMOTE_FIREFOX;

    public boolean isRemote() {
        return name().startsWith("REMOTE_");
    }

    public Browser toLocal() {
        return switch (this) {
            case REMOTE_CHROME -> CHROME;
            case REMOTE_EDGE -> EDGE;
            case REMOTE_FIREFOX -> FIREFOX;
            default -> this;
        };
    }
}