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
import org.springframework.context.annotation.Lazy;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.util.Arrays;

@Configuration
public class LocalKubeConfiguration implements WebMvcConfigurer {

    private final Tomcat tomcat;
    private final DeadContainerInterceptor deadContainerInterceptor;

    public LocalKubeConfiguration(ServletWebServerApplicationContext server, DeadContainerInterceptor deadContainerInterceptor) {
        this.tomcat = ((TomcatWebServer) server.getWebServer()).getTomcat();
        this.deadContainerInterceptor = deadContainerInterceptor;
    }

    public void addServicePort(int servicePort) {
        tomcat.setConnector(connectorFromPort(servicePort));
    }

    public void removeServicePort(int servicePort) throws IOException {
        var service = tomcat.getService();
        var connectors = service.findConnectors();
        var optionalConnector = Arrays.stream(connectors).filter(connector -> connector.getPort() == servicePort).findAny();
        if(optionalConnector.isEmpty()){
            throw new IOException("Connector not found for port : " + servicePort);
        }
        removeConnector(optionalConnector.get(),service);
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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(deadContainerInterceptor);
    }

    @Bean
    public Jdbi createJdbi(DataBaseProperties properties, ApplicationService applicationService) {
        return Jdbi.create(properties.getUrl(), properties.getUsername(), properties.getPassword()).installPlugin(new SQLitePlugin()).installPlugin(new SqlObjectPlugin()).registerRowMapper(new Log.LogMapper(applicationService));
    }

    @Bean
    public DockerManager createDockerManager(DockerProperties properties) throws IOException, InterruptedException {
        return new DockerManager(OperatingSystem.checkOS(),properties);
    }

    @Bean
    public LogRepository logRepository(Jdbi jdbi) {
        return jdbi.onDemand(LogRepository.class);
    }
}