# Elasticsearch
- Enquanto modelos de banco de dados relacionais, como o SQL, criam estruturas relacionais de dados (múltiplas tabelas representativas de entidades definidas), o Elasticsearch visa criar tabelas de índices as quais armazenam informações completas de uma entidade em modelo maleável.
- Ou seja, o Elasticsearch utiliza objetos JSON ao invés de informações em tabela, as quais permitem um modelo expansível e autodidata d e armazenamento de dados.
- Um _cluster_ de Elasticsearch contém diversos _nodes_, ao quais os documentos de um banco são distribuído e colocados em pronto acesso por meio de quaisquer _nodes_.
- Um documento armazenado contém uma indexação que permite uma busca em tempo real por meio de um index invertido, que lista toda palavra única do documento e identifica todas suas ocorrências em todos os documentos.

## Métodos REST HTTP

### POST _create, _doc
- `POST /<index>/_doc`
- `PUT /<index>/_doc/<_id?>`
- `POST /<index>/_create/<_id?>`
- `PUT /<index>/_create/<_id?>`
- _Query Parameters (?parameter:key&param2:key2)_:
    - `op_type`: `create` para criar um novo documento (mesmo que `_create`)
    - `version`: Define a versão do documento para qual a operação é válida
    - `if_seq_no`, `if_primary_term`
    - `routing`
    - `refresh`: Modo de atualização das shards (`true`, `wait_for` ou `false`)
    - `master_timeout`, `timeout`
- _Request body_:
    - Documento JSON a ser salvo
- _Response body_:
    - `_shards`: Informação sobre as shards criadas
    - `_index`, `_type`, `_id`, `_version`
    - `_seq_no`: Visa evitar que uma versão antiga do documento sobre-escreva uma mais nova.
    - `_primary_term`
    - `result`: Resultado da operação (`created` ou `updated`)

### GET / HEAD _source, _fields
- `GET <index>/_doc/<_id>`: Recolhe um documento completo
- `HEAD <index>/_doc/<_id>`: Verifica se documento existe
- `GET <index>/_source/<_id>`: Recolhe apenas o document source
- `HEAD <index>/_source/<_id>`: Verifica se document source existe
- _Query Parameters (?parameter:key&param2:key2)_:
    - `preference`: Define que shard utilizar
    - `realtime`: Define se a requisição trabalha em _realtime_ ou não (se há a análise do source)
    - `refresh`: Modo de atualização das shards (`true`, `wait_for` ou `false`)
    - `routing`: Determina um shard alvo
    - `stored_fields`: Define a devolução dos campos do index ou do seu source
    - `_source`, `_source_excludes`, `_source_includes`: Define como um source é recolhido
    - `version`
- _Response Body_:
    - `_index`, `_type`, `_id`, `_version`
    - `_seq_no`, `_primary_term`
    - `found`: Indica se documento existe
    - `_source`, `_fields`

### POST _update
- `POST /<index>/_update/<_id>`
- _Query Parameters (?parameter:key&param2:key2)_:
    - `if_seq_no`, `if_primary_term`
    - `refresh`: Modo de atualização das shards (`true`, `wait_for` ou `false`)
    - `retry_on_conflict`
    - `routing`
    - `_source`, `_source_excludes`, `_source_includes`
    - `master_timeout`, `timeout`
    - `wait_for_active_shards`: Define o número de shards ativas para realizar a operação
- _Request Body_:
    - `script`: Define um script para realizar um update
        - `lang`
        - `source`
        - `params`
    - `doc`: Define um documento integral ou parcial para atualizar o documento
        - `doc_as_upsert`
    - `upsert`: Inclui um documento padrão caso este não exista
        - `scripted_upsert`
- _Response Body_:
    - `result`: `noop` caso não haja alterações

### POST _update_by_query
- `POST /<index>/_update_by_query`
- _Query Parameters (?parameter:key&param2:key2)_:
    - `scroll_size`
- _Response Body_:
    - `took`, `timed_out`
    - `total`, `updated`, `deleted`
    - `version_conflicts`, `noops`
    - `retries`
    - `failures`
    
### DELETE
- `DELETE /<index>/_doc/<_id>`
- _Query Parameters_:
    - `if_seq_no`, `if_primary_term`
    - `refresh`: Modo de atualização das shards (`true`, `wait_for` ou `false`)
    - `routing`
    - `timeout`
    - `version`, `version_type`
    
### POST _delete_by_query
- `POST /<index>/_delete_by_query`
- _Query Parameters (?parameter:key&param2:key2)_:
    - `scroll_size`
    
#### Query Object
- `query`
    - `bool`
        - `must`, `should`, `must_not`
    - `term`
    - `range`
    - `match`
        - `fuzziness`
    - `match_all`
    - `size`, `sort`, `from`
    - `filter`
    - `_source`, `highlight`
    - `wildcard`
    - `query_string`, `simple_query_string`
    
# Referências
- https://www.elastic.co
- https://www.elastic.co/guide/en/elasticsearch/reference/current/docs.html (APÌ Implementation)
- https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/#reference (SSL Configuration)
- https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-document-index.html (High Level Client)
- https://dzone.com/articles/23-useful-elasticsearch-example-queries (Query Object)
- https://okfnlabs.org/blog/2013/07/01/elasticsearch-query-tutorial.html
