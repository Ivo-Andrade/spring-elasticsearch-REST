package test.elasticsearch_rest.demo.dao.user;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import test.elasticsearch_rest.demo.exceptions.UserDaoConflictException;
import test.elasticsearch_rest.demo.exceptions.UserDaoInternalException;
import test.elasticsearch_rest.demo.model.User;

@Component
@Qualifier("http-study")
public class _STUDY_UserDaoElasticHTTPImpl extends UserDao {

    RestTemplate restTemplate = new RestTemplate();

    public void create(User newInstance) {

        String postRequestURL = "http://localhost:9200/looplex-users/_create/" + newInstance.getUsername();

        String objectJson;

        try {
            objectJson = new ObjectMapper().writeValueAsString(newInstance);
        } catch (JsonProcessingException e) {
            throw new UserDaoInternalException(
                    "JsonProcessingException while converting newInstance object to Json string: " + e.getMessage());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(objectJson, headers);

        String result; 

        try {
            result = restTemplate.exchange(postRequestURL, HttpMethod.POST, request, String.class).getBody();
        } catch (HttpClientErrorException e) {
            if( e.getStatusCode().equals(HttpStatus.CONFLICT)) throw new UserDaoConflictException(
                "User already exists in databate");
            else throw new UserDaoInternalException(
                    "HttpClientErrorException while performing Elasticsearch HTTP request: " + e.getMessage());
        }

        JsonNode resultJson;

        try {
            resultJson = new ObjectMapper().readTree(result);
        } catch (JsonProcessingException e) {
            throw new UserDaoInternalException("JsonProcessingException while converting newInstance object to Json string: " + e.getMessage());
        }

        if( resultJson.has("error") ) {
            throw new UserDaoInternalException("An error has occured with the request: " + resultJson.get("error").get("type").toString() );
        }

    }

    @Override
    public List<User> findAll() {

        String getRequestURL = "http://localhost:9200/looplex-users/_search";

        String result = restTemplate.getForObject(getRequestURL, String.class);

        JsonNode resultJson;

        try {
            resultJson = new ObjectMapper().readTree(result);
        } catch (JsonProcessingException e) {
            throw new UserDaoInternalException("JsonProcessingException while converting newInstance object to Json string: " + e.getMessage());
        }

        if( resultJson.has("error") ) {
            throw new UserDaoInternalException("An error has occured with the request: " + resultJson.get("error").get("type").toString() );
        }
        
        else if( resultJson.has("hits") ) {
            try {
                List<User> userList = new ArrayList<User>();
                for(JsonNode hit : resultJson.get("hits").get("hits")){
                    userList.add(new ObjectMapper().readValue(hit.get("_source").toString(), User.class));
                }
                return userList;
            } catch (JsonProcessingException e) {
                throw new UserDaoInternalException("JsonProcessingException while converting result string to response object: " + e.getMessage());
            }
        }

        else throw new UserDaoInternalException("An unexpected response happened: " + resultJson.toString());

    }

    @Override
    public User findById(String id) {

        String getRequestURL = "http://localhost:9200/looplex-users/_search?q=username:" + id;

        String result = restTemplate.getForObject(getRequestURL, String.class);

        JsonNode resultJson;

        try {
            resultJson = new ObjectMapper().readTree(result);
        } catch (JsonProcessingException e) {
            throw new UserDaoInternalException("JsonProcessingException while converting newInstance object to Json string: " + e.getMessage());
        }

        if( resultJson.has("error") ) {
            throw new UserDaoInternalException("An error has occured with the request: " + resultJson.get("error").get("type").toString() );
        }
        
        else if( resultJson.has("hits") ) {
            try {
                return new ObjectMapper().readValue(resultJson.get("hits").get("hits").get(0).get("_source").toString(), User.class);
            } catch (JsonProcessingException e) {
                throw new UserDaoInternalException("JsonProcessingException while converting result json to result object: " + e.getMessage());
            }
        }

        else throw new UserDaoInternalException("An unexpected response happened: " + resultJson.toString());

    }

    public User update(User transientObject) {

        String putRequestURL = "http://localhost:9200/looplex-users/_update/" + transientObject.getUsername() + "?_source";

        String objectJson;

        try {
            objectJson = "{\"doc\":" + new ObjectMapper().writeValueAsString(transientObject) + "}";
        } catch (JsonProcessingException e) {
            throw new UserDaoInternalException("JsonProcessingException while converting newInstance object to Json string: " + e.getMessage());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(objectJson, headers);

        String result; 

        try {
            result = restTemplate.exchange(putRequestURL, HttpMethod.POST, request, String.class).getBody();
        } catch (HttpClientErrorException e) {
            throw new UserDaoInternalException(
                "HttpClientErrorException while performing Elasticsearch HTTP request: " + e.getMessage());
        }

        JsonNode resultJson;

        try {
            resultJson = new ObjectMapper().readTree(result);
        } catch (JsonProcessingException e) {
            throw new UserDaoInternalException("JsonProcessingException while converting newInstance object to Json string: " + e.getMessage());
        }

        if( resultJson.has("error") ) {
            throw new UserDaoInternalException("An error has occured with the request: " + resultJson.get("error").get("type").toString() );
        }
        
        else if( resultJson.has("get") ) {
            try {
                return new ObjectMapper().readValue(resultJson.get("get").get("_source").toString(), User.class);
            } catch (JsonProcessingException e) {
                throw new UserDaoInternalException("JsonProcessingException while converting newInstance object to Json string: " + e.getMessage());
            }
        }

        else throw new UserDaoInternalException("An unexpected response happened: " + resultJson.toString());

    }

    @Override
    public void delete(User persistentObject) {
        String deleteRequestURL = "http://localhost:9200/looplex-users/_doc/" + persistentObject.getUsername();

        String result;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>("", headers);

        if( findById(persistentObject.getUsername()) != null) {

            if(persistentObject.equals(findById(persistentObject.getUsername()))) {

                try {
                    result = restTemplate.exchange(deleteRequestURL, HttpMethod.DELETE, request, String.class).getBody();
                } catch (HttpClientErrorException e) {
                    throw new UserDaoInternalException(
                        "HttpClientErrorException while performing Elasticsearch HTTP request: " + e.getMessage());
                }

            }  else throw new UserDaoInternalException("Provided object doesn't match document.");

        } else throw new UserDaoInternalException("Object not found in database");

        JsonNode resultJson;

        try {
            resultJson = new ObjectMapper().readTree(result);
        } catch (JsonProcessingException e) {
            throw new UserDaoInternalException("JsonProcessingException while converting newInstance object to Json string: " + e.getMessage());
        }
        
        if( resultJson.has("result") ) {
            if( resultJson.get("result").asText().equals("deleted") ) return;
            else throw new UserDaoInternalException("Unexpected result: " + resultJson.get("result").toString());
        }

        else throw new UserDaoInternalException("An unexpected response happened: " + resultJson.toString());

    }

}