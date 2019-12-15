/*
 * Copyright: 2019 dingxiang-inc.com Inc. All rights reserved.
 */

package dev.research.base.controller;

import dev.research.base.config.ElasticSearchProperties;
import org.apache.commons.codec.Charsets;
import org.apache.tomcat.util.security.MD5Encoder;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @FileName: BookController.java
 * @Description: BookController.java类说明
 * @Author: by shimin.huang
 * @Date: 2019-12-07 19:04
 */
@RequestMapping("/book")
@RestController
public class BookController {
    public static void main(String[] args) {
        byte[] bytes = DigestUtils.md5Digest("123*#Ui.524adminDX-inc".getBytes(Charsets.UTF_8));
        String s = new String(bytes, Charsets.UTF_8);
        System.out.println(s);
    }

    @Resource(name = "highLevelClient")
    private RestHighLevelClient client;
    @Autowired
    private ElasticSearchProperties elasticSearchProperties;

    @GetMapping("/info")
    public ResponseEntity<Object> get(String name) {
        SearchRequest request = new SearchRequest(elasticSearchProperties.getIndex());
        MatchQueryBuilder query = QueryBuilders.matchQuery("name", name);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(query);
        request.source(sourceBuilder);
        SearchResponse sr;
        try {
            sr = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("查询异常");
        }
        return ResponseEntity.of(Optional.of(sr));
    }

    @PostMapping("/save")
    public ResponseEntity<Object> save(String name, Integer age) {
        IndexRequest indexRequest = new IndexRequest(elasticSearchProperties.getIndex());
        Map<String, Object> data = new HashMap<>(4);
        data.put("name", name);
        data.put("age", age);
        indexRequest.source(data);
        IndexResponse ir;
        try {
            ir = client.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("添加异常");
        }
        return ResponseEntity.of(Optional.of(ir));
    }

    @PutMapping("/edit")
    public ResponseEntity<Object> edit(String id, String name, Integer age) {
        UpdateRequest request = new UpdateRequest(elasticSearchProperties.getIndex(), id);
        Map<String, Object> data = new HashMap<>(4);
        data.put("name", name);
        data.put("age", age);
        request.doc(data);
        UpdateResponse ur;
        try {
            ur = client.update(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("添加异常");
        }
        return ResponseEntity.of(Optional.of(ur));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Object> delete(String id) {
        DeleteRequest request = new DeleteRequest(elasticSearchProperties.getIndex(), id);
        //使用乐观锁
        request.version(1);
        DeleteResponse dr;
        try {
            dr = client.delete(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("添加异常");
        }
        return ResponseEntity.of(Optional.of(dr));
    }

    @GetMapping(value = "/mutil")
    public ResponseEntity<Object> mutilQuery() {
        SearchRequest request = new SearchRequest(elasticSearchProperties.getIndex());
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("name", "王媛"));
        SearchSourceBuilder query = SearchSourceBuilder.searchSource()
                .query(boolQueryBuilder);
        request.source(query);
        SearchResponse sr;
        try {
            sr = client.search(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("添加异常");
        }
        return ResponseEntity.of(Optional.of(sr));
    }
}
