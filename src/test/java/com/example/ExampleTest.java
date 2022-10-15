package com.example;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

//@RunWith(JUnit4.class)
class ExampleTest {

	@Mock
	private HttpRequest request;
	@Mock
	private HttpResponse response;

	Example eg = Mockito.spy(Example.class);

//	@Before
//	public void setup() throws Exception{
//		MockitoAnnotations.openMocks(this);
//	}

//	@Test
//public void test_service()  {
//	List<String> expetedValue = new ArrayList<String>();
//	expetedValue.add("device_Id");
//	HashMap<String, String> fileCompleteMap = new HashMap<String, String>();
//	//stub
//doReturn(expetedValue).when(eg).ackMessageList(Mockito.any());
//try {
//	eg.service(request, response);
////	int res = eg.method2();
//} catch (Exception e) {
//	// TODO Auto-generated catch block
//	e.printStackTrace();
//}
//}

	@Test
	public void test_subscribeSyncExample() throws IOException {
		List<String> expetedValue = new ArrayList<String>();
		expetedValue.add("001");
		// stub
		doReturn(expetedValue).when(eg).ackMessageList(Mockito.any());
		eg.subscribeSyncExample("cherish-serenity-dev", "test-template-sub", 3);
	}

	@Test
	public void test_ackMessageList() {
		List<String> expetedValue = new ArrayList<String>();
		expetedValue.add("device_Id");
		HashMap<String, String> fileCompleteMap = new HashMap<String, String>();
//	doReturn(expetedValue).when(eg).ackMessageList(Mockito.any());
		eg.ackMessageList(fileCompleteMap);
	}

	@Test
	public void test_ack() {
		List<String> expetedValue = new ArrayList<String>();
		expetedValue.add("device_Id");
		HashMap<String, String> fileCompleteMap = new HashMap<String, String>();
		// stub
		doReturn(expetedValue).when(eg).ackMessageList(fileCompleteMap);
			List<String> output = eg.ackMessageList(fileCompleteMap);
			assertEquals(expetedValue, output);	
	}

//@Test
//public void testClass() {
//	doReturn(100).when(eg).method4();
//	int res = eg.method2();
//	System.out.println(res);
//}

}
