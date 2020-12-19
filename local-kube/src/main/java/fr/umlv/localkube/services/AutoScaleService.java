package fr.umlv.localkube.services;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AutoScaleService {

    private Map<String, Integer> instances = new HashMap<>();
    private final ApplicationService applicationService;

    public AutoScaleService(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     *
     * @param params
     */
    public Map<String, String> update(Map<String, Integer> params) {
        this.instances = params;
        // TODO docker operation
        return statusOutput();
    }

    /**
     *
     * @return
     */
    public Map<String, String> status() {
        return statusOutput();
    }

    /**
     *
     * @return
     */
    public Map<String, Integer> stop() {

        return instances;
    }

    /**
     *
     * @return
     */
    private Map<String, String> statusOutput() {
        var result = new HashMap<String, String>();
        for (var param : instances.entrySet()) {
            if (!param.getKey().contains(":")) {
                throw new IllegalArgumentException("Wrong format for app, must be <NAME>:<PORT>.");
            }
            var appCount = applicationService.getCountInstance(param.getKey());
            switch (param.getValue().compareTo(appCount)) {
                case 0 -> result.put(param.getKey(), "no action");
                case 1 -> result.put(param.getKey(), "need to start " + (param.getValue() - appCount) + " instance(s)");
                case -1 -> result.put(param.getKey(), "need to stop " + (appCount - param.getValue()) + " instance(s)");
                default -> throw new IllegalStateException("issue with compareTo");
            }
        }
        // TODO docker operation
        return result;
    }
}
