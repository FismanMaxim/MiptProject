package Integration;

import DTOs.UserDTO;
import EndpointsControllers.EntitiesControllers.CompanyController;
import EndpointsControllers.ControllersManager;
import EndpointsControllers.EntitiesControllers.UserController;
import EntitiesServices.CompanyService;
import EntitiesServices.UserService;
import InMemoryRepos.InMemoryCompanyRepository;
import InMemoryRepos.InMemoryUserRepository;
import Responses.EntityIdResponse;
import Responses.FindCompanyResponse;
import Responses.FindUserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntitiesCRUDTest {
    private Service service;
    private static ObjectMapper mapper;

    @BeforeAll
    static void initMapper() {
        mapper = new ObjectMapper();
    }

    @BeforeEach
    void initService() {
        service = Service.ignite();
        CompanyService companyService = new CompanyService(new InMemoryCompanyRepository());

        UserService userService = new UserService(new InMemoryUserRepository());
        ControllersManager manager = new ControllersManager(List.of(
                new CompanyController(service, companyService, mapper),
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
    void companyControllerCRUD() throws Exception {
        // Create company
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .POST(
                                        HttpRequest.BodyPublishers.ofString(
                                                "{\"name\": \"testName\", \"password\": \"companyPass\"}"
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/company".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        EntityIdResponse createResponse = mapper.readValue(response.body(), EntityIdResponse.class);

        assertEquals(201, response.statusCode());
        assertEquals(0, createResponse.id());

        // Create user
        response = HttpClient.newHttpClient()
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

        assertEquals(201, response.statusCode());

        // Update user
        response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .PUT(
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                        {"name": "newUserName", "deltaMoney": 5000}"""
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/usr/0".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        assertEquals(200, response.statusCode());

        // Update company
        response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .PUT(
                                        HttpRequest.BodyPublishers.ofString(
                                                "{\"name\": \"newCompanyName\", \"deltaMoney\": 1000, " +
                                                        "\"deltaShares\": 100, \"sharePrice\": 100, \"threshold\": 25}"
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/company/0".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );


        assertEquals(200, response.statusCode());

        // User buys shares
        response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .PUT(
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                      {"sharesDelta":[{"companyId":0,"countDelta":50}]}"""
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/usr/0/shares".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );


        assertEquals(200, response.statusCode());


        // Read company
        response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create("http://localhost:%d/api/company/0".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        FindCompanyResponse findCompanyResponse = mapper.readValue(response.body(), FindCompanyResponse.class);

        assertEquals(200, response.statusCode());
        assertEquals("newCompanyName", findCompanyResponse.company().getCompanyName());
        assertEquals(100, findCompanyResponse.company().getTotalShares());
        assertEquals(50, findCompanyResponse.company().getVacantShares());
        assertEquals(25, findCompanyResponse.company().getKeyShareholderThreshold());
        assertEquals(1000, findCompanyResponse.company().getMoney());
        assertEquals(100, findCompanyResponse.company().getSharePrice());
        assertEquals(1, findCompanyResponse.company().getCopyOfUsers().size());

        // update user one more time (adding shares and checking that vacant shares changes correctly)

        // Read user
        response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create("http://localhost:%d/api/usr/0".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        FindUserResponse findUserResponse = mapper.readValue(response.body(), FindUserResponse.class);
        UserDTO receivedUser = findUserResponse.user();

        assertEquals("newUserName", receivedUser.name());
        assertEquals(0, receivedUser.money());
        assertEquals(Map.of(0L, 50), receivedUser.shares());

        // Delete user
        response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .DELETE()
                                .uri(URI.create("http://localhost:%d/api/usr/0".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        assertEquals(200, response.statusCode());

        // Delete company
        response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .DELETE()
                                .uri(URI.create("http://localhost:%d/api/company/0".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        assertEquals(200, response.statusCode());
    }
}
