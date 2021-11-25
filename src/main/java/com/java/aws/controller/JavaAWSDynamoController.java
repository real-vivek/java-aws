package com.java.aws.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.java.aws.dao.DynamoDBDAO;
import com.java.aws.model.Transaction;

@RestController
@RequestMapping("dynamoDB")
public class JavaAWSDynamoController {

	@Autowired
	private DynamoDBDAO dynamoDBDAO;

	// Gets record using dynamoDBMapper
	@GetMapping("dynamoDBMapper/getTransaction")
	public Transaction getAllTransactions() {
		return dynamoDBDAO.getTransaction();
	}

	// Gets record using querySpec
	@GetMapping("querySpec/getTransaction")
	public Transaction getAllTransactionsUisngQuery() {
		return dynamoDBDAO.getTransactionUsingQuerySpec();
	}

	@PostMapping("dynamoDBMapper/saveTransaction")
	public void saveTransactionUsingDynamoDBMapper(@RequestBody Transaction transaction) {
		System.out.println(transaction);
		dynamoDBDAO.saveTransactionUsingDynamoDBMapper(transaction);
	}

	@PostMapping("saveTransaction")
	public void saveTransaction() {
		dynamoDBDAO.saveTransaction();
	}

	// Gets all the record using dynamoDBMapper corresponding to specific transactionId
	@GetMapping("dynamoDBMapper/getTransaction/{transactionId}")
	public List<Transaction> getAllTransactions(@PathVariable String transactionId) {
		return dynamoDBDAO.getAllTransaction(transactionId);
	}
	
	@DeleteMapping("dynamoDBMapper/delete")
	public void deleteTransactions() {
		 dynamoDBDAO.deleteTransaction();
	}
}
