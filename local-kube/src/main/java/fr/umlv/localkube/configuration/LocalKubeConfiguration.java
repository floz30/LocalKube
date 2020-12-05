package fr.umlv.localkube.configuration;

import fr.umlv.localkube.component.Interceptor;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;

@Configuration
public class LocalKubeConfiguration implements WebMvcConfigurer {

    private final ServletWebServerApplicationContext server;
    private final Interceptor interceptor;

    public LocalKubeConfiguration(ServletWebServerApplicationContext server,Interceptor interceptor){
        this.server = server;
        this.interceptor = interceptor;
    }

    public void addServicePort(int servicePort){
        var tomcatWebServer = TomcatWebServer.class.cast(server.getWebServer());
        tomcatWebServer.getTomcat().setConnector(connectorFromPort(servicePort));
    }

    public void removeServicePort(int servicePort) throws IOException, LifecycleException {
        var tomcatWebServer = TomcatWebServer.class.cast(server.getWebServer());
        var service = tomcatWebServer.getTomcat().getService();
        var connectors = service.findConnectors();
        for(var connector : connectors ){
            if(connector.getPort() == servicePort){
                connector.stop();
                connector.destroy();
                service.removeConnector(connector);
                return;
            }
        }
        throw new IOException("Connector not found for port : " + servicePort);
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
        registry.addInterceptor(interceptor);
    }
}