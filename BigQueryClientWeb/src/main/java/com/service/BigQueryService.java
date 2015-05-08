package com.service;

import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.Bigquery.Datasets;
import com.google.api.services.bigquery.BigqueryScopes;
import com.google.api.services.bigquery.model.DatasetList;
import com.google.api.services.bigquery.model.QueryRequest;
import com.google.api.services.bigquery.model.QueryResponse;
import com.google.api.services.bigquery.model.TableList;

@Service
public class BigQueryService {

	
	private static final String SCOPE = "https://www.googleapis.com/auth/bigquery";
	private static final HttpTransport TRANSPORT = new NetHttpTransport();

	private static final JsonFactory JSON_FACTORY = new JacksonFactory();
	private Bigquery bigquery;
	
	String privateKey="D:/calismalar/gurkan/BigQueryClientWeb/src/main/resources/privatekey.p12";
	//String privateKey="/mnt/ebs1/privatekey.p12";
	
	String TEST_QUERY2 = "SELECT TOP(word, 50), COUNT(*) FROM publicdata:samples.shakespeare";
	//String TEST_QUERY2 = "SELECT sum( CC_R ) FROM [dataset.gurkandata] LIMIT 50";
	
	public QueryResponse test() throws Exception{
		QueryResponse queryResponse= executeQuery(TEST_QUERY2);
		System.out.println("sorgu sonucu "+ queryResponse.getRows().size());
		
		return queryResponse;
	}
	
	public void setProxy(){
				
		final String authUser = "002834";
		final String authPassword = "Elifcan129";

		System.setProperty("http.proxyHost", "10.18.132.80");
		System.setProperty("http.proxyPort", "8080");
		System.setProperty("http.proxyUser", authUser);
		System.setProperty("http.proxyPassword", authPassword);

		Authenticator.setDefault(
		  new Authenticator() {
		    public PasswordAuthentication getPasswordAuthentication() {
		      return new PasswordAuthentication(authUser, authPassword.toCharArray());
		    }
		  }
		);
		
		

	}
	
	public void prepare() throws Exception{
		if (bigquery == null) {
			bigquery = setupConnection();
		}
		setProxy();
	}
	
	public Bigquery setupConnection() throws GeneralSecurityException, IOException{
		List<String> scopes = new ArrayList<String>();
		scopes.add(BigqueryScopes.BIGQUERY);
		GoogleCredential credential = new GoogleCredential.Builder().setTransport(TRANSPORT)
				.setJsonFactory(JSON_FACTORY)
				.setServiceAccountId("160087228600-i8dk6g3es9j1k3lbljb3dib6se09fim5@developer.gserviceaccount.com")
				.setServiceAccountScopes(scopes)
				.setServiceAccountPrivateKeyFromP12File(new File(privateKey)).build();
		
		

		bigquery = new Bigquery.Builder(TRANSPORT, JSON_FACTORY, credential).setApplicationName("nth-suprstate-560").setHttpRequestInitializer(credential).build();
		System.out.println("connectionOK");
		return bigquery;
	}
	
	
	public QueryResponse executeQuery( String query) throws Exception{
        if (bigquery == null) {
			bigquery = setupConnection();
		}
		
		QueryRequest queryInfo = new QueryRequest().setQuery(query);
		Bigquery.Jobs.Query queryRequest = bigquery.jobs().query("nth-suprstate-560", queryInfo);
		QueryResponse queryResponse = queryRequest.execute();
		queryResponse.getSchema();
		queryResponse.getTotalBytesProcessed();
		return queryResponse;
	}
	
	public List<String> getTableList(String dataset) throws Exception{
        if (bigquery == null) {
			bigquery = setupConnection();
		}
		
        List<String> resultList = new ArrayList<String>();
        
        Bigquery.Tables.List listRequest =bigquery.tables().list("nth-suprstate-560", dataset);
        TableList tableList = listRequest.execute();
        
        if(tableList.getTables()!=null){
        	 List<TableList.Tables> tablelist = tableList.getTables();
        	 for (TableList.Tables table : tablelist) {
     	        System.out.format("%s\n", table.getTableReference().getTableId());
     	       resultList.add(table.getTableReference().getTableId());
     	      }
        	
        }
		return resultList;
	}
	
	
	public List<String> getDemoTableList() throws Exception{
//        if (bigquery == null) {
//			bigquery = setupConnection();
//		}
		
		prepare();
        List<String> resultList = new ArrayList<String>();
        
        Bigquery.Tables.List listRequest =bigquery.tables().list("publicdata", "samples");
        TableList tableList = listRequest.execute();
        
        if(tableList.getTables()!=null){
        	 List<TableList.Tables> tablelist = tableList.getTables();
        	 for (TableList.Tables table : tablelist) {
     	        System.out.format("%s\n", table.getTableReference().getTableId());
     	       resultList.add(table.getTableReference().getTableId());
     	      }
        	
        }
		return resultList;
	}
	
	
	
	public void getDataSets() throws Exception{
		 if (bigquery == null) {
				bigquery = setupConnection();
			}
		
		Datasets.List datasetRequest = bigquery.datasets().list("nth-suprstate-560");
	    
		DatasetList datasetList = datasetRequest.execute();
	    if (datasetList.getDatasets() != null) {
	      List<DatasetList.Datasets> datasets = datasetList.getDatasets();
	      System.out.println("Available datasets\n----------------");
	      System.out.println(datasets.toString());
	      for (DatasetList.Datasets dataset : datasets) {
	        System.out.format("%s\n", dataset.getDatasetReference().getDatasetId());
	      }
	    }
	}
	
	
	
}
