package com.java.aws.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AWSProperties {

	@Value("${aws.region}")
	private String aws_region;

	@Value("${aws.appid}")
	private String app_id;

	@Value("${aws.secretkey}")
	private String app_secret_key;

	@Value("${dynamodb_endpoint}")
	private String dynamodb_endpoint;

	@Value("${dynamodb_tableName}")
	private String dynamodb_tableName;
	
	@Value("${s3.bucketName}")
	private String s3BucketName;
	
	@Value("${s3.endpoint}")
	private String s3BucketEndpoint;

	public String getAws_region() {
		return aws_region;
	}

	public String getApp_id() {
		return app_id;
	}

	public String getApp_secret_key() {
		return app_secret_key;
	}

	public String getDynamodb_endpoint() {
		return dynamodb_endpoint;
	}

	public String getDynamodb_tableName() {
		return dynamodb_tableName;
	}

	public String getS3BucketName() {
		return s3BucketName;
	}

	public String getS3BucketEndpoint() {
		return s3BucketEndpoint;
	}
}
