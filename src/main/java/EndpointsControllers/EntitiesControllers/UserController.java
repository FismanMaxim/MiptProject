package EndpointsControllers.EntitiesControllers;

import CustomExceptions.*;
import DTOs.UserDTO;
import Entities.Company;
import Entities.User;
import EntitiesServices.CompanyService;
import EntitiesServices.UserService;
import Requests.AddUserSharesRequest;
import Requests.AuthenticationRequest;
import Requests.CreateEntityRequest;
import Responses.EntityIdResponse;
import Responses.FindUserResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

public class UserController extends EntityController<UserService> {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private final CompanyService companyService;

    public UserController(Service service, UserService userService, CompanyService companyService, ObjectMapper objectMapper) {
        super(service, userService, objectMapper);

        this.companyService = companyService;
    }

    @Override
    public void initializeEndpoints() {
        createUserEndpoint();
        getUserByIdEndpoint();
        getUserIdByNamePasswordEndpoint();
        updateUserMoneyNameEndpoint();
        updateUserSharesEndpoint();
        deleteUserEndpoint();
    }

    void createUserEndpoint() {
        service.post("/api/usr", (Request request, Response response) -> {
            response.type("application.json");

            CreateEntityRequest createRequest;
            try {
                createRequest = objectMapper.readValue(request.body(), CreateEntityRequest.class);
            } catch (JsonProcessingException e) {
                return InformOfClientError(LOGGER,
                        "Failed to convert json string: " + request.body(),
                        response,
                        e,
                        400);
            }

            try {
                long createdId = entityService.create(createRequest);
                response.status(201);
                return objectMapper.writeValueAsString(new EntityIdResponse(createdId));
            } catch (CreateEntityException e) {
                return InformOfClientError(LOGGER,
                        "Failed to create user",
                        response,
                        e,
                        400);
            }
        });
    }

    void getUserByIdEndpoint() {
        service.get("/api/usr/:userId", (Request request, Response response) -> {
            response.type("application.json");

            long id;
            try {
                id = Long.parseLong(request.params("userId"));
            } catch (NumberFormatException e) {
                return InformOfClientError(LOGGER,
                        "Failed to convert parameter userId to type Long: " + request.params("userId"),
                        response,
                        e,
                        404);
            }

            try {
                User user = entityService.getById(id);
                response.status(200);
                return objectMapper.writeValueAsString(new FindUserResponse(new UserDTO(user)));
            } catch (GetEntityException e) {
                return InformOfClientError(LOGGER,
                        "Failed to find user by id, id=" + id,
                        response,
                        e,
                        404);
            }
        });
    }

    void getUserIdByNamePasswordEndpoint() {
        service.post("/api/usr/auth", (Request request, Response response) -> {
            response.type("application.json");

            AuthenticationRequest authenticateRequest;
            try {
                authenticateRequest = objectMapper.readValue(request.body(), AuthenticationRequest.class);
            } catch (JsonProcessingException e) {
                return InformOfClientError(LOGGER,
                        "Failed to read body: " + request.body(),
                        response,
                        e,
                        400);
            }

            try {
                long userId = entityService.getByNamePassword(authenticateRequest.name(), authenticateRequest.password()).getId();
                response.status(200);
                return objectMapper.writeValueAsString(new EntityIdResponse(userId));
            } catch (GetEntityException e) {
                return InformOfClientError(LOGGER,
                        "Failed to find user with given username and password",
                        response,
                        e,
                        404);
            }
        });
    }

    void updateUserMoneyNameEndpoint() {
        service.put("/api/usr/:userId", (Request request, Response response) -> {
            response.type("application.json");

            long id;
            try {
                id = Long.parseLong(request.params("userId"));
            } catch (NumberFormatException e) {
                return InformOfClientError(LOGGER,
                        "Failed to convert parameter userId to type Long: " + request.params("userId"),
                        response,
                        e,
                        400);
            }

            JsonNode jsonTree;
            try {
                jsonTree = objectMapper.readTree(request.body());
            } catch (JsonProcessingException e) {
                return InformOfClientError(LOGGER,
                        "Failed to convert body to json tree: " + request.body(),
                        response,
                        e,
                        400);
            }

            JsonNode deltaMoneyNode = jsonTree.get("deltaMoney");
            JsonNode newNameNode = jsonTree.get("name");

            if (deltaMoneyNode == null && newNameNode == null) {
                String errorString = "Failed to handle update user request because it contains no \"deltaMoney\" or \"name\" key";
                LOGGER.warn(errorString);
                response.status(400);
                return getJsonExceptionResponse(errorString);
            }

            if (deltaMoneyNode != null) {
                try {
                    entityService.updateMoney(id, deltaMoneyNode.longValue());
                } catch (NegativeMoneyException e) {
                    return InformOfClientError(LOGGER,
                            "Failed to update money since it results in negative amount of money",
                            response,
                            e,
                            400);
                } catch (EntityNotFoundException e) {
                    return InformOfClientError(LOGGER,
                            "User with given id not found, id=" + id,
                            response,
                            e,
                            404);
                }
            }

            if (newNameNode != null) {
                try {
                    entityService.updateName(id, newNameNode.textValue());
                } catch (EntityNotFoundException e) {
                    return InformOfClientError(LOGGER,
                            "User with given id not found, id=" + id,
                            response,
                            e,
                            404);
                }
            }

            response.status(200);
            return "";
        });
    }

