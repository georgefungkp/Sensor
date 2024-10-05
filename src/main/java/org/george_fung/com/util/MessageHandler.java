package org.george_fung.com.util;

@FunctionalInterface
public interface MessageHandler {
    void processMessage(String message);
}
