 import EndpointsControllers.EntitiesControllers.CompanyController;
import EndpointsControllers.ControllersManager;
import EndpointsControllers.EntitiesControllers.UserController;
import EntitiesServices.CompanyService;
import EntitiesServices.UserService;
import InMemoryRepos.InMemoryCompanyRepository;
import InMemoryRepos.InMemoryUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
 import database.Database;
 import spark.Service;

 import java.sql.DriverManager;
 import java.sql.SQLException;
 import java.util.List;
public class Main {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        try {
            Service service = Service.ignite();
            Database database = new Database(DriverManager.getConnection("jdbc" +
                            ":postgresql" +
                            "://cornelius.db.elephantsql.com:5432/hmtdjque",
                    "hmtdjque",
                    "mW9O7Imtz3eqjtvVolLGZ4gWlC9VuKMh"));
            CompanyService companyService =
                    new CompanyService(database.company);

            UserService userService = new UserService(database.user);
            ControllersManager manager = new ControllersManager(List.of(
                    new CompanyController(service, companyService, mapper),
                    new UserController(service, userService, companyService, mapper)
            ));

            manager.start();
            service.awaitInitialization();
            service.awaitStop();
        }
        catch (SQLException e){

        }
    }
}
