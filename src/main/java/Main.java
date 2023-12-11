import EntitiesControllers.CompanyController;
import EntitiesControllers.ControllersManager;
import EntitiesControllers.UserController;
import EntitiesServices.CompanyService;
import EntitiesServices.UserService;
import InMemoryRepos.InMemoryCompanyRepository;
import InMemoryRepos.InMemoryUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Service;

import java.util.List;
public class Main {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        Service service = Service.ignite();
        CompanyService companyService = new CompanyService(new InMemoryCompanyRepository());

        UserService userService = new UserService(new InMemoryUserRepository());
        ControllersManager manager = new ControllersManager(List.of(
                new CompanyController(service, companyService, mapper),
                new UserController(service, userService, companyService, mapper)
        ));

        manager.start();
        service.awaitInitialization();
        service.awaitStop();
    }
}
