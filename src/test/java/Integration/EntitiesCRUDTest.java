package Integration;

import DTOs.UserDTO;
import EndpointsControllers.ControllersManager;
import EndpointsControllers.EntitiesControllers.CompanyController;
import EndpointsControllers.EntitiesControllers.UserController;
import Entities.Company;
import Entities.User;
import EntitiesRepositories.EntityRepository;
import EntitiesServices.CompanyService;
import EntitiesServices.UserService;
import InMemoryRepos.InMemoryCompanyRepository;
import InMemoryRepos.InMemoryUserRepository;
import Responses.EntityIdResponse;
import Responses.FindCompanyResponse;
import Responses.FindUserResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.Database;
import org.junit.jupiter.api.*;
import spark.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EntitiesCRUDTest {
    private Service service;
    private static ObjectMapper mapper;

    private static Database database;

    @BeforeAll
    public static void setUpDb() throws SQLException {
        // Initialize the database connection before running the tests
        database = new Database(DriverManager.getConnection("jdbc:postgresql" +
                        "://cornelius.db.elephantsql.com:5432/hmtdjque",
                "hmtdjque",
                "mW9O7Imtz3eqjtvVolLGZ4gWlC9VuKMh"));
        try {
            database.connection.prepareStatement("DROP table users; DROP " +
                    "table companies;").execute();
        } catch (SQLException e) {
        }
        try {
            database.connection.prepareStatement("create extension hstore;").execute();
        } catch (SQLException e) {
        }
        database.connection.prepareStatement("CREATE TABLE users (\n" +
                "                       id SERIAL PRIMARY KEY,\n" +
                "                       name VARCHAR(255),\n" +
                "                       money INT,\n" +
                "                       shares hstore,\n" +
                "                       password VARCHAR(255)\n" +
                ");CREATE TABLE companies (\n" +
                "                           id SERIAL PRIMARY KEY,\n" +
                "                           users INT[],\n" +
                "                           name VARCHAR(255) NOT NULL,\n" +
                "                           key_shareholder_threshold INT NOT NULL,\n" +
                "                           vacant_shares INT NOT NULL,\n" +
                "                           total_shares INT NOT NULL,\n" +
                "                           money INT NOT NULL,\n" +
                "                           share_price INT NOT NULL,\n" +
                "                           password VARCHAR(255)\n" +
                ");").execute();
    }

    @AfterAll
    public static void tearDown() {
        database.dropConnection();
    }

    @AfterEach
    public void clearing() {
        try {
            database.connection.prepareStatement("delete from users where id != " +
                    "-3").execute();
            database.connection.prepareStatement("delete from companies where id != " +
                    "-3").execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // endregion

    @BeforeAll
    static void initMapper() {
        mapper = new ObjectMapper();
    }

    @BeforeEach
    void initService() {
        service = Service.ignite();

        EntityRepository<User> userRepository = new InMemoryUserRepository();
        EntityRepository<Company> companyRepository = new InMemoryCompanyRepository();

//        EntityRepository<User> userRepository = database.user;
//        EntityRepository<Company> companyRepository = database.company;

        CompanyService companyService = new CompanyService(companyRepository);
        UserService userService = new UserService(userRepository);

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
        long companyId = createResponse.id();

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

        createResponse = mapper.readValue(response.body(), EntityIdResponse.class);

        assertEquals(201, response.statusCode());
        long userId = createResponse.id();

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
                                .uri(URI.create("http://localhost:%d/api/usr/%d".formatted(service.port(), userId)))
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
                                .uri(URI.create("http://localhost:%d/api/company/%d".formatted(service.port(), companyId)))
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
                                                        {"sharesDelta":[{"companyId":%d,"countDelta":50}]}""".formatted(companyId)
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/usr/%d/shares".formatted(service.port(), userId)))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );


        assertEquals(200, response.statusCode());

        // Read company
        response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create("http://localhost:%d/api/company/%d".formatted(service.port(), companyId)))
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
                                .uri(URI.create("http://localhost:%d/api/usr/%d".formatted(service.port(), userId)))
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
                                .uri(URI.create("http://localhost:%d/api/usr/%d".formatted(service.port(), userId)))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        assertEquals(200, response.statusCode());

        // Delete company
        response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .DELETE()
                                .uri(URI.create("http://localhost:%d/api/company/%d".formatted(service.port(), companyId)))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        assertEquals(200, response.statusCode());
    }
}
