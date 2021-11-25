package com.java.aws.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.java.aws.dao.S3DAO;

@RestController
@RequestMapping("s3")
public class JavaAWSS3Controller {

	@Autowired
	private S3DAO s3dao;

	@PostMapping("saveFile")
	public ResponseEntity<String> saveFiletoS3(@RequestPart MultipartFile file) {
		boolean success = s3dao.putObject(file);
		if (success) {
			System.out.println("File uploaded successfully!!");
		}
		return new ResponseEntity<String>("Uploaded File Successfully!!", HttpStatus.CREATED);
	}

	@GetMapping("getFile/{fileName}")
	public ResponseEntity<ByteArrayResource> getFile(@PathVariable String fileName) throws IOException {
		byte[] data = s3dao.downlaodFile(fileName);
		ByteArrayResource resource = new ByteArrayResource(data);
		return ResponseEntity.ok().contentLength(data.length).header("Content-type", "application/octet-stream")
				.header("content-diposition", "atttachment; fileName=\"" + fileName + "\"").body(resource);
	}

}
