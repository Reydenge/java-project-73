package hexlet.code.service.impl;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.exception.TaskNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.LabelService;
import hexlet.code.service.TaskService;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final TaskStatusService taskStatusService;
    private final LabelService labelService;

    @Override
    public Task createNewTask(TaskDto taskDto) {
        Task newTask = constructFromDto(taskDto);
        return taskRepository.save(newTask);
    }

    @Override
    public Task updateTask(Long id, TaskDto updatedTaskDto) {
        Task taskToBeUpdated = constructFromDto(updatedTaskDto);
        taskToBeUpdated.setId(id);
        return taskRepository.save(taskToBeUpdated);
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public List<Task> getAllTasks(Predicate predicate) {
        return StreamSupport.stream(taskRepository.findAll(predicate).spliterator(), false)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found"));
    }

    private Task constructFromDto(TaskDto taskDto) {
        User executor = Optional.ofNullable(taskDto.getExecutorId())
                .map(userService::getUserById)
                .orElse(null);

        TaskStatus taskStatus = Optional.of(taskDto.getTaskStatusId())
                .map(taskStatusService::getTaskStatusById)
                .orElse(null);

        List<Label> labels = Optional.ofNullable(taskDto.getLabelIds())
                .orElse(List.of())
                .stream()
                .filter(Objects::nonNull)
                .map(labelService::getLabelById)
                .collect(Collectors.toList());

        return Task.builder()
                .author(userService.getCurrentUser())
                .executor(executor)
                .taskStatus(taskStatus)
                .labels(labels)
                .name(taskDto.getName())
                .description(taskDto.getDescription())
                .createdAt(taskDto.getCreatedAt())
                .build();
    }
}
