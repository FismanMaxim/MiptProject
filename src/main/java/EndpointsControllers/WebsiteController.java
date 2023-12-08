package EndpointsControllers;

import EndpointsControllers.EntitiesControllers.CompanyController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

import java.io.*;
import java.util.Objects;

public class WebsiteController extends EndpointsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyController.class);

    public WebsiteController(Service service, ObjectMapper objectMapper) {
        super(service, objectMapper);
    }

    @Override
    public void initializeEndpoints() {
        getMainPageEndpoint();
    }

    private void getMainPageEndpoint() {
        service.get("/", (Request request, Response response) -> {
            response.type("application/html");

            try {
                String indexHtml;

                InputStream inputStream = getClass().getResourceAsStream("/index.html");
                try (BufferedReader br = new BufferedReader(new InputStreamReader(Objects.requireNonNull(inputStream)))) {
                    final StringBuilder sb = new StringBuilder();
                    String line = br.readLine();

                    while (line != null) {
                        sb.append(line);
                        sb.append(System.lineSeparator());
                        line = br.readLine();
                    }

                    indexHtml = sb.toString();
                }

                return indexHtml;
            } catch (NullPointerException e) {
                return InformOfClientError(LOGGER,
                        "File index.html not found",
                        response,
                        e,
                        404);
            } catch (IOException e) {
                return InformOfClientError(LOGGER,
                        "Failed to read index.html",
                        response,
                        e,
                        400);
            }
        });
    }
}
