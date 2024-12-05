package com.camunda.training;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.extension.process_test_coverage.junit5.ProcessEngineCoverageExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.assertThat;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.complete;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.findId;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.runtimeService;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.task;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.taskService;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@ExtendWith(ProcessEngineCoverageExtension.class)
public class ProcessJUnitTest {

    @AfterEach
    public void resetMocks() {
        Mocks.reset(); // Очистка моков после каждого теста
    }

    @Test
    @Deployment(resources = "Twitter_QA.bpmn")
    public void testHappyPath() throws Exception {
        // Создаем mock для CreateTweetDelegate
        CreateTweetDelegate createTweetDelegateMock = mock(CreateTweetDelegate.class);
        Mocks.register("createTweetDelegate", createTweetDelegateMock);

        // Создаем переменные процесса
        Map<String, Object> variables = new HashMap<>();
        variables.put("content", "Exercise 4 test - Maks");

        // Запускаем процесс
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("TwitterQAProcess", variables);

        // Проверяем, что задача назначена группе "management"
        List<Task> taskList = taskService()
                .createTaskQuery()
                .taskCandidateGroup("management")
                .processInstanceId(processInstance.getId())
                .list();
        assertThat(taskList).asList()
                .isNotNull()
                .hasSize(1);

        // Проверяем, что задача называется "Review tweet"
        Task reviewTask = taskList.get(0);
        assertThat(reviewTask.getName()).isEqualTo("Review tweet");

        // Завершаем задачу
        complete(reviewTask, Map.of("approved", true));

        // Проверяем, что делегат был вызван
        verify(createTweetDelegateMock).execute(any());

        // Проверяем, что процесс завершился
        assertThat(processInstance).isEnded();
    }

    private static void assertThatTaskIsWaitingThenCompleteIt(
            ProcessInstance processInstance,
            String taskName,
            Map<String, Object> variables
    ) {
        String reviewTweetTaskId = findId(taskName);
        assertThat(processInstance).isWaitingAt(reviewTweetTaskId);
        Task task = task(reviewTweetTaskId);

        // Добавляем явную проверку названия задачи
        assertThat(task.getName()).isEqualTo(taskName);

        complete(task, variables);
    }
}