    void updateUserSharesEndpoint() {
        service.put("/api/usr/:userId/shares", (Request request, Response response) -> {
            response.type("application.json");

            long id;
            try {
                id = Long.parseLong(request.params("userId"));
            } catch (NumberFormatException e) {
                return InformOfClientError(LOGGER,
                        "Failed to convert parameter userId to type Long: " + request.params("userId"),
                        response,
                        e,
                        400);
            }


            AddUserSharesRequest addUserSharesRequest;
            try {
                addUserSharesRequest = objectMapper.readValue(request.body(), AddUserSharesRequest.class);
            } catch (JsonProcessingException e) {
                return InformOfClientError(LOGGER,
                        "Failed to convert body to json: " + request.body(),
                        response,
                        e,
                        400);
            }

            User user = entityService.getById(id);

            // Check request validity (user has enough money, companies have enough shares)
            long totalPrice = 0;
            for (int i = 0; i < addUserSharesRequest.sharesDelta().size(); i++) {
                Company company = companyService.getById(addUserSharesRequest.sharesDelta().get(i).companyId());
                int countShares = addUserSharesRequest.sharesDelta().get(i).countDelta();

                // If user is buying shares, the amount of them cannot be less than vacant shares of company
                if (countShares > 0 && company.getVacantShares() < countShares) {
                    return InformOfClientError(LOGGER,
                            "User cannot buy more shares than a company has",
                            response,
                            new NegativeSharesException(),
                            400);
                }
                // If user is selling shares, the amount of them cannot be greater than the amount they have
                if (countShares < 0 && user.countSharesOfCompany(company.getId()) < -countShares) {
                    return InformOfClientError(LOGGER,
                            "User cannot sell more shares than they have",
                            response,
                            new NegativeSharesException(),
                            400);
                }

                totalPrice += countShares * company.getSharePrice();
            }
            if (user.getMoney() < totalPrice) {
                return InformOfClientError(LOGGER,
                        "The total price of shares the user wants to buy exceeds the amount of money they have",
                        response,
                        new NegativeMoneyException(),
                        400);
            }

            // If the validation check was successful, apply the changes
            for (int i = 0; i < addUserSharesRequest.sharesDelta().size(); i++) {
                Company company = companyService.getById(addUserSharesRequest.sharesDelta().get(i).companyId());
                int countShares = addUserSharesRequest.sharesDelta().get(i).countDelta();

                company = company.withVacantSharesDelta(-countShares);
                if (countShares > 0 && !company.hasUser(user)) {
                    company = company.withNewUser(user);
                } else if (countShares + user.countSharesOfCompany(company.getId()) == 0) {
                    company = company.withoutUser(user);
                }

                companyService.update(company);
            }

            try {
                entityService.updateMoney(id, -totalPrice);
                entityService.updateShares(id, addUserSharesRequest.sharesDelta());
            } catch (EntityNotFoundException e) {
                return InformOfClientError(LOGGER,
                        "User with given id not found, id=" + id,
                        response,
                        e,
                        400);
            }

            response.status(200);
            return "";
        });
    }

    void deleteUserEndpoint() {
        service.delete("/api/usr/:userId", (Request request, Response response) -> {
            response.type("application.json");

            long id;
            try {
                id = Long.parseLong(request.params("userId"));
            } catch (NumberFormatException e) {
                return InformOfClientError(LOGGER,
                        "Failed to convert parameter userId to type Long: " + request.params("userId"),
                        response,
                        e,
                        400);
            }

            User user;
            try {
                user = entityService.getById(id);
            } catch (GetEntityException e) {
                return InformOfClientError(LOGGER,
                        "Failed to find user with id " + id,
                        response,
                        e,
                        404);
            }

            for (Long companyId : user.getIdsOfCompaniesWithShares()) {
                try {
                    Company company = companyService.getById(companyId);
                    company = company.withVacantSharesDelta(user.countSharesOfCompany(companyId));
                    company = company.withoutUser(user);
                    companyService.update(company);
                } catch (GetEntityException e) {
                    return InformOfClientError(LOGGER,
                            "Failed to find company with id " + companyId,
                            response,
                            e,
                            404);
                }
            }


            try {
                entityService.delete(id);
            } catch (DeleteEntityException e) {
                return InformOfClientError(LOGGER, "Failed to delete user with given id: " + id, response, e, 404);
            }

            response.status(200);
            return "";
        });
    }
}
