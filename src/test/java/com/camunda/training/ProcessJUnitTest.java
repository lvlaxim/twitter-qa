package com.camunda.training;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@ExtendWith(ProcessEngineCoverageExtension.class)
public class ProcessJUnitTest {

    @Test
    @Deployment(resources = "Twitter_QA.bpmn")
    public void testHappyPath() {
        // Create a HashMap to put in variables for the process instance
        Map<String, Object> variables = new HashMap<>();
        variables.put("content", "Exercise 4 test - Maks");
        // Start process with Java API and variables
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("TwitterQAProcess", variables);
        // Make assertions on the process instance
        assertThatTaskIsWaitingThenCompleteIt(processInstance, "Review tweet", Map.of("approved", true));
        assertThat(processInstance).isEnded();
    }

    private static void assertThatTaskIsWaitingThenCompleteIt(
            ProcessInstance processInstance,
            String taskName,
            Map<String, Object> variables
    ) {
        String reviewTweetTaskId = findId(taskName);
        assertThat(processInstance).isWaitingAt(reviewTweetTaskId);
        complete(task(reviewTweetTaskId), variables);
    }

}
