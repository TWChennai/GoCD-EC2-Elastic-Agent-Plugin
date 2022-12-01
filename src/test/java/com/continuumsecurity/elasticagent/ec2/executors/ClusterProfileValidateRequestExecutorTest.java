/*
 * Copyright 2019 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file incorporates changes by @continuumsecurity
 */

package com.continuumsecurity.elasticagent.ec2.executors;

import com.continuumsecurity.elasticagent.ec2.requests.ClusterProfileValidateRequest;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClusterProfileValidateRequestExecutorTest {
    @Test
    public void shouldBarfWhenUnknownKeysArePassed() throws Exception {
        ClusterProfileValidateRequestExecutor executor = new ClusterProfileValidateRequestExecutor(new ClusterProfileValidateRequest(Collections.singletonMap("foo", "bar")));
        String json = executor.execute().responseBody();
        String expectedStr = "[" +
                "{\"message\":\"Go Server URL must not be blank.\",\"key\":\"go_server_url\"}," +
                "{\"message\":\"auto_register_timeout must be a positive integer.\",\"key\":\"auto_register_timeout\"}," +
                "{\"message\":\"max_elastic_agents must be a positive integer.\",\"key\":\"max_elastic_agents\"}," +
                "{\"message\":\"aws_region must not be blank.\",\"key\":\"aws_region\"}," +
                "{\"key\":\"foo\",\"message\":\"Is an unknown property\"}" +
                "]";
        JSONAssert.assertEquals(expectedStr, json, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldValidateMandatoryKeys() throws Exception {
        ClusterProfileValidateRequestExecutor executor = new ClusterProfileValidateRequestExecutor(new ClusterProfileValidateRequest(Collections.<String, String>emptyMap()));
        String json = executor.execute().responseBody();

        String expectedStr = "[" +
                "{\"message\":\"Go Server URL must not be blank.\",\"key\":\"go_server_url\"}," +
                "{\"message\":\"auto_register_timeout must be a positive integer.\",\"key\":\"auto_register_timeout\"}," +
                "{\"message\":\"max_elastic_agents must be a positive integer.\",\"key\":\"max_elastic_agents\"}," +
                "{\"message\":\"aws_region must not be blank.\",\"key\":\"aws_region\"}" +
                "]\n";

        JSONAssert.assertEquals(expectedStr, json, JSONCompareMode.NON_EXTENSIBLE);
    }

    @Test
    public void shouldValidateUrlFields() throws Exception {
        Map<String, String> properties = new HashMap<>();
        properties.put("go_server_url", "https://go-server.com");
        properties.put("aws_endpoint_url", "vpce-some-endpoint.com");
        properties.put("auto_register_timeout", "10");
        properties.put("max_elastic_agents", "3");
        properties.put("aws_region", "ap-south-1");
        ClusterProfileValidateRequest request = new ClusterProfileValidateRequest(properties);
        ClusterProfileValidateRequestExecutor executor = new ClusterProfileValidateRequestExecutor(request);
        String json = executor.execute().responseBody();

        String expectedStr = "[" +
                "{\"message\":\"Go Server URL must be a valid URL ending with '/go' (https://example.com:8154/go)\",\"key\":\"go_server_url\"}," +
                "{\"message\":\"Endpoint URL must be a valid URL (https://<vpc-endpoint-id>.ec2.<aws-region>.vpce.amazonaws.com)\",\"key\":\"aws_endpoint_url\"}" +
                "]\n";

        JSONAssert.assertEquals(expectedStr, json, JSONCompareMode.NON_EXTENSIBLE);
    }
}
