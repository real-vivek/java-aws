package com.java.aws.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Component
public class AWSConfig {

	@Autowired
	private AWSProperties awsProperties;

	public AmazonDynamoDB getDynamoDbClient() {
		ClientConfiguration clientConfiguration = getClientConfiguration();
		EndpointConfiguration endpointConfiguration = new EndpointConfiguration(awsProperties.getDynamodb_endpoint(),awsProperties.getAws_region());
		BasicAWSCredentials basicAWSCredentials = getBasicAWSCredentials();
		return AmazonDynamoDBClientBuilder.standard()
				.withEndpointConfiguration(endpointConfiguration)
				.withClientConfiguration(clientConfiguration)
				.withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
				.build();
	}
	
	public AmazonS3 getS3Client() {
		ClientConfiguration clientConfiguration = getClientConfiguration();
		EndpointConfiguration endpointConfiguration = new EndpointConfiguration(awsProperties.getS3BucketEndpoint(), awsProperties.getAws_region());
		BasicAWSCredentials basicAWSCredentials = getBasicAWSCredentials();
		return AmazonS3ClientBuilder.standard()
				.withEndpointConfiguration(endpointConfiguration)
				.withClientConfiguration(clientConfiguration)
				.withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
				.build();
	}

	private BasicAWSCredentials getBasicAWSCredentials() {
		return new BasicAWSCredentials(awsProperties.getApp_id(), 
				awsProperties.getApp_secret_key());
	}

	private ClientConfiguration getClientConfiguration() {
		ClientConfiguration clientConfiguration = new ClientConfiguration().withProtocol(Protocol.HTTPS);
		// After setting client configuration we can set proxyHost, port, userName, password, etc
		clientConfiguration.setSocketTimeout(120000);
		return clientConfiguration;
	}

}
