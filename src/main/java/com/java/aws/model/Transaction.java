package com.java.aws.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBVersionAttribute;
import com.java.aws.dao.CustomerTranslator;

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
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName = "Transactions")
public class Transaction {

	@DynamoDBHashKey //Tells dynamoDbMapper that it is partition key
	@DynamoDBAttribute(attributeName = "transaction-id") // Tells dynamoDBMapper what is columName corresponding to attribute
	private String transactionId;
	
	@DynamoDBRangeKey // Tells dynamoDBMapper that it is sort key
	@DynamoDBAttribute(attributeName = "date")
	private String date;
	
	@DynamoDBAttribute(attributeName = "amount")
	private Integer amount;
	
	// Make sure that this property is set to Object type Long so that DynamoDBMapper can set it
	@DynamoDBVersionAttribute(attributeName = "version")
	private Long version;
	
	@DynamoDBAttribute(attributeName = "type")
	private String type;
	
	@DynamoDBAttribute(attributeName = "customer")
	@DynamoDBTypeConverted(converter = CustomerTranslator.class) // Tells dynamoDBMapper which class to use as Translator
	private Customer customer;
}
