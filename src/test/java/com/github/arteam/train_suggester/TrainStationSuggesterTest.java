package com.github.arteam.train_suggester;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.elasticsearch.ElasticsearchContainer;

public class TrainStationSuggesterTest {

    private static final String PASSWORD = "s3cret";
    private static final ElasticsearchContainer ELASTICSEARCH =
            new ElasticsearchContainer("docker.elastic.co/elasticsearch/elasticsearch:8.1.2")
                    .withExposedPorts(9200)
                    .withPassword(PASSWORD);

    @BeforeAll
    static void beforeAll() {
        ELASTICSEARCH.start();
    }

    @AfterEach
    void tearDown() {
        ELASTICSEARCH.stop();
    }

    @Test
    void test() throws Exception {
        var credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("elastic", PASSWORD));

        RestClient client = RestClient.builder(HttpHost.create(ELASTICSEARCH.getHttpHostAddress()))
                .setHttpClientConfigCallback(builder -> builder.setDefaultCredentialsProvider(credentialsProvider))
                .build();

        Response response = client.performRequest(new Request("GET", "/_cluster/health"));
        System.out.println(response);
    }
}
