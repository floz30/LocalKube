package fr.umlv.localkube.repository;

import fr.umlv.localkube.model.Application;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Repository
public class ApplicationRepository {
    private final Map<Integer, Application> applications;

    public ApplicationRepository() {
        applications = new HashMap<>();
    }

    /**
     * Save an application.
     *
     * @param application the application to save
     */
    public void save(Application application) {
        if (application.getId() <= 0) {
            throw new IllegalArgumentException("application id can't be negative or zero");
        }
        Objects.requireNonNull(application);
        applications.put(application.getId(), application);
    }

    /**
     * Returns a list of all applications.
     *
     * @return a list of Application
     */
    public List<Application> applicationList() {
        return new ArrayList<>(applications.values());
    }

    /**
     * Returns the application whose id matches to the specified one.
     *
     * @param id id to search
     * @return the entity with the specified id or Optional.empty() if none found
     */
    public Optional<Application> findById(int id) {
        return Optional.ofNullable(applications.get(id));
    }

    /**
     * Returns the application id whose predicate matches to the specified one.
     *
     * @param predicate filter to apply
     * @return the application id
     */
    public OptionalInt findId(Predicate<Map.Entry<Integer, Application>> predicate) {
        return getFilteredStream(predicate)
                .mapToInt(Map.Entry::getKey)
                .findFirst();
    }

    private Stream<Map.Entry<Integer, Application>> getFilteredStream(Predicate<Map.Entry<Integer, Application>> predicate) {
        return applications.entrySet().stream()
                .filter(predicate);
    }

    /**
     * Returns the next usable identifier.
     * @return max id + 1
     */
    public int getMaxId() {
        return applications.keySet().stream()
                .mapToInt(x -> x)
                .max()
                .orElse(0);
    }

    /**
     * Removes all applications that have their dockerInstance in array.
     */
    public void removeAllDeadDockerInstance(String instance) {
        getFilteredStream(entry -> entry.getValue().getDockerInstance().equals(instance))
                .findFirst()
                .ifPresent(application -> applications.remove(application.getKey()));
    }

}
