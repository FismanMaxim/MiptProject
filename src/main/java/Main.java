import EndpointsControllers.ControllersManager;
import EndpointsControllers.EntitiesControllers.CompanyController;
import EndpointsControllers.EntitiesControllers.UserController;
import EndpointsControllers.TemplateFactory;
import EndpointsControllers.WebsiteController;
import EntitiesServices.CompanyService;
import EntitiesServices.UserService;
import InMemoryRepos.InMemoryCompanyRepository;
import InMemoryRepos.InMemoryUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import static spark.Spark.staticFileLocation;

public class Main {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        staticFileLocation("/public");

        // Service service = Service.ignite();

        /*get("/test", (Request request, Response response) -> {
            response.type("text/html; charset=utf-8");

            Map<String, Object> model = new HashMap<>();
            return TemplateFactory.freeMarkerEngine().render(new ModelAndView(model, "index1.html"));
        });*/

        CompanyService companyService = new CompanyService(new InMemoryCompanyRepository());

        UserService userService = new UserService(new InMemoryUserRepository());
        ControllersManager manager = new ControllersManager(List.of(
                new WebsiteController(/*service, */TemplateFactory.freeMarkerEngine(), mapper),
                new CompanyController(/*service,*/ companyService, mapper),
                new UserController(/*service, */userService, companyService, mapper)
        ));

        manager.start();
        //service.awaitInitialization();
        //service.awaitStop();
    }
}
