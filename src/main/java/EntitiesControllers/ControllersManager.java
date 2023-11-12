package EntitiesControllers;

import java.util.List;

public class ControllersManager {
    private final List<EntityController> controllers;


    public ControllersManager(List<EntityController> controllers) {
        this.controllers = controllers;
    }

    public void start() {
        for (EntityController controller : controllers) controller.initializeEndpoints();
    }
}
