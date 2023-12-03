package com.elk.config;

import co.elastic.clients.elasticsearch.ElasticsearchAsyncClient;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.elk.constants.GlobalConstants;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;

@ApplicationScoped
public class ElasticConfig {

    @Singleton
    @Produces
    public ElasticsearchClient elasticsearchClient() {
        RestClient restClient = getRestClient();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }

   @Singleton
    @Produces
    public ElasticsearchAsyncClient elasticsearchAsyncClient() {
       RestClient restClient = getRestClient();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        return new ElasticsearchAsyncClient(transport);
    }

    @Singleton
    public  RestClient getRestClient() {
        return RestClient.builder(HttpHost.create(GlobalConstants.ELK_SERVER_URL))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization", "ApiKey " + GlobalConstants.ELK_SERVER_API_KEY)
                })
                .build();
    }
}
