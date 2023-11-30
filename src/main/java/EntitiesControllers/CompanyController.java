package EntitiesControllers;

import CustomExceptions.*;
import DTOs.CompanyDTO;
import Entities.Company;
import EntitiesServices.CompanyService;
import Responses.EntityIdResponse;
import Responses.FindCompanyResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Service;

public class CompanyController extends EntityController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyController.class);

    private final CompanyService companyService;

    public CompanyController(Service service, CompanyService companyService, ObjectMapper objectMapper) {
        super(service, objectMapper);

        this.companyService = companyService;
    }

    @Override
    public void initializeEndpoints() {
        createCompanyEndpoint();
        getCompanyByIdEndpoint();
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
                        "Failed to convert json string to an instance of User: " + request.body(),
                        response,
                        e,
                        404);
            }

            try {
                long createdId = companyService.create(companyDTO);
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
                        404);
            }

            try {
                Company company = companyService.getById(id);
                response.status(200);
                return objectMapper.writeValueAsString(new FindCompanyResponse(
                        company.getCompanyName(), company.getTotalShares(), company.getVacantShares(),
                        company.getKeyShareholderThreshold(), company.getMoney(), company.getSharePrice(),
                        company.getUsersDTOs()
                ));
            } catch (GetEntityException e) {
                return InformOfClientError(LOGGER,
                        "Failed to find company by id, id=" + id,
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
                company = companyService.getById(id);
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
            JsonNode moneyNode = jsonTree.get("money");

            try {
                if (nameNode != null) {
                    company = company.withName(nameNode.textValue());
                }
                if (deltaSharesNode != null) {
                    company = company.withCountShares(company.getTotalShares() + deltaSharesNode.intValue());
                }
                if (thresholdNode != null) {
                    company = company.withThreshold(thresholdNode.intValue());
                }
                if (moneyNode != null)
                    company = company.withMoney(moneyNode.longValue());
            } catch (IllegalArgumentException e) {
                String message = "Illegal arguments for update request: " + e;
                LOGGER.warn(message);
                response.status(400);
                return getJsonExceptionResponse(message);
            }

            try {
                companyService.update(company);
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
                companyService.delete(id);
            } catch (DeleteEntityException e) {
                return InformOfClientError(LOGGER, "Failed to delete company with given id: " + id, response, e, 404);
            }

            response.status(200);
            return "";
        });
    }
}
