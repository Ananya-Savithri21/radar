package com.example;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

	Example example = Mockito.spy(Example.class);

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
	public void test_subscribeSyncExample() throws IOException, ExecutionException, InterruptedException {
		List<String> expetedValue = new ArrayList<String>();
		expetedValue.add("001");
		// stub
//		doReturn(expetedValue).when(eg).ackMessageList(Mockito.any());
		PublisherSyncExample.publisherExample();
		example.subscribeSyncExample("cherish-serenity-dev", "test-template-sub", 3);
	}

	@Test
	public void test_ackMessageList() {
		List<String> expetedValue = new ArrayList<String>();
		expetedValue.add("device_Id");
		HashMap<String, String> fileCompleteMap = new HashMap<String, String>();
//	doReturn(expetedValue).when(eg).ackMessageList(Mockito.any());
		example.ackMessageList(fileCompleteMap);
	}

	@Test
	public void test_ack() {
		List<String> expetedValue = new ArrayList<String>();
		expetedValue.add("device_Id");
		HashMap<String, String> fileCompleteMap = new HashMap<String, String>();
		// stub
		doReturn(expetedValue).when(example).ackMessageList(fileCompleteMap);

			List<String> output = example.ackMessageList(fileCompleteMap);
			assertEquals(expetedValue, output);
		
	}

}
