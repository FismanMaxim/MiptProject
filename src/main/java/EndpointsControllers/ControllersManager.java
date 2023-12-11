package EndpointsControllers;

import java.util.List;

public class ControllersManager {
    private final List<EndpointsController> controllers;


    public ControllersManager(List<EndpointsController> controllers) {
        this.controllers = controllers;
    }

    public void start() {
        for (EndpointsController controller : controllers) controller.initializeEndpoints();
    }
}
