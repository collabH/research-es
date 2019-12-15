/*
 * Copyright: 2019 dingxiang-inc.com Inc. All rights reserved.
 */

package dev.research.base.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @FileName: ElasticSearchProperties.java
 * @Description: ElasticSearchProperties.java类说明
 * @Author: by shimin.huang
 * @Date: 2019-12-07 18:43
 */
@Component
@ConfigurationProperties(prefix = "es.config")
@Data
public class ElasticSearchProperties {
    private List<String> hostList;
    private String index;
    private String type;
}
