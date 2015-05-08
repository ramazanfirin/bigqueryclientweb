package com.gui;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.el.ValueExpression;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIOutput;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import javax.faces.model.ListDataModel;

import org.primefaces.component.column.Column;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.api.services.bigquery.model.QueryResponse;
import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;
import com.model.ReadyReport;
import com.service.BigQueryService;

/**
 * 
 * User Managed Bean
 * 
 * @author onlinetechvision.com
 * @since 25 Mar 2012
 * @version 1.0.0
 *
 */
@Component
@ManagedBean(name="viewBean")
@SessionScoped
public class ViewBean implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Autowired(required=true)
	BigQueryService bigQueryService;
	
	private String query2 = "SELECT TOP(word, 50), COUNT(*) FROM publicdata:samples.shakespeare";
	
	private String query = "SELECT [W2DATE] AS [none_W2DATE_nk],   [model] AS [none_model_nk] FROM [dataset.gurkandata] [gurkandata] GROUP BY 1,   2";
	
	private DataTable dataTable;
	
	private String resultMessage;
	
	 private List<ColumnModel> simpleColumns;  
	 
	 private ListDataModel<String[]> dataModel;
	 
	 private List<String> tableList = new ArrayList<String>();
	 
	 private List<ReadyReport> readyReportList = new ArrayList<ReadyReport>();
	 
	 
 	
	 private int counter=0;
	 
	 
	 
	public ViewBean() {
		super();
		ReadyReport report = new ReadyReport();
		report.setLink("http://localhost:8080/birt/frameset?__report=BirtRate.rptdesign");
		report.setName("Dogum Orani");
		report.setSql("SELECT year, SUM(record_weight) as births FROM publicdata:samples.natality GROUP BY year");
		readyReportList.add(report);
	}

	public String getConnection() throws Exception{
		try {
			long start = new Date().getTime();
			QueryResponse response= bigQueryService.executeQuery(query);
			long finish = new Date().getTime();
			System.out.println("duration = "+(finish-start));
			buildDataModel(response);
			populateTableColums(response);
			//resultList.add("ramazan");
			
			resultMessage = "Kayit Sayisi= " + response.getRows().size()+"     processed data="+response.getTotalBytesProcessed()/(1024*1024) +" MB";
			RequestContext.getCurrentInstance().execute("stoptimer()");
			return "";
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}
	
	 public void resetMessage() {
		 resultMessage  ="";
	    }
	
	public String showTableList() throws Exception{
		tableList = bigQueryService.getTableList("dataset");
		tableList.addAll(bigQueryService.getDemoTableList());
		return "";
	}
	
	private void buildDataModel(QueryResponse response) {
        //build up the data
        List<String[]> data = new ArrayList<String[]>();
        
        for (Iterator iterator = response.getRows().iterator(); iterator.hasNext();) {
			TableRow row = (TableRow) iterator.next();
			List<TableCell> cell = row.getF();
			List<String> tempResult = new ArrayList<String>();
			for (Iterator iterator2 = cell.iterator(); iterator2.hasNext();) {
				TableCell tableCell = (TableCell) iterator2.next();
				String value = (String)tableCell.getV();
				tempResult.add(value);
			}
			String[] aaa = new String[tempResult.size()];
			for (int i = 0; i < tempResult.size(); i++) {
				aaa[i] = tempResult.get(i);
			}
			
			data.add(aaa);
		}
        
       dataModel = new ListDataModel(data);
       System.out.println("geldi");
    }
	
	public void populateTableColums(QueryResponse response){
		dataTable.getColumns().clear();
		int i =0;
		for (Iterator iterator = response.getSchema().getFields().iterator(); iterator.hasNext();) {
			TableFieldSchema schema = (TableFieldSchema) iterator.next();
			Column abcCol = buildColumn(schema.getName(),"#{item["+i+"]}");
			  dataTable.getColumns().add(abcCol);
			    i++;
		}
		
       
	}
		
	private Column buildColumn(String colTitle, String colValueExpr) {
        //add columns so the datatable knows what to display
        Column col = (Column) FacesContext.getCurrentInstance().getApplication().createComponent(Column.COMPONENT_TYPE);
        UIOutput title = (UIOutput) FacesContext.getCurrentInstance().getApplication().createComponent(UIOutput.COMPONENT_TYPE);
        title.setValue(colTitle);
        col.getFacets().put("header", title);
        //setup the field that will display the column data
        ValueExpression val = FacesContext.getCurrentInstance().getApplication().getExpressionFactory().createValueExpression(FacesContext.getCurrentInstance().getELContext(),colValueExpr, String.class);
        HtmlOutputText display = (HtmlOutputText) FacesContext.getCurrentInstance().getApplication().createComponent(HtmlOutputText.COMPONENT_TYPE);
        display.setValueExpression("value", val);
        col.getChildren().add(display);
        return col;
    }

	
	static public class ColumnModel implements Serializable {  
		  
        private String header;  
        private String property;  
  
        public ColumnModel(String header, String property) {  
            this.header = header;  
            this.property = property;  
        }  
  
        public String getHeader() {  
            return header;  
        }  
  
        public String getProperty() {  
            return property;  
        }  
    }  
	
	
	
	
	
	

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}








	public DataTable getDataTable() {
		return dataTable;
	}








	public void setDataTable(DataTable dataTable) {
		this.dataTable = dataTable;
	}


	public String getResultMessage() {
		return resultMessage;
	}

	public void setResultMessage(String resultMessage) {
		this.resultMessage = resultMessage;
	}


	public List<ColumnModel> getSimpleColumns() {
		return simpleColumns;
	}


	public void setSimpleColumns(List<ColumnModel> simpleColumns) {
		this.simpleColumns = simpleColumns;
	}


	public ListDataModel<String[]> getDataModel() {
		return dataModel;
	}


	public void setDataModel(ListDataModel<String[]> dataModel) {
		this.dataModel = dataModel;
	}

	public void setTableList(List<String> tableList) {
		this.tableList = tableList;
	}

	public List<String> getTableList() {
		return tableList;
	}

	public int getCounter() {
		return counter;
	}

	public void setCounter(int counter) {
		this.counter = counter;
	}

	public List<ReadyReport> getReadyReportList() {
		return readyReportList;
	}

	public void setReadyReportList(List<ReadyReport> readyReportList) {
		this.readyReportList = readyReportList;
	}

	

	
	 
	
}