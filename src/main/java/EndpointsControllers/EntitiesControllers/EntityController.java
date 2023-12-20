package EndpointsControllers.EntitiesControllers;

import EndpointsControllers.EndpointsController;
import EntitiesServices.EntityService;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class EntityController<T extends EntityService> extends EndpointsController {
    protected T entityService;

    public EntityController(/*Service service,*/ T entityService, ObjectMapper objectMapper) {
        super(/*service, */objectMapper);

        this.entityService = entityService;
    }
}
