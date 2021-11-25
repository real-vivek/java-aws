package com.java.aws.dao;

import org.springframework.stereotype.Component;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.java.aws.model.Customer;

// Class which takes care of conversion between Customer Pojo and String and vice versa
@Component
public class CustomerTranslator implements DynamoDBTypeConverter<String, Customer> {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public String convert(Customer customer) {
		try {
			return objectMapper.writeValueAsString(customer);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public Customer unconvert(String string) {
		try {
			return objectMapper.readValue(string, Customer.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
