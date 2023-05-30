package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.service.LabelService;
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
@RequestMapping("${base-url}" + LabelController.LABEL_CONTROLLER_PATH)
public class LabelController {
    public static final String LABEL_CONTROLLER_PATH = "/labels";
    public static final String ID = "/{id}";

    private LabelService labelService;

    @Operation(summary = "Create new label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The label has been successfully created",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Label.class))})})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping()
    public Label createNewLabel(@RequestBody @Valid LabelDto labelDto) {
        return labelService.createLabel(labelDto);
    }

    @Operation(summary = "Get all labels")
    @ApiResponse(responseCode = "200", description = "All labels are found",
            content = @Content(schema = @Schema(implementation = Label.class)))
    @GetMapping()
    public List<Label> getAllLabel() {
        return labelService.getAllLabel().stream().toList();
    }

    @Operation(summary = "Get label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The label is found",
                    content = {@Content(mediaType = "application/jsom",
                            schema = @Schema(implementation = Label.class))}),
            @ApiResponse(responseCode = "404", description = "No such label found", content = @Content)})
    @GetMapping(path = ID)
    public Label getLabelById(@PathVariable (name = "id") Long id) {
        return labelService.getLabelById(id);
    }

    @Operation(summary = "Update the label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The label is updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Label.class))}),
            @ApiResponse(responseCode = "422", description = "Invalid request", content = @Content)})
    @PutMapping(path = ID)
    public Label updateLabelById(@PathVariable (name = "id") Long id, @RequestBody @Valid LabelDto labelDto) {
        return labelService.updateLabelById(id, labelDto);
    }

    @Operation(summary = "Delete the label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Label has been successfully deleted"),
            @ApiResponse(responseCode = "404", description = "No such label found")})
    @DeleteMapping(path = ID)
    public void deleteLabelById(@PathVariable (name = "id") Long id) {
        labelService.deleteLabelById(id);
    }
}
