package com.camunda.training;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwitterService {
    private final Logger LOGGER = LoggerFactory.getLogger(CreateTweetDelegate.class.getName());

    public void updateStatus(String content) {
        LOGGER.info("Tweet: " + content);
    }
}
