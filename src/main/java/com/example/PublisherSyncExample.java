package com.example;

import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PublisherSyncExample {
	public static String projectId = "cherish-serenity-dev";
	public static String topicId = "test-template";

	public static void main(String... args) throws Exception {
		// TODO(developer): Replace these variables before running the sample.
		publisherExample();
	}

	public static void publisherExample() throws IOException, ExecutionException, InterruptedException {
		TopicName topicName = TopicName.of(projectId, topicId);

		Publisher publisher = null;
		try {
			// Create a publisher instance with default settings bound to the topic
			publisher = Publisher.newBuilder(topicName).build();

			String message = "Hello World 6!";
			ByteString data = ByteString.copyFromUtf8(message);
//            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).putAttributes("pubsubMessage", "newFile").build();
			Map<String, String> attributeMap = new HashMap<>();
			attributeMap.put("filename", "newFile");
			attributeMap.put("CRC32C", "CRC32Cvalue");
			attributeMap.put("sequence", "6");
			attributeMap.put("bytesread", "1");

			PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).putAllAttributes(attributeMap)
					.build();
			ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
			String messageId = messageIdFuture.get();
			System.out.println("Published message ID: " + messageId);
		} finally {
			if (publisher != null) {
				// When finished with the publisher, shutdown to free up resources.
				publisher.shutdown();
				publisher.awaitTermination(1, TimeUnit.MINUTES);
			}
		}
	}

}
