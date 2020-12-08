package fr.umlv.localkube.component;

import fr.umlv.localkube.configuration.DockerProperties;
import fr.umlv.localkube.manager.DockerManager;
import fr.umlv.localkube.services.ApplicationService;
import fr.umlv.localkube.utils.OperatingSystem;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class Interceptor extends HandlerInterceptorAdapter {

    private final DockerManager dockerManager;
    private final ApplicationService applicationService;

    public Interceptor(DockerProperties properties, ApplicationService applicationService){
        super();
        this.dockerManager = new DockerManager(OperatingSystem.checkOS(), properties);
        this.applicationService = applicationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        var names = dockerManager.listDeadContainers();
        applicationService.removeAllByDockerInstanceName(names);
        dockerManager.removeAll(names);
        return super.preHandle(request, response, handler);
    }

}
