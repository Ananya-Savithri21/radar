package merger;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import com.example.PublisherSyncExample;
import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PullRequest;
import com.google.pubsub.v1.PullResponse;
import com.google.pubsub.v1.ReceivedMessage;

class MergerTest {

	@InjectMocks
	private Merger merger = new Merger();

	@Test
	public void test_copytoFile() throws IOException {
		byte[] data = "deviceid".getBytes(StandardCharsets.UTF_8);
		merger.copytoFile(data);
	}

	@Test
	public void test_mergeChunks() throws IOException, ExecutionException, InterruptedException {

		String projectId = "cherish-serenity-dev";
		String subscriptionId = "test-template-sub";
		Integer numOfMessages = 15;
		PublisherSyncExample.publisherExample();
		byte[] data = "deviceid".getBytes(StandardCharsets.UTF_8);
		MultiValuedMap<String, ReceivedMessage> chunkMap = new ArrayListValuedHashMap<String, ReceivedMessage>();
//	    com.google.pubsub.v1.ReceivedMessage result = ReceivedMessage.getDefaultInstance();
		SubscriberStubSettings subscriberStubSettings = SubscriberStubSettings.newBuilder()
				.setTransportChannelProvider(SubscriberStubSettings.defaultGrpcTransportProviderBuilder()
						.setMaxInboundMessageSize(20 * 1024 * 1024) // 20MB (maximum message size).
						.build())
				.build();
		try (SubscriberStub subscriber = GrpcSubscriberStub.create(subscriberStubSettings)) {
			String subscriptionName = ProjectSubscriptionName.format(projectId, subscriptionId);
			PullRequest pullRequest = PullRequest.newBuilder().setMaxMessages(numOfMessages).setReturnImmediately(false)
					.setSubscription(subscriptionName).build();

			PullResponse pullResponse = subscriber.pullCallable().call(pullRequest);
			List<ReceivedMessage> message = pullResponse.getReceivedMessagesList();
			chunkMap.put("DeviceId", message.get(0));
			merger.mergeChunks(chunkMap);
		}
	}

}
