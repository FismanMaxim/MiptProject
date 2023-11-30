package EntitiesControllers;

import DTOs.UserDTO;
import EntitiesServices.CompanyService;
import EntitiesServices.UserService;
import InMemoryRepos.InMemoryCompanyRepository;
import InMemoryRepos.InMemoryUserRepository;
import Responses.FindUserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {
    private UserService userService;
    private Service service;
    private static ObjectMapper mapper;

    @BeforeAll
    static void initMapper() {
        mapper = new ObjectMapper();
    }

    @BeforeEach
    void initService() {
        service = Service.ignite();
        userService = new UserService(new InMemoryUserRepository());
    }

    @AfterEach
    void stopService() {
        service.stop();
        service.awaitStop();
    }

     @Test
     void getUserByIdTest() throws IOException, InterruptedException {
         CompanyService companyService = new CompanyService(new InMemoryCompanyRepository());
         ControllersManager manager = new ControllersManager(List.of(
                 new UserController(service, userService, companyService, mapper)
         ));

         manager.start();
         service.awaitInitialization();

         // Create user
         HttpClient.newHttpClient()
                 .send(
                         HttpRequest.newBuilder()
                                 .POST(
                                         HttpRequest.BodyPublishers.ofString(
                                                 """
                                                         {"name": "testUsername", "money": 5000 }"""
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
         assertEquals(5000, receivedUser.money());
     }

    @Test
    void errorOnCreateUser() {
        CompanyService companyService = new CompanyService(new InMemoryCompanyRepository());
        ControllersManager manager = new ControllersManager(List.of(
                new UserController(service, userService, companyService, mapper)
        ));

        manager.start();
        service.awaitInitialization();

        Supplier<HttpResponse<String>> createUser = () -> {
            try {
                HttpResponse<String> response = HttpClient.newHttpClient()
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

                return response;
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        var response = createUser.get();
        assertEquals(response.statusCode(), 400);
    }

    @Test
    void ErrorOnUpdateUser() throws IOException, InterruptedException {
        CompanyService companyService = new CompanyService(new InMemoryCompanyRepository());
        ControllersManager manager = new ControllersManager(List.of(
                new UserController(service, userService, companyService, mapper)
        ));

        manager.start();
        service.awaitInitialization();

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
}