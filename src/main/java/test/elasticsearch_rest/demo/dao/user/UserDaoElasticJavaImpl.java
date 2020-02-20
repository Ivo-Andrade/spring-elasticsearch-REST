package test.elasticsearch_rest.demo.dao.user;

import java.io.IOException;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;

import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.delete.*;
import org.elasticsearch.action.index.*;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.update.*;
import org.elasticsearch.client.*;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Component;

import test.elasticsearch_rest.demo.exceptions.*;
import test.elasticsearch_rest.demo.model.*;

@Component
@Qualifier("java-final")
public class UserDaoElasticJavaImpl extends UserDao {

    @Autowired
    RestHighLevelClient highLevelClient;

    public void create(User newInstance) {

        String objectJson;

        try {
            objectJson = new ObjectMapper().writeValueAsString(newInstance);
        } catch (JsonProcessingException e) {
            throw new UserDaoInternalException("JsonProcessingException while converting newInstance object to Json string: " + e.getMessage());
        }

        IndexRequest request = new IndexRequest("looplex-users", "_doc", newInstance.getUsername())
                .opType(DocWriteRequest.OpType.CREATE).source(objectJson, XContentType.JSON);

        try {
            highLevelClient.index(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new UserDaoInternalException("IOException while executing Elasticsearch client: " + e.getMessage());
        }

    }

    public List<User> findAll() {

        List<User> results = new ArrayList<User>();

        SearchRequest searchRequest = new SearchRequest().indices("looplex-users")
                .source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));

        SearchResponse searchResponse;

        try {
            searchResponse = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new UserDaoInternalException("IOException while executing Elasticsearch client: " + e.getMessage());
        }

        for (SearchHit hit : searchResponse.getHits().getHits()) {
            try {
                results.add(new ObjectMapper().readValue(hit.getSourceAsString(), User.class));
            } catch (JsonMappingException e) {
                throw new UserDaoInternalException("JsonMappingException while parsing results: " + e.getMessage());
            } catch (JsonProcessingException e) {
                throw new UserDaoInternalException("JsonProcessingException while parsing results: " + e.getMessage());
            }
        }

        return results;

    }

    public User findById(String id) {

        SearchRequest searchRequest = new SearchRequest()
            .indices("looplex-users")
            .source(new SearchSourceBuilder().query(QueryBuilders.matchQuery("_id", id)));

        SearchResponse searchResponse;

        try {
            searchResponse = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new UserDaoInternalException("IOException while executing Elasticsearch client: " + e.getMessage());
        }

        try {
            return new ObjectMapper().readValue(searchResponse.getHits().getHits()[0].getSourceAsString(), User.class);
        } catch (JsonMappingException e) {
            throw new UserDaoInternalException("JsonMappingException while parsing results: " + e.getMessage());
        } catch (JsonProcessingException e) {
            throw new UserDaoInternalException("JsonProcessingException while parsing results: " + e.getMessage());
        }

    }

    public User update(User transientObject) {

        String objectJson;

        try {
            objectJson = new ObjectMapper().writeValueAsString(transientObject);
        } catch (JsonProcessingException e) {
            throw new UserDaoInternalException("JsonProcessingException while converting transientObject object to Json string: " + e.getMessage());
        }

        UpdateRequest updateRequest = new UpdateRequest("looplex-users", "_doc", transientObject.getUsername())
                .doc(objectJson, XContentType.JSON)
                .fetchSource(true);

        UpdateResponse updateResponse;

        try {
            updateResponse = highLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new UserDaoInternalException("IOException while executing Elasticsearch client: " + e.getMessage());
        }

        if (updateResponse.getGetResult().isExists()) {
            try {
                return new ObjectMapper().readValue(updateResponse.getGetResult().sourceAsString(), User.class);
            } catch (JsonMappingException e) {
                throw new UserDaoInternalException("JsonMappingException while parsing results: " + e.getMessage());
            } catch (JsonProcessingException e) {
                throw new UserDaoInternalException("JsonProcessingException while parsing results: " + e.getMessage());
            }
        } else throw new UserNotFoundException();

    }

    public void delete(User persistentObject) {
        DeleteRequest deleteRequest = new DeleteRequest("looplex-users", "_doc", persistentObject.getUsername());

        try {
            highLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new UserDaoInternalException("IOException while executing Elasticsearch client: " + e.getMessage());
        }
    }
    
}