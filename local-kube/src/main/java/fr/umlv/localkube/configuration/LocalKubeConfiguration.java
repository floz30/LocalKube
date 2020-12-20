package fr.umlv.localkube.configuration;

import fr.umlv.localkube.component.DeadContainerInterceptor;
import fr.umlv.localkube.manager.DockerManager;
import fr.umlv.localkube.model.Log;
import fr.umlv.localkube.repository.LogRepository;
import fr.umlv.localkube.services.ApplicationService;
import fr.umlv.localkube.utils.OperatingSystem;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlite3.SQLitePlugin;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.Arrays;


/**
 * Configuration class for the LocalKube application.
 */
@Configuration
public class LocalKubeConfiguration implements WebMvcConfigurer {

    private final Tomcat tomcat;
    private final DeadContainerInterceptor deadContainerInterceptor;

    public LocalKubeConfiguration(ServletWebServerApplicationContext server, DeadContainerInterceptor deadContainerInterceptor) {
        //retrieve the Tomcat class from the application server
        this.tomcat = ((TomcatWebServer) server.getWebServer()).getTomcat();
        this.deadContainerInterceptor = deadContainerInterceptor;
    }

    /**
     * Adds the deadContainerInterceptor to the InterceptorRegistry.
     *
     * @param registry Interceptor registry.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(deadContainerInterceptor);
    }

    /**
     * Adds Service Port for inter application communication.
     *
     * @param servicePort New port to listen on.
     */
    public void addServicePort(int servicePort) {
        tomcat.setConnector(connectorFromPort(servicePort));
    }

    /**
     * Creates the Jdbi instance with correct properties.
     * @param properties Database properties.
     * @param applicationService application Service.
     * @return Jdbi instance.
     */
    @Bean
    public Jdbi createJdbi(DataBaseProperties properties, ApplicationService applicationService) {
        return Jdbi.create(properties.getUrl(), properties.getUsername(), properties.getPassword()).installPlugin(new SQLitePlugin()).installPlugin(new SqlObjectPlugin()).registerRowMapper(new Log.LogMapper(applicationService));
    }

    /**
     * Creates the docker manager with correct properties.
     * @param properties Docker properties.
     * @return DockerManager object.
     * @throws IOException If docker swarm initialization fails.
     * @throws InterruptedException If docker swarm initialization fails.
     */
    @Bean
    public DockerManager createDockerManager(DockerProperties properties) throws IOException, InterruptedException {
        return new DockerManager(OperatingSystem.checkOS(), properties);
    }

    /**
     * Creates the log repository instance.
     * @param jdbi Jdbi instance.
     * @return New log repository.
     */
    @Bean
    public LogRepository logRepository(Jdbi jdbi) {
        return jdbi.onDemand(LogRepository.class);
    }

    /**
     * Removes Service Port.
     *
     * @param servicePort Port to remove.
     * @throws IOException If port was not registered in the server.
     */
    public void removeServicePort(int servicePort) throws IOException {
        var service = tomcat.getService();
        var connectors = service.findConnectors();
        var optionalConnector = Arrays.stream(connectors).filter(connector -> connector.getPort() == servicePort).findAny();
        if (optionalConnector.isEmpty()) {
            throw new IOException("Connector not found for port : " + servicePort);
        }
        removeConnector(optionalConnector.get(), service);
    }

    private void removeConnector(Connector connector, Service service) {
        try {
            connector.stop();
            connector.destroy();
            service.removeConnector(connector);
        } catch (LifecycleException e) {
            throw new RuntimeException(e);
        }
    }

    private Connector connectorFromPort(int port) {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        connector.setPort(port);
        connector.setSecure(true);
        connector.setDomain("localhost");
        return connector;
    }
}