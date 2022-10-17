package gcs;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.cloud.storage.Storage;

class GoogleCloudStorageTest {


	 @Mock 
	 private  Storage storage;
	 
	  @Mock private HttpRequest request;
	  @Mock private HttpResponse response;
	  
	 String bytecode = "Testing";
	 byte[] bytes = bytecode.getBytes();
	GoogleCloudStorage googleStorage = new GoogleCloudStorage(); 
	
	@Test
	public void test_Service() {
	
		googleStorage.write_to_bucket(bytes,"file" );
		
	}




	



}
