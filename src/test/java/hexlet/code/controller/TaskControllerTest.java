package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfig;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.TaskController.ID;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.utils.TestUtils.FIRST_TEST_USERNAME;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(SpringConfig.TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfig.class)
public class TaskControllerTest {

    public static final String BASE_URL = "/api";
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TaskStatusRepository statusRepository;

    @Autowired
    private TestUtils utils;

    @AfterEach
    public void clear() {
        utils.tearDown();
    }


    @Test
    public void testCreateTask() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);

        final var statusDto = new TaskStatusDto("new");

        final var statusPostRequest = post(BASE_URL + TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        final TaskStatus status = fromJson((utils.perform(statusPostRequest, FIRST_TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>() {
        });


        final var labelDto = new LabelDto("bug");

        final var labelPostRequest = post(BASE_URL + LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        final Label label = fromJson((utils.perform(labelPostRequest, FIRST_TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>() {
        });
        List<Long> labels = new ArrayList<>();
        labels.add(label.getId());


        final var taskDto = new TaskDto("task", "description",
                status.getId(), expectedUser.getId(), labels);

        final var taskPostRequest = post(BASE_URL + TASK_CONTROLLER_PATH)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        final var response = utils.perform(taskPostRequest, FIRST_TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Task task = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(task.getName(), taskDto.getName());
        assertEquals(task.getDescription(), taskDto.getDescription());
        assertEquals(task.getExecutor().getId(), taskDto.getExecutorId());
        assertEquals(task.getTaskStatus().getId(), taskDto.getTaskStatusId());
        assertEquals(task.getLabelIds().stream().map(Label::getId).collect(Collectors.toList()), taskDto.getLabelIds());
    }


    @Test
    public void testGetTaskById() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);

        final var statusDto = new TaskStatusDto("new");

        final var statusPostRequest = post(BASE_URL + TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        final TaskStatus status = fromJson((utils.perform(statusPostRequest, FIRST_TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>() {
        });


        final var labelDto = new LabelDto("bug");

        final var labelPostRequest = post(BASE_URL + LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        final Label label = fromJson((utils.perform(labelPostRequest, FIRST_TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>() {
        });
        List<Long> labels = new ArrayList<>();
        labels.add(label.getId());


        final var taskDto = new TaskDto("task", "description",
                status.getId(), expectedUser.getId(), labels);

        final var taskPostRequest = post(BASE_URL + TASK_CONTROLLER_PATH)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        utils.perform(taskPostRequest, FIRST_TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();


        final var response = utils.perform(get(BASE_URL + TASK_CONTROLLER_PATH + ID,
                                taskRepository.findAll().get(0).getId()),
                        FIRST_TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Task task = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(task.getName(), taskDto.getName());
        assertEquals(task.getDescription(), taskDto.getDescription());
        assertEquals(task.getExecutor().getId(), taskDto.getExecutorId());
        assertEquals(task.getTaskStatus().getId(), taskDto.getTaskStatusId());
        assertEquals(task.getLabelIds().stream().map(Label::getId).collect(Collectors.toList()), taskDto.getLabelIds());
    }


    @Test
    public void testUpdateTask() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);

        final var statusDto = new TaskStatusDto("new");

        final var statusPostRequest = post(BASE_URL + TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        final TaskStatus status = fromJson((utils.perform(statusPostRequest, FIRST_TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>() {
        });


        final var labelDto = new LabelDto("bug");

        final var labelPostRequest = post(BASE_URL + LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        final Label label = fromJson((utils.perform(labelPostRequest, FIRST_TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>() {
        });
        List<Long> labels = new ArrayList<>();
        labels.add(label.getId());


        final var taskDto = new TaskDto("task", "description",
                status.getId(), expectedUser.getId(), labels);

        final var taskPostRequest = post(BASE_URL + TASK_CONTROLLER_PATH)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        final var postTaskResponse = utils.perform(taskPostRequest, FIRST_TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        final Task postedTask = fromJson(postTaskResponse.getContentAsString(), new TypeReference<>() {
        });


        final var newTaskDto = new TaskDto("task1", "description2",
                status.getId(), expectedUser.getId(), labels);

        final var putRequest = put(BASE_URL + TASK_CONTROLLER_PATH + ID, postedTask.getId())
                .content(asJson(newTaskDto))
                .contentType(APPLICATION_JSON);
        final var response = utils.perform(putRequest, FIRST_TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final Task task = fromJson(response.getContentAsString(), new TypeReference<>() {
        });


        assertEquals(task.getName(), newTaskDto.getName());
        assertEquals(task.getDescription(), newTaskDto.getDescription());
        assertNotEquals(task.getName(), taskDto.getName());
        assertNotEquals(task.getDescription(), taskDto.getDescription());
    }


    @Test
    public void testGetAllTasks() throws Exception {
        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);

        final var statusDto = new TaskStatusDto("new");

        final var statusPostRequest = post(BASE_URL + TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        final TaskStatus status = fromJson((utils.perform(statusPostRequest, FIRST_TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>() {
        });


        final var labelDto = new LabelDto("bug");

        final var labelPostRequest = post(BASE_URL + LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        final Label label = fromJson((utils.perform(labelPostRequest, FIRST_TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>() {
        });
        List<Long> labels = new ArrayList<>();
        labels.add(label.getId());


        final var taskDto = new TaskDto("task", "description",
                status.getId(), expectedUser.getId(), labels);

        final var taskPostRequest = post(BASE_URL + TASK_CONTROLLER_PATH)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        utils.perform(taskPostRequest, FIRST_TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();


        final var response = utils.perform(get(BASE_URL + TASK_CONTROLLER_PATH), FIRST_TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Task> tasks = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(tasks.size()).isEqualTo(1);
    }


    @Test
    public void testDeleteTask() throws Exception {

        utils.regDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);

        final var statusDto = new TaskStatusDto("new");

        final var statusPostRequest = post(BASE_URL + TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(statusDto))
                .contentType(APPLICATION_JSON);
        final TaskStatus status = fromJson((utils.perform(statusPostRequest, FIRST_TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>() {
        });


        final var labelDto = new LabelDto("bug");

        final var labelPostRequest = post(BASE_URL + LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        final Label label = fromJson((utils.perform(labelPostRequest, FIRST_TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse())
                .getContentAsString(), new TypeReference<>() {
        });
        List<Long> labels = new ArrayList<>();
        labels.add(label.getId());


        final var taskDto = new TaskDto("task", "description",
                status.getId(), expectedUser.getId(), labels);

        final var taskPostRequest = post(BASE_URL + TASK_CONTROLLER_PATH)
                .content(asJson(taskDto))
                .contentType(APPLICATION_JSON);
        final var postTaskResponse = utils.perform(taskPostRequest, FIRST_TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();
        final Task postedTask = fromJson(postTaskResponse.getContentAsString(), new TypeReference<>() {
        });


        final var deleteRequest = delete(BASE_URL + TASK_CONTROLLER_PATH + ID, postedTask.getId());
        utils.perform(deleteRequest, FIRST_TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();


        assertEquals(0, taskRepository.count());

    }
}
