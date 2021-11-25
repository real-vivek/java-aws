package com.java.aws.dao;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.internal.IteratorSupport;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.java.aws.config.AWSConfig;
import com.java.aws.config.AWSProperties;
import com.java.aws.model.Address;
import com.java.aws.model.Customer;
import com.java.aws.model.Transaction;

@Repository
public class DynamoDBDAO {

	@Autowired
	private AWSConfig awsConfig;

	@Autowired
	private AWSProperties awsProperties;

	@Autowired
	private CustomerTranslator customerTranslator;
	

	// Single lookup using DynamoDBMapper
	public Transaction getTransaction() {
		// Basic load
		// Combination of transactionId and date must be unique so that unique look up
		// is possible
		DynamoDBMapper dynamoDBMapper= new DynamoDBMapper(awsConfig.getDynamoDbClient());
		Transaction transaction = new Transaction();
		transaction.setTransactionId("t1");
		transaction.setDate("2021-03-02");
		Transaction retrievedTransaction = dynamoDBMapper.load(transaction);
		System.out.println(retrievedTransaction);
		return retrievedTransaction;
	}

	// Single lookup using QuerySPec
	public Transaction getTransactionUsingQuerySpec() {
		// Basic load
		Transaction transaction = null;
		QuerySpec querySpec = new QuerySpec().withKeyConditionExpression("#transId = :transId") // Use the
				// KeyConditionExpression
				// parameter to provide
				// a specific value for
				// the partition key
				.withFilterExpression("#type = :type")
				.withNameMap(new NameMap().with("#transId", "transaction-id").with("#type", "type"))
				.withValueMap(new ValueMap().withString(":transId", "t1").withString(":type", "PURCHASE"));

		// This code is written to check for null value if value is null then customized
		// null pointer exception is thrown with given string
		Objects.requireNonNull(querySpec.getValueMap().get(":transId"),
				new StringBuilder("No value set for ").append(":transId").append(" in QuerySpec").toString());

		// Getting table name
		Table table = getTable();

		ItemCollection<QueryOutcome> resultSet = table.query(querySpec);

		IteratorSupport<Item, QueryOutcome> iterator = resultSet.iterator();

		if (iterator.hasNext()) {
			Item item = iterator.next();
			transaction = populateTransaction(item);
		}

		// If we want to query on specific index first we get that index
		// Index index = getIndex(getTable(), "indexName");
		//
		// Here we are querying based on that index
		// ItemCollection<QueryOutcome> retrievedResultSet = index.query(querySpec);
		//
		// IteratorSupport<Item, QueryOutcome> iterator = retrievedResultSet.iterator();
		//
		// Item queriedItem = iterator.next();
		//
		// Transaction retrievedTransaction=populateTransaction(queriedItem);

		return transaction;
	}

	private Transaction populateTransaction(Item queriedItem) {
		Transaction transaction = new Transaction();
		transaction.setTransactionId(queriedItem.getString("transaction-id"));
		transaction.setDate(queriedItem.getString("date"));
		transaction.setVersion(queriedItem.getLong("version"));
		transaction.setType(queriedItem.getString("type"));
		transaction.setAmount(queriedItem.getInt("amount"));
		transaction.setCustomer(customerTranslator.unconvert(queriedItem.getString("customer")));
		return transaction;
	}

	// Gets configured table or throws error if unsuccessful
	private Table getTable() {
		return Optional.of(new DynamoDB(awsConfig.getDynamoDbClient()))
				.map(dynamoDB -> dynamoDB.getTable(awsProperties.getDynamodb_tableName()))
				.orElseThrow((() -> new RuntimeException()));
	}

