package com.java.aws.dao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.java.aws.config.AWSConfig;
import com.java.aws.config.AWSProperties;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class S3DAO {

	@Autowired
	private AWSConfig awsConfig;

	@Autowired
	private AWSProperties awsProperties;
	
	public boolean putObject(MultipartFile multipartFile) {
		File file = convertMultipartFile(multipartFile);
		AmazonS3 s3Client = awsConfig.getS3Client();
		s3Client.putObject(awsProperties.getS3BucketName(), file.getName(), file);
		return true;
	}
	
	private File convertMultipartFile(MultipartFile multipartFile) {
		File file = new File(multipartFile.getOriginalFilename());
		try (FileOutputStream fileOutputStream = new FileOutputStream(file) ){
			fileOutputStream.write(multipartFile.getBytes());
		} catch (IOException e) {
			log.error("Error converting multipart file to file: "+e);
		}
		return file;
	}
	
	public byte[] downlaodFile(String fileName) throws IOException {
		AmazonS3 s3Client = awsConfig.getS3Client();
		S3Object s3Object = s3Client.getObject(awsProperties.getS3BucketName(),fileName);
		S3ObjectInputStream objectContent = s3Object.getObjectContent();
		byte[] byteArray = IOUtils.toByteArray(objectContent);
		return byteArray;
	}
}
