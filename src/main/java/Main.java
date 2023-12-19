import EndpointsControllers.EntitiesControllers.CompanyController;
import EndpointsControllers.ControllersManager;
import EndpointsControllers.EntitiesControllers.UserController;
import EndpointsControllers.WebsiteController;
import EntitiesServices.CompanyService;
import EntitiesServices.UserService;
import InMemoryRepos.InMemoryCompanyRepository;
import InMemoryRepos.InMemoryUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import database.Database;
import spark.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;

public class Main {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        try {
            Service service = Service.ignite();

            Path path = Paths.get("src/main/resources/login");
            BufferedReader reader = Files.newBufferedReader(path);
            Database database =
                    new Database(DriverManager.getConnection(reader.readLine().trim(),
                            reader.readLine().trim(),
                            reader.readLine().trim()));
            CompanyService companyService =
                    new CompanyService(database.company);

            UserService userService = new UserService(database.user);
            ControllersManager manager = new ControllersManager(List.of(
                    new CompanyController(service, companyService, mapper),
                    new UserController(service, userService, companyService,
                            mapper),
                    new WebsiteController(service, mapper)
            ));

            manager.start();
            service.awaitInitialization();
            service.awaitStop();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
