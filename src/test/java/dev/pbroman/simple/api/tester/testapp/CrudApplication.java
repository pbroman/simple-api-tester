package dev.pbroman.simple.api.tester.testapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Simple CRUD application exposing APIs for create, read, search, update and delete operations.
 */
@SpringBootApplication(scanBasePackageClasses = CrudApplication.class)
@RestController
public class CrudApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrudApplication.class, args);
    }

    private final ObjectMapper objectMapper;

    public CrudApplication(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /*
     * "Database" with users having an id and a unique username.
     */
    private final Map<String, User> database = new HashMap<>();

    @PostMapping( path = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser(@RequestBody String body) throws JsonProcessingException {

        var user = objectMapper.readValue(body, User.class);
        if (searchUserInDB(user.username) != null) {
            throw new ResourceAlreadyPresent("User with username " + user.username + " already exists");
        }
        var id = UUID.randomUUID().toString();
        user = user.withId(id);
        database.put(id, user);

        return user;
    }

    @GetMapping( path = "/user/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public User readUser(@PathVariable String id) {
        if (database.containsKey(id)) {
            return database.get(id);
        }
        throw new ResourceNotFoundException("User with id " + id + " not found");
    }

    @GetMapping( path = "/search/{username}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> searchUser(@PathVariable String username) {
        return database.values().stream()
                .filter(user -> user.username().equals(username))
                .collect(Collectors.toList());
    }

    @GetMapping( path = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> allUsers() {
        return new ArrayList<>(database.values());
    }

    @PostMapping( path = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    public User updateUser(@RequestBody String body) throws JsonProcessingException {
        var user = objectMapper.readValue(body, User.class);
        if (user.id == null) {
            throw new BadRequest("User id must be provided");
        }
        if (database.containsKey(user.id)) {
            if (user.username != null) {
                if (searchUserInDB(user.username) != null) {
                    throw new ResourceAlreadyPresent("User with username " + user.username + " already exists");
                }
                user = database.get(user.id).withUsername(user.username);
            }
            database.put(user.id, user);
            return user;
        } else {
            throw new ResourceNotFoundException("User with id " + user.id + " not found");
        }
    }

    @DeleteMapping( path = "/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable String id) {
        if (database.containsKey(id)) {
            database.remove(id);
        } else {
            throw new ResourceNotFoundException("User with id " + id + " not found");
        }
    }

    @DeleteMapping( path = "/clear")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAll() {
        database.clear();
    }


    /*
     * Utils
     */

    private User searchUserInDB(String name) {
        return database.values().stream()
                .filter(user -> user.username().equals(name))
                .findFirst()
                .orElse(null);
    }


    /*
     * Resources
     */

    public record User(String id, String username) {
        public User withId(String id) { return new User(id, username); }
        public User withUsername(String username) { return new User(id, username); }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    @ResponseStatus(value = HttpStatus.CONFLICT)
    static class ResourceAlreadyPresent extends RuntimeException {
        public ResourceAlreadyPresent(String message) {
            super(message);
        }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    static class BadRequest extends RuntimeException {
        public BadRequest(String message) {
            super(message);
        }
    }

}
