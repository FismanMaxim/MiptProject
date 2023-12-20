package Integration;

import EndpointsControllers.ControllersManager;
import EndpointsControllers.EntitiesControllers.CompanyController;
import EndpointsControllers.EntitiesControllers.UserController;
import Entities.Company;
import Entities.User;
import EntitiesRepositories.EntityRepository;
import EntitiesServices.CompanyService;
import EntitiesServices.UserService;
import Responses.EntityIdResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.Database;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static spark.Spark.*;

public class InvalidBuySellRequestsTest {
    private static ObjectMapper mapper;
    private static Database database;

    private long companyId, userId;

    // region Before/After all/each
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

    @BeforeAll
    static void setUpMapper() {
        mapper = new ObjectMapper();
    }

    @BeforeEach
    void createCompanyAndUser() throws Exception {
        // region Create company
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .POST(
                                        HttpRequest.BodyPublishers.ofString(
                                                "{\"name\": \"testName\", \"password\": \"companyPass\"}"
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/company".formatted(port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        EntityIdResponse createResponse = mapper.readValue(response.body(), EntityIdResponse.class);

        assertEquals(201, response.statusCode());
        companyId = createResponse.id();
        // endregion

        // region Create user
        response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .POST(
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                        {"name": "testUsername", "password": "pass" }"""
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/usr".formatted(port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        createResponse = mapper.readValue(response.body(), EntityIdResponse.class);

        assertEquals(201, response.statusCode());
        userId = createResponse.id();
        // endregion
    }

    @BeforeEach
    void initService() {
        EntityRepository<User> userRepository = database.user;
        EntityRepository<Company> companyRepository = database.company;

        CompanyService companyService = new CompanyService(companyRepository);
        UserService userService = new UserService(userRepository);

        ControllersManager manager = new ControllersManager(List.of(
                new CompanyController(/*service,*/ companyService, mapper),
                new UserController(/*service,*/ userService, companyService, mapper)
        ));

        manager.start();
        init();
        awaitInitialization();
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

    @AfterEach
    void stopService() {
        stop();
        awaitStop();
    }
    // endregion

    @Test
    void cannotBuyMoreSharesThanCompanyHas() throws Exception {
        // region local constants
        final int countSharesPart = 100;
        final int countUserBuys = countSharesPart + 1;
        final int sharePrice = 50;
        // endregion

        // region Update user
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .PUT(
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                        {"deltaMoney": %d}""".formatted(countSharesPart * sharePrice)
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/usr/%d".formatted(port(), userId)))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        assertEquals(200, response.statusCode());
        // endregion

        // region Update company
        response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .PUT(
                                        HttpRequest.BodyPublishers.ofString(
                                                "{\"deltaShares\": %d, \"sharePrice\": %d, \"threshold\": 25}"
                                                        .formatted(countSharesPart, sharePrice)
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/company/%d".formatted(port(), companyId)))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        assertEquals(200, response.statusCode());
        // endregion

        // User buys shares in the amount of 200 (must throw exception)
        response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .PUT(
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                        {"sharesDelta":[{"companyId":%d,"countDelta":%d}]}""".formatted(companyId, countUserBuys)
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/usr/%d/shares".formatted(port(), userId)))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );
        assertEquals(400, response.statusCode());
    }

    @Test
    void cannotSellMoreSharesThanUserHas() throws IOException, InterruptedException {
        // region local constants
        final int buySharesCount = 50;
        final int sellSharesCount = buySharesCount + 1; // user tries to sell more shares than they have
        final int sharePrice = 50;
        // endregion

        // region Update user
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .PUT(
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                        {"deltaMoney": %d}""".formatted(buySharesCount * sharePrice)
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/usr/%d".formatted(port(), userId)))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        assertEquals(200, response.statusCode());
        // endregion

        // region Update company
        response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .PUT(
                                        HttpRequest.BodyPublishers.ofString(
                                                "{\"deltaShares\": %d, \"sharePrice\": %d, \"threshold\": 25}"
                                                        .formatted(buySharesCount, sharePrice)
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/company/%d".formatted(port(), companyId)))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        assertEquals(200, response.statusCode());
        // endregion

        // User buys shares
        response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .PUT(
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                        {"sharesDelta":[{"companyId":%d,"countDelta":%d}]}""".formatted(companyId, buySharesCount)
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/usr/%d/shares".formatted(port(), userId)))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );
        assertEquals(200, response.statusCode());

        // User sells shares
        response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .PUT(
                                        HttpRequest.BodyPublishers.ofString(
                                                """
                                                        {"sharesDelta":[{"companyId":%d,"countDelta":%d}]}""".formatted(companyId, -sellSharesCount)
                                        )
                                )
                                .uri(URI.create("http://localhost:%d/api/usr/%d/shares".formatted(port(), userId)))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );
        assertEquals(400, response.statusCode());
    }
}
