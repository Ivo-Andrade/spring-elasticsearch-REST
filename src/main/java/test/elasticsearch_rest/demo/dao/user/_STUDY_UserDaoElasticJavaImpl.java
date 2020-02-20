package test.elasticsearch_rest.demo.dao.user;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import test.elasticsearch_rest.demo.exceptions.UserDaoInternalException;
import test.elasticsearch_rest.demo.exceptions.UserNotFoundException;
import test.elasticsearch_rest.demo.model.User;

@Component
@Qualifier("java-study")
public class _STUDY_UserDaoElasticJavaImpl extends UserDao {

    @Autowired
    RestHighLevelClient highLevelClient;

    public void create(User newInstance) {
        String objectJson;
        try {
            objectJson = new ObjectMapper().writeValueAsString(newInstance);
        } catch (Exception e) {
            // JsonProcessingException
            throw new UserDaoInternalException("An error occured while searching for an user: " + e.getMessage());
        }

        IndexRequest request = new IndexRequest("looplex-users", "_doc", newInstance.getUsername())
                .opType(DocWriteRequest.OpType.CREATE)
                .source(objectJson, XContentType.JSON);

        try {
            // IndexResponse response =
            highLevelClient.index(request, RequestOptions.DEFAULT);
            // } catch (ElasticsearchException e) {
            // if (e.status() == RestStatus.CONFLICT) {
            // throw new UserDaoInternalException("The user with the provided id already exists: " +
            // e.getMessage());
            // } else
            // throw new UserDaoInternalException("An error occured while creating a new user: " +
            // e.getMessage());
            // }
        } catch (Exception e) {
            // IOException
            throw new UserDaoInternalException("An error occured while creating a new user: " + e.getMessage());
        }

        // From Elasticsearch Java REST Client Documentation
        // https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-document-index.html

        // String index = response.getIndex();
        // String id = response.getId();
        // if (response.getResult() == DocWriteResponse.Result.CREATED) {
        // // handles (whenever needed) a created document
        // } else if (response.getResult() == DocWriteResponse.Result.UPDATED) {
        // // handles (whenever needed) an updated document
        // }

        // ReplicationResponse.ShardInfo shardInfo = response.getShardInfo();
        // if (shardInfo.getTotal() != shardInfo.getSuccessful()) {
        // // handles if the number of sucessful shards is less than total shards
        // }
        // if (shardInfo.getFailed() > 0) {
        // for (ReplicationResponse.ShardInfo.Failure failure :
        // shardInfo.getFailures()) {
        // String reason = failure.reason();
        // // handles failures
        // }
        // }

    }

    public List<User> findAll() {
        SearchRequest searchRequest = new SearchRequest().indices("looplex-users")
                .source(new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()));

        SearchResponse searchResponse;

        try {
            searchResponse = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            // IOException
            throw new UserDaoInternalException("An error occured while listing all users: " + e.getMessage());
        }

        // From Elasticsearch Java REST Client Documentation
        // https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-search.html

        // RestStatus status = searchResponse.status();
        // TimeValue took = searchResponse.getTook();
        // Boolean terminatedEarly = searchResponse.isTerminatedEarly();
        // boolean timedOut = searchResponse.isTimedOut();

        List<User> results = new ArrayList<User>();

        for (SearchHit hit : searchResponse.getHits().getHits()) {
            try {
                results.add(new ObjectMapper().readValue(hit.getSourceAsString(), User.class));
            } catch (Exception e) {
                // JsonProcessingException, JsonMappingException
                throw new UserDaoInternalException("An error occured while listing all users: " + e.getMessage());
            }
        }

        // SearchHits hits = searchResponse.getHits();
        // TotalHits totalHits = hits.getTotalHits();
        // long numHits = totalHits.value;
        // // whether the number of hits is accurate (EQUAL_TO) or a lower bound of the
        // total (GREATER_THAN_OR_EQUAL_TO)
        // TotalHits.Relation relation = totalHits.relation;
        // float maxScore = hits.getMaxScore();

        return results;
    }

    public User findById(String id) {
        SearchRequest searchRequest = new SearchRequest()
            .indices("looplex-users")
            .source(new SearchSourceBuilder().query(QueryBuilders.matchQuery("_id", id)));

        SearchResponse searchResponse;

        try {
            searchResponse = highLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            // IOException, JsonProcessingException, JsonMappingException
            throw new UserDaoInternalException("An error occured while searching for an user: " + e.getMessage());
        }

        try {
            System.out.println(searchResponse.getHits().getHits().length);
            return new ObjectMapper().readValue(searchResponse.getHits().getHits()[0].getSourceAsString(), User.class);
        } catch (Exception e) {
            // IOException, JsonProcessingException, JsonMappingException
            throw new UserDaoInternalException("An error occured while searching for an user: " + e.getMessage());
        }
    }

    public User update(User transientObject) {
        String objectJson;
        try {
            objectJson = new ObjectMapper().writeValueAsString(transientObject);
        } catch (Exception e) {
            // JsonProcessingException
            throw new UserDaoInternalException("An error occured while updating an user: " + e.getMessage());
        }

        UpdateRequest updateRequest = new UpdateRequest("looplex-users", "_doc", transientObject.getUsername())
                .doc(objectJson, XContentType.JSON)
                .fetchSource(true);
        // .doc() also accepts partial objects, Maps, XContent Builders, and Object
        // key-pairs!
        // .upsert(jsonString, XContentType.JSON) to create when non-existant

        // From Elasticsearch Java REST Client Documentation
        // https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-document-update.html

        // inline script method

        // Map<String, Object> parameters = singletonMap("count", 4);
        // Script inline = new Script(ScriptType.INLINE, "painless",
        // "ctx._source.field += params.count", parameters);
        // request.script(inline);

        // stored script method

        // Script stored = new Script(
        // ScriptType.STORED, null, "increment-field", parameters);
        // request.script(stored);

        UpdateResponse updateResponse;

        try {
            updateResponse = highLevelClient.update(updateRequest, RequestOptions.DEFAULT);
            // } catch (ElasticsearchException e) {
            // if (e.status() == RestStatus.NOT_FOUND) {
            // // handles if document was not found
            // }
        } catch (Exception e) {
            // IOException
            throw new UserDaoInternalException("An error occured while updating an user: " + e.getMessage());
        }

        // String index = updateResponse.getIndex();
        // String id = updateResponse.getId();
        // long version = updateResponse.getVersion();
        // if (updateResponse.getResult() == DocWriteResponse.Result.CREATED) {
        // // handles if created
        // } else if (updateResponse.getResult() == DocWriteResponse.Result.UPDATED) {
        // // handles if udpated
        // } else if (updateResponse.getResult() == DocWriteResponse.Result.DELETED) {
        // // handles if deleted
        // } else if (updateResponse.getResult() == DocWriteResponse.Result.NOOP) {
        // // handles if no operation was done to document
        // }
        
        System.out.println(updateResponse.toString());

        if (updateResponse.getGetResult().isExists()) {
            try {
                return new ObjectMapper().readValue(updateResponse.getGetResult().sourceAsString(), User.class);
            } catch (Exception e) {
                // JsonProcessingException, JsonMappingException
                throw new UserDaoInternalException("An error occured while updating an user: " + e.getMessage());
            }
        } else
            throw new UserNotFoundException();

    }

    public void delete(User persistentObject) {
        DeleteRequest deleteRequest = new DeleteRequest("looplex-users", "_doc", persistentObject.getUsername());

        try {
            highLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (Exception e) {
            // IOException
            throw new UserDaoInternalException("An error occured while deleting an user: " + e.getMessage());
        }
    }
    
}