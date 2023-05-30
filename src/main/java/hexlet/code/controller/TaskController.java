package hexlet.code.controller;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + TaskController.TASK_CONTROLLER_PATH)
public class TaskController {
    public static final String TASK_CONTROLLER_PATH = "/tasks";
    public static final String ID = "/{id}";

    private static final String OWNER =
            "@taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()";

    private final TaskService taskService;
    private final TaskRepository taskRepository;

    @Operation(summary = "Create new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task has been created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Task.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public Task createNewTask(@RequestBody TaskDto taskDto) {
        return taskService.createNewTask(taskDto);
    }

    @Operation(summary = "Get all tasks")
    @ApiResponse(responseCode = "200", description = "All tasks are found",
            content = @Content(schema = @Schema(implementation = Task.class)))
    @GetMapping()
    public Iterable<Task> getAllTasks(@QuerydslPredicate Predicate predicate) {
        return predicate == null ? taskService.getAllTasks() : taskService.getAllTasks(predicate);
    }

    @Operation(summary = "Get task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The task is found",
            content = {@Content(mediaType = "application/jsom", schema = @Schema(implementation = Task.class))}),
            @ApiResponse(responseCode = "404", description = "No such task found", content = @Content)})
    @GetMapping(path = ID)
    public Task getTaskById(@PathVariable(name = "id") Long id) {
        return taskService.getTaskById(id);
    }

    @Operation(summary = "Update the task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The task is successfully updated",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Task.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)})
    @PutMapping(path = ID)
    public Task updateTask(@PathVariable(name = "id") Long id, @RequestBody TaskDto taskDto) {
        return taskService.updateTask(id, taskDto);
    }

    @Operation(summary = "Delete the task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The task has been successfully deleted"),
            @ApiResponse(responseCode = "404", description = "The task is not found", content = @Content)})
    @PreAuthorize(OWNER)
    @DeleteMapping(path = ID)
    public void deleteTask(@PathVariable(name = "id") Long id) {
        taskRepository.deleteById(id);
    }
}
