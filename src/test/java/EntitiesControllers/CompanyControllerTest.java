package EntitiesControllers;

import EntitiesServices.CompanyService;
import EntitiesServices.UserService;
import InMemoryRepos.InMemoryCompanyRepository;
import InMemoryRepos.InMemoryUserRepository;
import Responses.EntityIdResponse;
import Responses.FindCompanyResponse;
import Responses.GetAllCompaniesResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import spark.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

class CompanyControllerTest {
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
    void errorOnGetCompanyById() throws IOException, InterruptedException {
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

    @Test
    void testGetAllCompanies() throws IOException, InterruptedException {
        // Create company
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .POST(
                                        HttpRequest.BodyPublishers.ofString(
                                                "{\"name\": \"company1\", \"keySharesThreshold\": 10, \"totalShares\": 100, " +
                                                        "\"money\": 100, \"sharePrice\": 100}"
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/company".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        EntityIdResponse createResponse = mapper.readValue(response.body(), EntityIdResponse.class);

        assertEquals(201, response.statusCode());
        assertEquals(0, createResponse.id());

        // Create another company
        response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .POST(
                                        HttpRequest.BodyPublishers.ofString(
                                                "{\"name\": \"company2\", \"keySharesThreshold\": 10, \"totalShares\": 100, " +
                                                        "\"money\": 100, \"sharePrice\": 100}"
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/company".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        createResponse = mapper.readValue(response.body(), EntityIdResponse.class);

        assertEquals(201, response.statusCode());
        assertEquals(1, createResponse.id());

        // Get all companies
        response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create("http://localhost:%d/api/company".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        GetAllCompaniesResponse getResponse = mapper.readValue(response.body(), GetAllCompaniesResponse.class);

        List<FindCompanyResponse> companiesResponses = getResponse.companiesResponses();
        assertEquals(companiesResponses.size(), 2);
        assertEquals(companiesResponses.get(0).companyDTO().name() , "company1");
        assertEquals(companiesResponses.get(1).companyDTO().name(), "company2");
    }

    @Test void errorOnCreateCompany() {
        Supplier<HttpResponse<String>> createCompany = () -> {
            try {

                return HttpClient.newHttpClient()
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
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        };

        var response = createCompany.get();
        assertEquals(response.statusCode(), 400);
    }

    @Test
    void getCompanyByNamePassword() throws IOException, InterruptedException {
        // Create company
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .POST(
                                        HttpRequest.BodyPublishers.ofString(
                                                "{\"name\": \"testName\", \"keySharesThreshold\": 25, \"totalShares\": 100, " +
                                                        "\"money\": 1000000, \"sharePrice\": 100, \"password\": \"pass\"}"
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/company".formatted(service.port())))
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
                                                "{\"name\": \"testName\", \"password\": \"pass\"}"
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/company/auth".formatted(service.port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        FindCompanyResponse findCompanyResponse = mapper.readValue(response.body(), FindCompanyResponse.class);

        assertEquals(200, response.statusCode());
        assertEquals(findCompanyResponse.companyDTO().name(), "testName");
    }
}

