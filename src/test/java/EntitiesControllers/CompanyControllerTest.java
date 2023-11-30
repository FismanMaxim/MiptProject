package EntitiesControllers;

import DTOs.UserDTO;
import EntitiesServices.CompanyService;
import EntitiesServices.UserService;
import InMemoryRepos.InMemoryCompanyRepository;
import InMemoryRepos.InMemoryUserRepository;
import Responses.EntityIdResponse;
import Responses.FindCompanyResponse;
import Responses.FindUserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
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

import static org.junit.jupiter.api.Assertions.*;

class CompanyControllerTest {
    private CompanyService companyService;
    private Service service;
    private static ObjectMapper mapper;

    @BeforeAll
    static void initMapper() {
        mapper = new ObjectMapper();
    }

    @BeforeEach
    void initService() {
        service = Service.ignite();
        companyService = new CompanyService(new InMemoryCompanyRepository());
    }

    @AfterEach
    void stopService() {
        service.stop();
        service.awaitStop();
    }


    @Test
    void errorOnGetCompanyById() throws IOException, InterruptedException {
        UserService userService = new UserService(new InMemoryUserRepository());
        ControllersManager manager = new ControllersManager(List.of(
                new CompanyController(service, companyService, mapper),
                new UserController(service, userService, companyService, mapper)
        ));

        manager.start();
        service.awaitInitialization();

        // Read non-existing company
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create("http://localhost:%d/api/company/0".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        assertEquals(404, response.statusCode());
    }

    @Test
    void errorOnUpdateCompanyById() throws IOException, InterruptedException {
        UserService userService = new UserService(new InMemoryUserRepository());
        ControllersManager manager = new ControllersManager(List.of(
                new CompanyController(service, companyService, mapper),
                new UserController(service, userService, companyService, mapper)
        ));

        manager.start();
        service.awaitInitialization();

        // Update non-existing company
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .PUT(
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                        {"name": "newCompanyName", "money": 2000000}"""
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/company/0".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        assertEquals(404, response.statusCode());
    }

    @Test void errorOnCreateCompany() {
        UserService userService = new UserService(new InMemoryUserRepository());
        ControllersManager manager = new ControllersManager(List.of(
                new CompanyController(service, companyService, mapper),
                new UserController(service, userService, companyService, mapper)
        ));

        manager.start();
        service.awaitInitialization();

        Supplier<HttpResponse<String>> createCompany = () -> {
            try {
                HttpResponse<String> response = HttpClient.newHttpClient()
                        .send(
                                HttpRequest.newBuilder()
                                        .POST(
                                                HttpRequest.BodyPublishers.ofString(
                                                        "{\"wrongKey\": \"wrongValue\", \"wrongKey\": -1}"
                                                )
                                        )
                                        .uri(URI.create("http://localhost:%d/api/company".formatted(service.port())))
                                        .build(),
                                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                        );

                return response;
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        var response = createCompany.get();
        assertEquals(response.statusCode(), 400);
    }
}

