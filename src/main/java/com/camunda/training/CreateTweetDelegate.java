package com.camunda.training;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateTweetDelegate implements JavaDelegate {
    private final Logger LOGGER = LoggerFactory.getLogger(CreateTweetDelegate.class.getName());
    TwitterService twitter = new TwitterService();

    public void execute(DelegateExecution execution) throws Exception {
        String content = "I did it! Cheers Maks";
        twitter.updateStatus(content);
    }
}
