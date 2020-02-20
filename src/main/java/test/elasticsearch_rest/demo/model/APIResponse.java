package test.elasticsearch_rest.demo.model;

import lombok.Data;

@Data
public class APIResponse {

    private String message;

    public APIResponse(
        String message
    ) {
        this.message = message;
    }
    
}