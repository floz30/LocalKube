package fr.umlv.localkube.repository;

import fr.umlv.localkube.model.Application;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ApplicationRepositoryTest {

    @Test
    @Tag("save")
    void shouldSaveApplication(){
        var applicationRepository = new ApplicationRepository();
        var application = Application.initializeApp("demo:8081",1);
        applicationRepository.save(application);
        assertEquals(Optional.of(application),applicationRepository.findById(1));
    }

    @Test
    @Tag("save")
    void shouldFailOnNullApplication(){
        var applicationRepository = new ApplicationRepository();
        assertThrows(NullPointerException.class,() -> applicationRepository.save(null));
    }

    @Test
    @Tag("applicationList")
    void shouldListApplicationsSaved(){
        var applicationRepository = new ApplicationRepository();
        var application1 = Application.initializeApp("demo:8081",1);
        var application2 = Application.initializeApp("demo:8081",2);
        applicationRepository.save(application1);
        applicationRepository.save(application2);
        assertEquals(List.of(application1,application2),applicationRepository.applicationList());
    }

    @Test
    @Tag("findById")
    void shouldFindApplication(){
        var applicationRepository = new ApplicationRepository();
        var application = Application.initializeApp("demo:8081",4);
        applicationRepository.save(application);
        assertEquals(Optional.of(application),applicationRepository.findById(4));
    }

    @Test
    @Tag("findId")
    void shouldFindIdWithSpecificPredicate(){
        var applicationRepository = new ApplicationRepository();
        var application = Application.initializeApp("demo:8081",1);
        applicationRepository.save(application);
        assertEquals(OptionalInt.of(1),applicationRepository.findId(entry -> entry.getValue().equals(application)));
    }

    @Test
    @Tag("getMaxId")
    void shouldGetValidMaxId(){
        var applicationRepository = new ApplicationRepository();
        var application1 = Application.initializeApp("demo:8081",1);
        var application2 = Application.initializeApp("demo:8081",2);
        applicationRepository.save(application1);
        applicationRepository.save(application2);
        assertEquals(2,applicationRepository.getMaxId());
    }

    @Test
    @Tag("removeAllDeadDockerInstance")
    void shouldRemoveAllInstances(){
        var applicationRepository = new ApplicationRepository();
        var application = Application.initializeApp("demo:8081",1);
        applicationRepository.save(application);
        applicationRepository.removeAllDeadDockerInstance(application.getDockerInstance());
        assertEquals(Optional.empty(), applicationRepository.findById(1));
    }

}
