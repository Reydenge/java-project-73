package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;

import java.util.List;
import com.querydsl.core.types.Predicate;

public interface TaskService {
    Task createNewTask(TaskDto taskDto);
    Task updateTask(Long id, TaskDto taskDto);

    void deleteTask(Long id);
    List<Task> getAllTasks(Predicate predicate);
    List<Task> getAllTasks();

    Task getTaskById(Long id);
}
