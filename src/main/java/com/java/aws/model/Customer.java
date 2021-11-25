package com.java.aws.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@Getter
@Setter
@NoArgsConstructor// required
@AllArgsConstructor
public class Customer {

	@JsonProperty("customer_id")
	private Integer customerId;
	
	private String first_name;
	
	private String last_name;
	
	@JsonProperty("email")
	private String customer_email;
	
	private Address address;
}
