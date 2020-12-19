package fr.umlv.localkube.component;

import fr.umlv.localkube.services.ApplicationService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class DeadContainerInterceptor extends HandlerInterceptorAdapter {

    private final ApplicationService applicationService;

    public DeadContainerInterceptor(@Lazy ApplicationService applicationService){
        super();
        this.applicationService = applicationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        applicationService.removeAllDeadDockerInstance();
        return super.preHandle(request, response, handler);
    }

}
