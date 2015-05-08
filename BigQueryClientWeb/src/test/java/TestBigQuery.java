

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.service.BigQueryService;

@ContextConfiguration(locations = "classpath:applicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)

public class TestBigQuery {

	
	@Autowired
	private BigQueryService bigQueryService;

	@Test
	@Ignore
	public void testTables() throws Exception{
		bigQueryService.getTableList("publicdata:samples");
		System.out.println("bitti");
	}
	
	@Test
	//@Ignore
	public void testTables2() throws Exception{
		bigQueryService.getDemoTableList();
		System.out.println("bitti");
	}
	
	@Test
	@Ignore
	public void getDataSets() throws Exception{
		bigQueryService.getDataSets();
	}
	
}
