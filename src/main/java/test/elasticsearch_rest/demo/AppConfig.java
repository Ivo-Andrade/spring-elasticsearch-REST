package test.elasticsearch_rest.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

// import java.time.Duration;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
// import org.springframework.http.HttpHeaders;

@Configuration
@ComponentScan
public class AppConfig {

    @Value("localhost:9200")
    protected String hostPrimary;
    
    @Value("localhost:9201")
    protected String hostSecondary;
    
    @Bean
    RestHighLevelClient elasticsearchClient() {

        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
        .connectedTo(
            hostPrimary
            , hostSecondary // if needed
        )
        .build();

        return RestClients.create(clientConfiguration).rest();    

    // From Spring Data Elasticsearch documentation
    // https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#reference

    // Define default headers if they need to be customized
        // HttpHeaders defaultHeaders = new HttpHeaders();
        // defaultHeaders.setBasicAuth(USER_NAME, USER_PASS);                      

    // Additional configurations for HighLevelClient
        // ClientConfiguration clientConfiguration = ClientConfiguration.builder()
        //     .connectedTo("localhost:9200", "localhost:9291")                      
        //     .withConnectTimeout(Duration.ofSeconds(5))                            
        //     .withSocketTimeout(Duration.ofSeconds(3))                             
        //     .useSsl()                                                             
        //     .withDefaultHeaders(defaultHeaders)                                   
        //     .withBasicAuth(username, password)                                    
        //     // ... other options
        //     .build();              

    }

}