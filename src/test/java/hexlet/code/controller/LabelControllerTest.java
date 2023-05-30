package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfig;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@ActiveProfiles(SpringConfig.TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfig.class)
public class LabelControllerTest {

    public static final String BASE_URL = "/api";
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Autowired
    private TestUtils utils;

    @AfterEach
    public void clear() {
        utils.tearDown();
    }


    @Test
    public void testCreateLabel() throws Exception {
        utils.regDefaultUser();

        final var labelDto = new LabelDto("New label");

        final var postRequest = MockMvcRequestBuilders.post(BASE_URL + LabelController.LABEL_CONTROLLER_PATH)
                .content(TestUtils.asJson(labelDto))
                .contentType(APPLICATION_JSON);

        final var response = utils.perform(postRequest, TestUtils.FIRST_TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final Label expectedLabel = labelRepository.findAll().get(0);

        final Label label = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertTrue(labelRepository.existsById(label.getId()));
        assertEquals(expectedLabel.getId(), label.getId());
        assertEquals(expectedLabel.getName(), label.getName());
    }


    @Test
    public void testGetLabelById() throws Exception {
        utils.regDefaultUser();

        final var labelDto = new LabelDto("New label");

        final var postRequest = MockMvcRequestBuilders.post(BASE_URL + LabelController.LABEL_CONTROLLER_PATH)
                .content(TestUtils.asJson(labelDto))
                .contentType(APPLICATION_JSON);

        utils.perform(postRequest, TestUtils.FIRST_TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        final var response = utils.perform(MockMvcRequestBuilders.get(BASE_URL + LabelController.LABEL_CONTROLLER_PATH
                                + LabelController.ID, labelRepository.findAll().get(0).getId()),
                        TestUtils.FIRST_TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        final Label label = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(labelDto.getName(), label.getName());
    }


    @Test
    public void testCreatedLabelFails() throws Exception {
        utils.regDefaultUser();

        final var labelDto = new LabelDto("");

        final var postRequest = MockMvcRequestBuilders.post(BASE_URL + LabelController.LABEL_CONTROLLER_PATH)
                .content(TestUtils.asJson(labelDto))
                .contentType(APPLICATION_JSON);


        assertEquals(0, labelRepository.count());
    }


    @Test
    public void testUpdateLabel() throws Exception {
        utils.regDefaultUser();

        final var postRequest = MockMvcRequestBuilders.post(BASE_URL + LabelController.LABEL_CONTROLLER_PATH)
                .content(TestUtils.asJson(new LabelDto("bug")))
                .contentType(APPLICATION_JSON);
        utils.perform(postRequest, TestUtils.FIRST_TEST_USERNAME);
        Label createdLabel = labelRepository.findAll().get(0);

        final var labelDto = new LabelDto("feature");

        final var putRequest = MockMvcRequestBuilders.put(BASE_URL + LabelController.LABEL_CONTROLLER_PATH
                        + LabelController.ID, createdLabel.getId())
                .content(TestUtils.asJson(labelDto))
                .contentType(APPLICATION_JSON);
        final var response = utils.perform(putRequest, TestUtils.FIRST_TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        createdLabel = labelRepository.findAll().get(0);

        final Label label = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });


        assertEquals(createdLabel.getId(), label.getId());
        assertEquals(createdLabel.getName(), labelDto.getName());
    }


    @Test
    public void getAllLabels() throws Exception {
        utils.regDefaultUser();

        final var labelDto = new LabelDto("bug");

        final var postRequest = MockMvcRequestBuilders.post(BASE_URL + LabelController.LABEL_CONTROLLER_PATH)
                .content(TestUtils.asJson(labelDto))
                .contentType(APPLICATION_JSON);
        utils.perform(postRequest, TestUtils.FIRST_TEST_USERNAME)
                .andExpect(status().isCreated());

        final var response = utils.perform(MockMvcRequestBuilders.get(BASE_URL
                        + LabelController.LABEL_CONTROLLER_PATH), TestUtils.FIRST_TEST_USERNAME)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final List<Label> labels = TestUtils.fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(labels.size()).isEqualTo(1);
    }


    @Test
    public void deleteLabel() throws Exception {
        utils.regDefaultUser();

        final var labelDto = new LabelDto("bug");

        final var postRequest = MockMvcRequestBuilders.post(BASE_URL + LabelController.LABEL_CONTROLLER_PATH)
                .content(TestUtils.asJson(labelDto))
                .contentType(APPLICATION_JSON);
        utils.perform(postRequest, TestUtils.FIRST_TEST_USERNAME)
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        utils.perform(MockMvcRequestBuilders.delete(BASE_URL + LabelController.LABEL_CONTROLLER_PATH
                        + LabelController.ID, labelRepository.findAll().get(0).getId()), TestUtils.FIRST_TEST_USERNAME)
                .andExpect(status().isOk());

        assertEquals(0, labelRepository.count());
    }
}
