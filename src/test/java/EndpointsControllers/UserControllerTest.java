package EndpointsControllers;

import DTOs.UserDTO;
import EndpointsControllers.EntitiesControllers.UserController;
import EntitiesServices.CompanyService;
import EntitiesServices.UserService;
import InMemoryRepos.InMemoryCompanyRepository;
import InMemoryRepos.InMemoryUserRepository;
import Responses.EntityIdResponse;
import Responses.FindUserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {
    private Service service;
    private static ObjectMapper mapper;

    @BeforeAll
    static void initMapper() {
        mapper = new ObjectMapper();
    }

    @BeforeEach
    void initService() {
        service = Service.ignite();
        UserService userService = new UserService(new InMemoryUserRepository());

        CompanyService companyService = new CompanyService(new InMemoryCompanyRepository());
        ControllersManager manager = new ControllersManager(List.of(
                new UserController(service, userService, companyService, mapper)
        ));

        manager.start();
        service.awaitInitialization();
    }

    @AfterEach
    void stopService() {
        service.stop();
        service.awaitStop();
    }

    @Test
    void getUserByIdTest() throws IOException, InterruptedException {
        // Create user
        HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .POST(
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                        {"name": "testUsername", "password": "pass" }"""
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/usr".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        // Get user
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create("http://localhost:%d/api/usr/0".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        FindUserResponse findUserResponse = mapper.readValue(response.body(), FindUserResponse.class);
        UserDTO receivedUser = findUserResponse.user();

        assertEquals("testUsername", receivedUser.name());
        assertEquals("pass", receivedUser.password());
    }

    @Test
    void errorOnCreateUser() {
        Supplier<HttpResponse<String>> createUser = () -> {
            try {

                return HttpClient.newHttpClient()
                        .send(
                                HttpRequest.newBuilder()
                                        .POST(
                                                HttpRequest.BodyPublishers.ofString(
                                                        "{\"wrongKey\": \"wrongValue\", \"wrongKey\": -1}"
                                                )
                                        )
                                        .uri(URI.create("http://localhost:%d/api/usr".formatted(service.port())))
                                        .build(),
                                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                        );
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        var response = createUser.get();
        assertEquals(response.statusCode(), 400);
    }

    @Test
    void errorOnUpdateUser() throws IOException, InterruptedException {
        // Update non-existing user
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .PUT(
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                        {"name": "newUserName"}"""
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/usr/0".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        assertEquals(404, response.statusCode());
    }

    @Test
    void getUserByNamePassword() throws IOException, InterruptedException {
        // Create user
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .POST(
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                        {"name": "testUsername", "password": "pass" }"""
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/usr".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        EntityIdResponse createResponse = mapper.readValue(response.body(), EntityIdResponse.class);

        assertEquals(201, response.statusCode());
        assertEquals(0, createResponse.id());

        // Get by name and password
        response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .POST(
                                        HttpRequest.BodyPublishers.ofString(
                                                "{\"name\": \"testUsername\", \"password\": \"pass\"}"
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/usr/auth".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );


        FindUserResponse userResponse = mapper.readValue(response.body(), FindUserResponse.class);

        assertEquals(200, response.statusCode());
        assertEquals(userResponse.user().name(), "testUsername");
    }
}