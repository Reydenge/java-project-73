package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfig;
import hexlet.code.dto.LoginDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
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

import static hexlet.code.config.security.SecurityConfig.LOGIN;
import static hexlet.code.controller.UserController.ID;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;

import static hexlet.code.utils.TestUtils.FIRST_TEST_USERNAME;
import static hexlet.code.utils.TestUtils.SECOND_TEST_USERNAME;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@ActiveProfiles(SpringConfig.TEST_PROFILE)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT, classes = SpringConfig.class)
public class UserControllerTest {

    public static final String BASE_URL = "/api";
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestUtils utils;

    @AfterEach
    public void clear() {
        utils.tearDown();
    }


    @Test
    public void testCreateNewUser() throws Exception {
        assertEquals(0, userRepository.count());
        utils.regDefaultUser().andExpect(status().isCreated());
        assertEquals(1, userRepository.count());
    }


    @Test
    public void testCreateUserWithInvalidFirstName() throws Exception {

        UserDto userDto = new UserDto(FIRST_TEST_USERNAME, "", "last name", "pwd");

        var postRequest = MockMvcRequestBuilders.post(BASE_URL + USER_CONTROLLER_PATH)
                .content(asJson(userDto))
                .contentType(APPLICATION_JSON);

        utils.perform(postRequest, FIRST_TEST_USERNAME).andExpect(status().is(400));
    }


    @Test
    public void testCreateUserWithInvalidLastName() throws Exception {

        UserDto userDto = new UserDto(FIRST_TEST_USERNAME, "first name", "", "pwd");

        final var postRequest = MockMvcRequestBuilders.post(BASE_URL + USER_CONTROLLER_PATH)
                .content(asJson(userDto))
                .contentType(APPLICATION_JSON);

        utils.perform(postRequest, FIRST_TEST_USERNAME).andExpect(status().is(400));
    }


    @Test
    public void testCreateUserWithInvalidPassword() throws Exception {

        UserDto userDto = new UserDto(FIRST_TEST_USERNAME, "first name", "last name", "p");

        final var postRequest = MockMvcRequestBuilders.post(BASE_URL + USER_CONTROLLER_PATH)
                .content(asJson(userDto))
                .contentType(APPLICATION_JSON);

        utils.perform(postRequest, FIRST_TEST_USERNAME).andExpect(status().is(400));
    }


    @Test
    public void testGetUserById() throws Exception {
        utils.regDefaultUser();
        User expectedUser = userRepository.findAll().get(0);
        final var response = utils.perform(
                        get(BASE_URL + USER_CONTROLLER_PATH + ID, expectedUser.getId()),
                        expectedUser.getEmail()
                ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final User user = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedUser.getId(), user.getId());
        assertEquals(expectedUser.getEmail(), user.getEmail());
        assertEquals(expectedUser.getFirstName(), user.getFirstName());
        assertEquals(expectedUser.getLastName(), user.getLastName());
    }


    @Test
    public void testGetAllUsers() throws Exception {
        utils.regDefaultUser();
        var response = utils.perform(get(BASE_URL + USER_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        List<User> users = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertThat(users.size()).isEqualTo(1);
    }

    @Test
    public void testLogin() throws Exception {
        utils.regDefaultUser();
        LoginDto loginDto = new LoginDto(
                utils.getTestRegistrationDto().getEmail(),
                utils.getTestRegistrationDto().getPassword()
        );

        var loginRequest = MockMvcRequestBuilders
                .post(BASE_URL + LOGIN)
                .content(asJson(loginDto))
                .contentType(APPLICATION_JSON);

        utils.perform(loginRequest).andExpect(status().isOk());
    }

    @Test
    public void testLoginFail() throws Exception {
        LoginDto loginDto = new LoginDto(
                utils.getTestRegistrationDto().getEmail(),
                utils.getTestRegistrationDto().getPassword()
        );

        final var loginRequest = MockMvcRequestBuilders.post(BASE_URL + LOGIN)
                .content(asJson(loginDto)).contentType(APPLICATION_JSON);

        utils.perform(loginRequest).andExpect(status().isUnauthorized());
    }


    @Test
    public void testUpdateUser() throws Exception {
        utils.regDefaultUser();

        Long userId = userRepository.findByEmail(FIRST_TEST_USERNAME).get().getId();

        UserDto userDto = new UserDto(SECOND_TEST_USERNAME,
                "Some first name",
                "Some last name",
                "Some password");

        var updateRequest = put(BASE_URL + USER_CONTROLLER_PATH + ID, userId)
                .content(asJson(userDto))
                .contentType(APPLICATION_JSON);

        utils.perform(updateRequest, FIRST_TEST_USERNAME).andExpect(status().isOk());

        assertTrue(userRepository.existsById(userId));
        assertNull(userRepository.findByEmail(FIRST_TEST_USERNAME).orElse(null));
        assertNotNull(userRepository.findByEmail(SECOND_TEST_USERNAME).orElse(null));
    }

    @Test
    public void testDeleteUser() throws Exception {
        utils.regDefaultUser();

        Long userId = userRepository.findByEmail(FIRST_TEST_USERNAME).get().getId();

        utils.perform(delete(BASE_URL + USER_CONTROLLER_PATH + ID, userId), FIRST_TEST_USERNAME)
                .andExpect(status().isOk());

        assertEquals(0, userRepository.count());
    }


    @Test
    public void testDeleteUserFail() throws Exception {
        utils.regDefaultUser();
        utils.regUser(new UserDto(
                SECOND_TEST_USERNAME,
                "First name 2",
                "Last name 2",
                "Password 2"
        ));

        final Long userId = userRepository.findByEmail(FIRST_TEST_USERNAME).get().getId();

        utils.perform(delete(BASE_URL + USER_CONTROLLER_PATH + ID, userId), SECOND_TEST_USERNAME)
                .andExpect(status().isForbidden());

        assertEquals(2, userRepository.count());
    }
}
