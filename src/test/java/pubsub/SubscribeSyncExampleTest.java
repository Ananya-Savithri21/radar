package pubsub;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.example.PublisherSyncExample;

class SubscribeSyncExampleTest {
	public static HashMap<String, String> fileWithLastSequenceMap = new HashMap<>();

	SubscribeSyncExample subscribeSyncExample = Mockito.spy(SubscribeSyncExample.class);
	@Test
	public void test_Subscription() {
		fileWithLastSequenceMap.put("filename", "2");
		subscribeSyncExample.completnessCheck(fileWithLastSequenceMap);
		
	}
	@Test
	public void test_subscribeSyncExample() throws IOException, ExecutionException, InterruptedException {
		List<String> expetedValue = new ArrayList<String>();
		expetedValue.add("001");
		//stub 
//		doReturn(expetedValue).when(subscribeSyncExample).ackMessageList(Mockito.any());
		PublisherSyncExample.publisherExample();
		subscribeSyncExample.subscribeSyncExample("cherish-serenity-dev","test-template-sub" , 3);
	}
	
	@Test
	public void test_ackMessageList() {
		List<String> expetedValue = new ArrayList<String>();
		expetedValue.add("device_Id");
		HashMap<String, String> fileCompleteMap = new HashMap<String, String>();
		//stub
	doReturn(expetedValue).when(subscribeSyncExample).ackMessageList(fileCompleteMap);		
		List<String> output =subscribeSyncExample.ackMessageList(fileCompleteMap);
	assertEquals(expetedValue, output);
	
}

}
