package EndpointsControllers;

import EndpointsControllers.EntitiesControllers.CompanyController;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.freemarker.FreeMarkerEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.get;

public class WebsiteController extends EndpointsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyController.class);
    private final FreeMarkerEngine freeMarkerEngine;

    public WebsiteController(FreeMarkerEngine freeMarkerEngine, ObjectMapper objectMapper) {
        super(objectMapper);

        this.freeMarkerEngine = freeMarkerEngine;
    }

    @Override
    public void initializeEndpoints() {
        getMainPageEndpoint();
    }

    private void getMainPageEndpoint() {
        /*service.*/get("/", (Request request, Response response) -> {
            response.type("text/html; charset=utf-8");

            Map<String, Object> model = new HashMap<>();
            return freeMarkerEngine.render(new ModelAndView(model, "index.html"));
        });
    }
}
