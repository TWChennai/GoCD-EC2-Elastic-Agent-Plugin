package com.continuumsecurity.elasticagent.ec2.executors;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class AWSEndpointURLMetadata extends Metadata {
    private static final String AWS_ENDPOINT_URL_DISPLAY_VALUE = "Endpoint URL";

    public AWSEndpointURLMetadata(String key, boolean required, boolean secure) {
        super(key, required, secure);
    }

    @Override
    protected String doValidate(String input) {
        if (isBlank(input)) {
            return null;
        }

        URI uri;
        try {
            uri = new URL(input).toURI();
        } catch (URISyntaxException | MalformedURLException e) {
            return AWS_ENDPOINT_URL_DISPLAY_VALUE + " must be a valid URL (https://<vpc-endpoint-id>.ec2.<aws-region>.vpce.amazonaws.com)";
        }

        if (!uri.getScheme().equalsIgnoreCase("https")) {
            return AWS_ENDPOINT_URL_DISPLAY_VALUE + " must be a valid HTTPs URL (https://<vpc-endpoint-id>.ec2.<aws-region>.vpce.amazonaws.com)";
        }

        if (uri.getHost().equalsIgnoreCase("localhost") || uri.getHost().equalsIgnoreCase("127.0.0.1")) {
            return AWS_ENDPOINT_URL_DISPLAY_VALUE + " must not be localhost, since this gets resolved on the agents";
        }

        return null;
    }
}
