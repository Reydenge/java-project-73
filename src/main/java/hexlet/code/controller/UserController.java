package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@RestController
@RequestMapping("${base-url}" + UserController.USER_CONTROLLER_PATH)
public class UserController {
    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID = "/{id}";
    private final UserRepository userRepository;
    private final UserService userService;

    private static final String OWNER = "@userRepository.findById(#id).get().getEmail() == authentication.getName()";

    public UserController(UserRepository userRepository, UserService userService) {
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @Operation(summary = "Create new user")
    @ApiResponse(responseCode = "201", description = "User successfully created",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = User.class))})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public User createNewUser(@RequestBody @Valid UserDto userDto) {
        return userService.createNewUser(userDto);
    }

    @Operation(summary = "Get user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User is found",
                    content = @Content(schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "404", description = "No such user found", content = @Content)})
    @GetMapping(path = ID)
    public User getUserById(@PathVariable(name = "id") long id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200", description = "All users are found",
            content = @Content(schema = @Schema(implementation = User.class)))
    @GetMapping()
    public List<User> getAllUsers() {
        return userService.getAll();
    }

    @Operation(summary = "Update the user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The user is updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))}),
            @ApiResponse(responseCode = "404", description = "The user is not found",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden to update",
                    content = @Content),
            @ApiResponse(responseCode = "422", description = "Invalid request",
                    content = @Content)})
    @PreAuthorize(OWNER)
    @PutMapping(path = ID)
    public User updateUser(@PathVariable(name = "id") long id, @RequestBody @Valid UserDto userDto) {
        return userService.updateUser(id, userDto);
    }

    @Operation(summary = "Delete the user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The user has been successfully deleted"),
            @ApiResponse(responseCode = "404", description = "The user is not found", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden to delete", content = @Content),
            @ApiResponse(responseCode = "422", description = "Data integrity violation", content = @Content)})
    @PreAuthorize((OWNER))
    @DeleteMapping(path = ID)
    public void deleteUser(@PathVariable(name = "id") long id) {
        userRepository.deleteById(id);
    }
}
