package fr.umlv.localkube.component;

import fr.umlv.localkube.services.ApplicationService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interceptor class to process before each HTTP request.
 */
@Component
public class DeadContainerInterceptor extends HandlerInterceptorAdapter {

    private final ApplicationService applicationService;

    public DeadContainerInterceptor(@Lazy ApplicationService applicationService){
        super();
        this.applicationService = applicationService;
    }

    /**
     * Removes all dead applications from applicationService before each HTTP request.
     *
     * @param request current HTTP request
     * @param response current HTTP response
     * @param handler chosen handler to execute, for type and/or instance evaluation
     * @return true if the execution chain should proceed with the next interceptor or the handler itself. Else, DispatcherServlet assumes that this interceptor has already dealt with the response itself.
     * @throws Exception in case of errors
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        applicationService.removeAllDeadDockerInstance();
        return super.preHandle(request, response, handler);
    }

}
