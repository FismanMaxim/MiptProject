import EndpointsControllers.EntitiesControllers.CompanyController;
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

import static spark.Spark.*;

public class Main {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {    staticFileLocation("/public");
        init();
        CompanyService companyService = new CompanyService(new InMemoryCompanyRepository());
        UserService userService = new UserService(new InMemoryUserRepository());    ControllersManager manager = new ControllersManager(List.of(
            new WebsiteController(TemplateFactory.freeMarkerEngine(), mapper),            new CompanyController(companyService, mapper),
            new UserController(userService, companyService, mapper)    ));
        manager.start();
        awaitInitialization();    awaitStop();
    }
}
