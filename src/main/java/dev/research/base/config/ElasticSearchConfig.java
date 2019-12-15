/*
 * Copyright: 2019 dingxiang-inc.com Inc. All rights reserved.
 */

package dev.research.base.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @FileName: ElasticSearchConfig.java
 * @Description: ElasticSearchConfig.java类说明
 * @Author: by shimin.huang
 * @Date: 2019-12-07 18:23
 */
@Configuration
public class ElasticSearchConfig {

    @Autowired
    private ElasticSearchProperties elasticsearchProperties;


    @Bean
    public RestClientBuilder restClientBuilder() {
        return buildHttpPorts();
    }


    @Bean(name = "highLevelClient")
    public RestHighLevelClient highLevelClient(@Autowired RestClientBuilder restClientBuilder) {
        return new RestHighLevelClient(restClientBuilder);
    }


    private RestClientBuilder buildHttpPorts() {
        List<String> hostList = elasticsearchProperties.getHostList();
        if (CollectionUtils.isEmpty(hostList)) {
            throw new RuntimeException("es配置异常");
        }
        List<HttpHost> httpHostList = new ArrayList<>(2);
        hostList.forEach(host -> {
            HttpHost httpHost = HttpHost.create(host);
            httpHostList.add(httpHost);
        });
        return RestClient.builder(httpHostList.toArray(new HttpHost[2]));
    }
}
