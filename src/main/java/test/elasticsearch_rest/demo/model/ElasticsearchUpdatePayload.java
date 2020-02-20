package test.elasticsearch_rest.demo.model;

import javax.validation.Valid;

import lombok.Data;

@Data
public class ElasticsearchUpdatePayload {

    @Valid
    User doc;

    public ElasticsearchUpdatePayload(
        User doc
    ) {
        this.doc = doc;
    }

}