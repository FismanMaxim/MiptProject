package EndpointsControllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static spark.Spark.*;

class WebsiteControllerTest {
    private static ObjectMapper mapper;

    @BeforeAll
    static void initMapper() {
        mapper = new ObjectMapper();
    }

    @BeforeEach
    void initService() {
        ControllersManager manager = new ControllersManager(List.of(new WebsiteController(/*service,*/ TemplateFactory.freeMarkerEngine(), mapper)));

        manager.start();
        init();
        awaitInitialization();
    }

    @AfterEach
    void stopService() {
        stop();
        awaitStop();
    }

    @Test
    void getMainPageCode() throws Exception {
        // Get the code of the main page
        HttpResponse<String> response = HttpClient.newHttpClient()
                .send(
                        HttpRequest.newBuilder()
                                .GET()
                                .uri(URI.create("http://localhost:%d/".formatted(port())))
                                .build(),
                        HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
                );

        String retrievedHtml = response.body();

        assert retrievedHtml.contains("html");
    }
}