package fr.umlv.localkube;

import com.google.cloud.tools.jib.api.*;
import com.google.cloud.tools.jib.api.buildplan.AbsoluteUnixPath;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
@RestController
public class LocalKubeApplication {

    public static void main(String[] args) throws RegistryException, InterruptedException, ExecutionException, IOException, CacheDirectoryCreationException, InvalidImageReferenceException {
        SpringApplication.run(LocalKubeApplication.class, args);
    }

    private static void buildDockerImage() throws IOException, InvalidImageReferenceException, InterruptedException, ExecutionException, RegistryException, CacheDirectoryCreationException {
        //JavaContainerBuilder.from("openjdk:15").addProjectDependencies(Paths.get("target/demo-0.0.1-SNAPSHOT.jar")).toContainerBuilder().containerize(Containerizer.to(DockerDaemonImage.named("demobox")));
        Jib.from("openjdk:15").addLayer(Arrays.asList(Paths.get("../apps/demo-0.0.1-SNAPSHOT.jar")), AbsoluteUnixPath.get("/")).setEntrypoint("java","--enable-preview","-jar","demo-0.0.1-SNAPSHOT.jar")
                .containerize(Containerizer.to(TarImage.at(Paths.get("../docker-images/demo")).named("demo")));
    }
}
