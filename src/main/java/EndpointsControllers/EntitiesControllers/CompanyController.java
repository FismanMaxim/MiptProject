package EndpointsControllers.EntitiesControllers;

import CustomExceptions.*;
import DTOs.CompanyDTO;
import Entities.Company;
import EntitiesServices.CompanyService;
import Requests.AuthenticationRequest;
import Responses.EntityIdResponse;
import Responses.FindCompanyResponse;
import Responses.GetAllCompaniesResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.ArrayList;
import java.util.List;

public class CompanyController extends EntityController<CompanyService> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyController.class);

    public CompanyController(Service service, CompanyService companyService, ObjectMapper objectMapper) {
        super(service, companyService, objectMapper);
    }

    @Override
    public void initializeEndpoints() {
        createCompanyEndpoint();
        getCompanyByIdEndpoint();
        getCompanyIdByNamePasswordEndpoint();
        getAllCompaniesEndpoint();
        updateCompanyEndpoint();
        deleteCompanyEndpoint();
    }

    private void createCompanyEndpoint() {
        service.post("/api/company", (Request request, Response response) -> {
            response.type("application.json");

            CompanyDTO companyDTO;
            try {
                companyDTO = objectMapper.readValue(request.body(), CompanyDTO.class);
            } catch (JsonProcessingException e) {
                return InformOfClientError(LOGGER,
                        "Failed to convert json string to an instance of Company: " + request.body(),
                        response,
                        e,
                        400);
            }

            try {
                long createdId = entityService.create(companyDTO);
                response.status(201);
                return objectMapper.writeValueAsString(new EntityIdResponse(createdId));
            } catch (CreateEntityException e) {
                return InformOfClientError(LOGGER,
                        "Failed to create company",
                        response,
                        e,
                        400);
            }
        });
    }

    private void getCompanyByIdEndpoint() {
        service.get("/api/company/:companyId", (Request request, Response response) -> {
            response.type("application.json");

            long id;
            try {
                id = Long.parseLong(request.params("companyId"));
            } catch (NumberFormatException e) {
                return InformOfClientError(LOGGER,
                        "Failed to convert parameter companyId to type Long: " + request.params("companyId"),
                        response,
                        e,
                        400);
            }

            try {
                Company company = entityService.getById(id);
                response.status(200);
                return objectMapper.writeValueAsString(new FindCompanyResponse(new CompanyDTO(company)));
            } catch (GetEntityException e) {
                return InformOfClientError(LOGGER,
                        "Failed to find company by id, id=" + id,
                        response,
                        e,
                        404);
            }
        });
    }

    void getCompanyIdByNamePasswordEndpoint() {
        service.post("/api/company/auth", (Request request, Response response) -> {
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
                long companyId = entityService.getByNamePassword(authenticateRequest.name(), authenticateRequest.password()).getId();
                response.status(200);
                return objectMapper.writeValueAsString(new EntityIdResponse(companyId));
            } catch (GetEntityException e) {
                return InformOfClientError(LOGGER,
                        "Failed to find user with given username and password",
                        response,
                        e,
                        404);
            }
        });
    }

    private void getAllCompaniesEndpoint() {
        service.get("/api/company", (Request request, Response response) -> {
            response.type("application.json");

            try {
                List<Company> companies = entityService.getAll();
                List<FindCompanyResponse> listResponse = new ArrayList<>();
                for (Company company : companies)
                    listResponse.add(new FindCompanyResponse(new CompanyDTO(company)));

                response.status(200);
                return objectMapper.writeValueAsString(new GetAllCompaniesResponse(listResponse));
            } catch (GetEntityException e) {
                return InformOfClientError(LOGGER,
                        "Failed to get all companies",
                        response,
                        e,
                        404);
            }
        });
    }

    private void updateCompanyEndpoint() {
        service.put("/api/company/:companyId", (Request request, Response response) -> {
            response.type("application.json");

            long id;
            try {
                id = Long.parseLong(request.params("companyId"));
            } catch (NumberFormatException e) {
                return InformOfClientError(LOGGER,
                        "Failed to convert parameter companyId to type Long: " + request.params("companyId"),
                        response,
                        e,
                        404);
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

            Company company;
            try {
                company = entityService.getById(id);
            } catch (GetEntityException e) {
                return InformOfClientError(LOGGER,
                        "Failed to find company by id: " + id,
                        response,
                        e,
                        404);
            }

            JsonNode nameNode = jsonTree.get("name");
            JsonNode deltaSharesNode = jsonTree.get("deltaShares");
            JsonNode thresholdNode = jsonTree.get("threshold");
            JsonNode moneyNode = jsonTree.get("deltaMoney");
            JsonNode sharePrice = jsonTree.get("sharePrice");

            try {
                if (nameNode != null) {
                    company = company.withName(nameNode.textValue());
                }
                if (deltaSharesNode != null) {
                    company = company.withCountShares(company.getTotalShares() + deltaSharesNode.intValue(), false);
                }
                if (thresholdNode != null) {
                    company = company.withThreshold(thresholdNode.intValue());
                }
                if (moneyNode != null)
                    company = company.withDeltaMoney(moneyNode.longValue());
                if (sharePrice != null)
                    company = company.withSharePrice(sharePrice.longValue());
            } catch (IllegalArgumentException e) {
                String message = "Illegal arguments for update request: " + e;
                LOGGER.warn(message);
                response.status(400);
                return getJsonExceptionResponse(message);
            }

            try {
                entityService.update(company);
            } catch (UpdateEntityException e) {
                return InformOfClientError(LOGGER,
                        "Failed to update company: ",
                        response,
                        e,
                        400);
            }

            response.status(200);
            return "";
        });
    }

    private void deleteCompanyEndpoint() {
        service.delete("/api/company/:companyId", (Request request, Response response) -> {
            response.type("application.json");

            long id;
            try {
                id = Long.parseLong(request.params("companyId"));
            } catch (NumberFormatException e) {
                return InformOfClientError(LOGGER,
                        "Failed to convert parameter companyId to type Long: " + request.params("companyId"),
                        response,
                        e,
                        400);
            }

            try {
                entityService.delete(id);
            } catch (DeleteEntityException e) {
                return InformOfClientError(LOGGER, "Failed to delete company with given id: " + id, response, e, 404);
            }

            response.status(200);
            return "";
        });
    }
}
