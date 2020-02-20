package test.elasticsearch_rest.demo.dao.user;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import test.elasticsearch_rest.demo.exceptions.*;
import test.elasticsearch_rest.demo.model.ElasticsearchUpdatePayload;
import test.elasticsearch_rest.demo.model.User;

@Component
@Qualifier("http-final")
public class UserDaoElasticHTTPImpl extends UserDao {

    RestTemplate restTemplate = new RestTemplate();

    public void create(User newInstance) {

        // TODO: Solve repeated header setup
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // TODO: Encapsulate payload builder
        String payload;
        try {
            payload = new ObjectMapper().writeValueAsString(newInstance);
        } catch (JsonProcessingException e) {
            throw new UserDaoInternalException(
                    "JsonProcessingException while converting newInstance object to Json string: " + e.getMessage());
        }

        // TODO: Encapsulate HTTP Request mechanism -AND/OR- Create better error handling
        try {
            restTemplate.exchange(
                "http://localhost:9200/looplex-users/_create/" + newInstance.getUsername()
                , HttpMethod.POST
                , new HttpEntity<String>(payload, headers)
                , String.class
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.CONFLICT))
                throw new UserDaoConflictException("User already exists in database!");
            else
                throw new UserDaoInternalException(
                        "Unexpected HttpClientErrorException: " + e.getStatusCode().toString() + ", " + e.getMessage());
        } catch (Exception e) {
            throw new UserDaoInternalException(
                    "Unexpected Exception while performing Elasticsearch HTTP request: " + e.toString());
        }

    }

    @Override
    public List<User> findAll() {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<JsonNode> requestResponse;
        try {
            requestResponse = restTemplate.exchange(
                "http://localhost:9200/looplex-users/_search"
                , HttpMethod.GET
                , new HttpEntity<String>(null, headers)
                , JsonNode.class
            );
        } catch (HttpClientErrorException e) {
            throw new UserDaoInternalException(
                    "Unexpected HttpClientErrorException: " + e.getStatusCode().toString() + ", " + e.getMessage());
        } catch (Exception e) {
            throw new UserDaoInternalException(
                    "Unexpected Exception while performing Elasticsearch HTTP request: " + e.toString());
        }

        List<User> userList = new ArrayList<User>();
        for (JsonNode hit : requestResponse.getBody().get("hits").get("hits")) {
            try {
                userList.add(
                    new ObjectMapper().readValue(
                        hit.get("_source").toString()
                        , User.class
                    )
                );
            } catch (JsonMappingException e) {
                throw new UserDaoInternalException(
                        "Unexpected JsonMappingException while building response: " + e.toString());
            } catch (JsonProcessingException e) {
                throw new UserDaoInternalException(
                        "Unexpected JsonProcessingException while building response: " + e.toString());
            }
        }
        return userList;

    }

    @Override
    public User findById(String id) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<JsonNode> requestResponse;
        try {
            requestResponse = restTemplate.exchange(
                "http://localhost:9200/looplex-users/_search?q=username:" + id
                , HttpMethod.GET
                , new HttpEntity<String>(null, headers)
                , JsonNode.class
            );
        } catch (HttpClientErrorException e) {
            throw new UserDaoInternalException(
                    "Unexpected HttpClientErrorException: " + e.getStatusCode().toString() + ", " + e.getMessage());
        } catch (Exception e) {
            throw new UserDaoInternalException(
                    "Unexpected Exception while performing Elasticsearch HTTP request: " + e.toString());
        }

        try {
            return new ObjectMapper().readValue(
                requestResponse.getBody().get("hits").get("hits").get(0).get("_source").toString()
                , User.class
            );
        } catch (JsonProcessingException e) {
            throw new UserDaoInternalException("JsonProcessingException while converting result json to result object: " + e.getMessage());
        }

    }

    public User update(User transientObject) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String payload;
        try {
            payload = new ObjectMapper().writeValueAsString(
                new ElasticsearchUpdatePayload(transientObject)
            );
        } catch (JsonProcessingException e) {
            throw new UserDaoInternalException(
                    "JsonProcessingException while converting newInstance object to Json string: " + e.getMessage());
        }

        ResponseEntity<JsonNode> requestResponse;
        try {
            requestResponse = restTemplate.exchange(
                "http://localhost:9200/looplex-users/_update/" + transientObject.getUsername() + "?_source"
                , HttpMethod.POST
                , new HttpEntity<String>(payload, headers)
                , JsonNode.class
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new UserNotFoundException("User not found!");
            throw new UserDaoInternalException(
                    "Unexpected HttpClientErrorException: " + e.getStatusCode().toString() + ", " + e.toString());
        } catch (Exception e) {
            throw new UserDaoInternalException(
                    "Unexpected Exception while performing Elasticsearch HTTP request: " + e.toString());
        }

        try {
            return new ObjectMapper().readValue(
                requestResponse.getBody().get("get").get("_source").toString(), User.class
            );
        } catch (JsonProcessingException e) {
            throw new UserDaoInternalException("JsonProcessingException while converting newInstance object to Json string: " + e.getMessage());
        }

    }

    @Override
    public void delete(User persistentObject) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            restTemplate.exchange(
                "http://localhost:9200/looplex-users/_doc/" + persistentObject.getUsername()
                , HttpMethod.DELETE
                , new HttpEntity<String>(null, headers)
                , JsonNode.class
            );
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.NOT_FOUND))
                throw new UserNotFoundException("User not found!");
            throw new UserDaoInternalException(
                    "Unexpected HttpClientErrorException: " + e.getStatusCode().toString() + ", " + e.toString());
        } catch (Exception e) {
            throw new UserDaoInternalException(
                    "Unexpected Exception while performing Elasticsearch HTTP request: " + e.toString());
        }

    }

}