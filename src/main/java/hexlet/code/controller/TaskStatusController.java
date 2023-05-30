package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;


@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + TaskStatusController.TASK_STATUS_CONTROLLER_PATH)
public class TaskStatusController {
    public static final String TASK_STATUS_CONTROLLER_PATH = "/statuses";
    public static final String ID = "/{id}";
    private final TaskStatusService taskStatusService;
    private final TaskStatusRepository taskStatusRepository;

    @Operation(summary = "Create new task status")
    @ApiResponse(responseCode = "201", description = "New task status successfully created",
    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = TaskStatus.class))})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public TaskStatus createNewTaskStatus(@RequestBody @Valid TaskStatusDto taskStatusDto) {
        return taskStatusService.createNewTaskStatus(taskStatusDto);
    }

    @Operation(summary = "Get all task statuses")
    @ApiResponse(responseCode = "200", description = "All task statuses are found",
            content = @Content(schema = @Schema(implementation = TaskStatus.class)))
    @GetMapping()
    public List<TaskStatus> getAllTaskStatus() {
        return taskStatusRepository.findAll().stream().toList();
    }

    @Operation(summary = "Get task status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The task status is found",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskStatus.class))}),
            @ApiResponse(responseCode = "404", description = "No such task status found", content = @Content)})
    @GetMapping(path = ID)
    public TaskStatus getTaskStatusById(@PathVariable(name = "id") Long id) {
        return taskStatusService.getTaskStatusById(id);
    }

    @Operation(summary = "Update the task status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The task status has been successfully updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskStatus.class))}),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content)})
    @PutMapping(path = ID)
    public TaskStatus updateTaskStatus(@PathVariable(name = "id") Long id,
                                       @RequestBody @Valid TaskStatusDto taskStatusDto) {
        return taskStatusService.updateTaskStatus(id, taskStatusDto);
    }

    @Operation(summary = "Delete the task status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task status has been deleted"),
            @ApiResponse(responseCode = "404", description = "No such task status found")})
    @DeleteMapping(path = ID)
    public void deleteTaskStatus(@PathVariable (name = "id") Long id) {
        taskStatusRepository.deleteById(id);
    }
}
