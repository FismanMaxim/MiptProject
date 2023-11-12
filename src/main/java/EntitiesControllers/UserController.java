package EntitiesControllers;

import CustomExceptions.*;
import DTOs.UserDTO;
import Entities.Company;
import Entities.User;
import EntitiesServices.CompanyService;
import EntitiesServices.UserService;
import Requests.AddUserSharesRequest;
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

public class UserController extends EntityController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final CompanyService companyService;

    public UserController(Service service, UserService userService, CompanyService companyService, ObjectMapper objectMapper) {
        super(service, objectMapper);

        this.userService = userService;
        this.companyService = companyService;
    }

    @Override
    public void initializeEndpoints() {
        createUserEndpoint();
        getUserByIdEndpoint();
        updateUserMoneyNameEndpoint();
        updateUserSharesEndpoint();
        deleteUserEndpoint();
    }

    void createUserEndpoint() {
        service.post("/api/usr", (Request request, Response response) -> {
            response.type("application.json");

            UserDTO userDTO;
            try {
                userDTO = objectMapper.readValue(request.body(), UserDTO.class);
            } catch (JsonProcessingException e) {
                return InformOfClientError(LOGGER,
                        "Failed to convert json string to an instance of User: " + request.body(),
                        response,
                        e,
                        404);
            }

            try {
                long createdId = userService.create(userDTO);
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
                User user = userService.getById(id);
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
                    userService.updateMoney(id, deltaMoneyNode.longValue());
                } catch (NegativeMoneyException e) {
                    return InformOfClientError(LOGGER,
                            "Failed to update money since it results in negative amount of money",
                            response,
                            e,
                            400);
                } catch (EntityIdNotFoundException e) {
                    return InformOfClientError(LOGGER,
                            "User with given id not found, id=" + id,
                            response,
                            e,
                            404);
                }
            }

            if (newNameNode != null) {
                try {
                    userService.updateName(id, newNameNode.textValue());
                } catch (EntityIdNotFoundException e) {
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

            User user = userService.getById(id);

            long totalPrice = 0;
            for (int i = 0; i < addUserSharesRequest.sharesDelta().size(); i++) {
                Company company = companyService.getById(addUserSharesRequest.sharesDelta().get(i).companyId());
                int countShares = addUserSharesRequest.sharesDelta().get(i).countDelta();

                company = company.withVacantSharesDelta(-countShares);
                if (countShares > 0 && !company.hasUser(user)) {
                    company = company.withNewUser(user);
                } else if (countShares == -user.getShares().get(company.getId())) {
                    company = company.withoutUser(user);
                }

                companyService.update(company);

                totalPrice += countShares * company.getSharePrice();
            }

            try {
                userService.updateMoney(id, -totalPrice);
                userService.updateShares(id, addUserSharesRequest.sharesDelta());
            } catch (EntityIdNotFoundException e) {
                return InformOfClientError(LOGGER,
                        "User with given id not found, id=" + id,
                        response,
                        e,
                        400);
            } catch (NegativeSharesException e) {
                return InformOfClientError(LOGGER,
                        "Failed to update shares since it results in negative number of shares",
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
                user = userService.getById(id);
            } catch (GetEntityException e) {
                return InformOfClientError(LOGGER,
                        "Failed to find user with id " + id,
                        response,
                        e,
                        404);
            }

            for (Long companyId : user.getShares().keySet()) {
                try {
                    Company company = companyService.getById(companyId);
                    company = company.withVacantSharesDelta(user.getShares().get(companyId));
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
                userService.delete(id);
            } catch (DeleteEntityException e) {
                return InformOfClientError(LOGGER, "Failed to delete user with given id: " + id, response, e, 404);
            }

            response.status(200);
            return "";
        });
    }
}