	// Gets configured index or throws error if unsuccessful
	//	private Index getIndex(Table table, String indexId) {
	//		return Optional.of(table).map(t -> t.getIndex(indexId)).orElseThrow(() -> new RuntimeException());
	//	}
	
	
	// save using dynamoDBMapper
	public void saveTransactionUsingDynamoDBMapper(Transaction transaction) {
		DynamoDBMapper dynamoDBMapper= new DynamoDBMapper(awsConfig.getDynamoDbClient());
		// Make sure that value annotated with @DynamoDBVersionAttribute  is null
		// With optimistic locking, each item has an attribute that acts as a version number. 
		// If you retrieve an item from a table, the application records the version number of that item. 
		// You can update the item, but only if the version number on the server side has not changed. 
		// If there is a version mismatch, it means that someone else has modified the item before you did
		// If we don't set optimistic locking attribute to null the we get ConditionalCheckFailedException
		// If we want to overwrite version we can use: dynamoDBMapper.save(transaction,DynamoDBMapperConfig.builder().withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.CLOBBER).build());
		// Remember that we don't have to set value for locking attribute it should be by default null so dynamoDBMapper will set it to 1 when saving
		// If we set any value we get ConditionalCheckFailedException
		// If we try to save another record with same partition and sort key then version will be changed to 2
		// We need to get first modify data if needed and then save if we want to update
		dynamoDBMapper.save(transaction);
		// To do batch save use: dynamoDBMapper.batchSave(t1,t2,t3); however this doesn't respect optimistic locking we can overwrite version in our case
	}

	// save using putItem
	public void saveTransaction() {
			Table table = getTable();
			PutItemOutcome putItemOutcome = table.putItem(new Item()
					.withPrimaryKey("transaction-id", "t9")
					.withString("date", "2021-11-20")
					.withInt("amount", 540)
					.withString("type", "PURCHASE")
					.withString("customer", 
							customerTranslator.convert(Customer.builder() //converting cutomer pojo to string
							.customerId(6)
							.first_name("Ariana")
							.last_name("Mock")
							.customer_email("araina.mock@yahoo.com")
							.address(Address.builder()
									.country("USA")
									.state("IL")
									.city("CHICAGO")
									.postalCode(411047)
									.build())
							.build())));
			// we can use putItemOutcome.getPutItemResult().getSdkHttpMetadata().httpStatusCode to check 200 status
		}
	
	// retrieve all records having specific transactionId
	public List<Transaction> getAllTransaction(String transactionId){
		DynamoDBMapper dynamoDBMapper= new DynamoDBMapper(awsConfig.getDynamoDbClient());
		Transaction transaction = new Transaction();
		transaction.setTransactionId(transactionId);
		DynamoDBQueryExpression<Transaction> queryExpression = new DynamoDBQueryExpression<Transaction>()
		.withHashKeyValues(transaction)
		.withLimit(10);
		List<Transaction> retirvedList = dynamoDBMapper.query(Transaction.class, queryExpression);
		
		// Searching with GSI
		// Read using eventually consistency this is because secondary indexes are stored in a duplicate table 
		// Data which is in main table eventually is propagated to duplicate table where GSI are stored so there is a delay there
		// When we query there is possibility to get stale data
		// withConsistentRead(false) forces us to recognize it
		// Transaction transaction2 = new Transaction();
		// transaction.setDate("");// Set your GSI instead
		// DynamoDBQueryExpression<Transaction> queryExpression2 = new DynamoDBQueryExpression<Transaction>()
		// .withHashKeyValues(transaction)
		// .withIndexName("indexName")
		// .withConsistentRead(false) 
		// .withLimit(10);
		
		return retirvedList;
	}
	
	// deleting record using DynamoDBMapper
	public void deleteTransaction(){
		DynamoDBMapper dynamoDBMapper= new DynamoDBMapper(awsConfig.getDynamoDbClient());
		Transaction transactionToDelete = Transaction.builder()
		.transactionId("t11")
		.date("2021-10-25")
		.build();
		// First we need to load the transaction as we need to avoid overwrite
		// This prevents multiple threads from different env from interacting with same row in parallel and writing over one another
		// DynamoDBMapper will auto increment attribute with @DynamoDBVersionAttribute
		// We need to do get first modify data and then do any operation on it
		// Getting the data ensures that we get latest data(which may be modified by others) and not stale data
		Transaction retrievedTransaction = dynamoDBMapper.load(transactionToDelete);
		dynamoDBMapper.delete(retrievedTransaction);
	}
}
