package com.continuumsecurity.elasticagent.ec2.executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;

class AWSEndpointURLMetadataTest {

    private AWSEndpointURLMetadata awsEndpointURLMetadata;

    @BeforeEach
    void setUp() {
        awsEndpointURLMetadata = new AWSEndpointURLMetadata("aws_endpoint_url", false, false);
    }

    @Test
    void shouldNotReturnErrorWhenEndpointUrlIsNull() {
        assertNull(awsEndpointURLMetadata.doValidate(null));
    }

    @Test
    void shouldNotReturnErrorWhenEndpointUrlIsEmpty() {
        assertNull(awsEndpointURLMetadata.doValidate(""));
    }

    @Test
    void shouldReturnErrorWhenTheEndpointUrlIsNotInURIFormat() {
        String errorMessage = awsEndpointURLMetadata.doValidate("some-random-string");

        assertThat(errorMessage, is("Endpoint URL must be a valid URL (https://<vpc-endpoint-id>.ec2.<aws-region>.vpce.amazonaws.com)"));
    }

    @Test
    void shouldReturnErrorWhenTheSchemeIsNotSpecified() {
        String errorMessage = awsEndpointURLMetadata.doValidate("vpce-ewuey-23eqw.ec2.ap-south-1.vpce.amazonaws.com");

        assertThat(errorMessage, is("Endpoint URL must be a valid URL (https://<vpc-endpoint-id>.ec2.<aws-region>.vpce.amazonaws.com)"));
    }

    @Test
    void shouldReturnErrorWhenTheSchemeIsNotHttps() {
        String errorMessage = awsEndpointURLMetadata.doValidate("http://vpce-ewuey-23eqw.ec2.ap-south-1.vpce.amazonaws.com");

        assertThat(errorMessage, is("Endpoint URL must be a valid HTTPs URL (https://<vpc-endpoint-id>.ec2.<aws-region>.vpce.amazonaws.com)"));
    }

    @Test
    void shouldReturnErrorIfTheEndpointIsLocalHostUrl() {
        String errorMessage = awsEndpointURLMetadata.doValidate("https://localhost:1111/");

        assertThat(errorMessage, is("Endpoint URL must not be localhost, since this gets resolved on the agents"));
    }

    @Test
    void shouldReturnErrorIfTheEndpointHasLocalHostIP() {
        String errorMessage = awsEndpointURLMetadata.doValidate("https://127.0.0.1:1111/");

        assertThat(errorMessage, is("Endpoint URL must not be localhost, since this gets resolved on the agents"));
    }
}