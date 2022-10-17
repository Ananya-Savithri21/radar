package com.example;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.pubsub.v1.*;
import merger.Merger;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import java.io.IOException;
import java.util.*;


public class Example implements HttpFunction
{
    public static MultiValuedMap<String, ReceivedMessage> chunkMap = new ArrayListValuedHashMap<String, ReceivedMessage>();
    private static final String DIGEST_ALGORITHM_SHA1 = "SHA256";
    public static String projectId = "cherish-serenity-dev";
    public static String subscriptionId = "test-template-sub";
    public static Integer numOfMessages = 3;
    public static HashMap<String, String> fileWithLastSequenceMap = new HashMap<>();

    @Override
    public void service(HttpRequest request, HttpResponse response) throws Exception
    {
        Example pubsub = new Example();
        MultiValuedMap<String, ReceivedMessage> chunkMap = pubsub.subscribeSyncExample(projectId, subscriptionId, numOfMessages);
        Merger merger = new Merger();
        merger.mergeChunks(chunkMap);

        /*subscribeSyncExample(projectId, subscriptionId, numOfMessages);
        //System.out.println("Chunk size is " + chunkMap.size());
        Merger.mergeChunks(chunkMap);*/
    }
    

    public MultiValuedMap<String, ReceivedMessage> subscribeSyncExample(String projectId, String subscriptionId, Integer numOfMessages) throws IOException
    {
        SubscriberStubSettings subscriberStubSettings =
                SubscriberStubSettings.newBuilder()
                        .setTransportChannelProvider(
                                SubscriberStubSettings.defaultGrpcTransportProviderBuilder()
                                        .setMaxInboundMessageSize(20 * 1024 * 1024) // 20MB (maximum message size).
                                        .build())
                        .build();

        try (SubscriberStub subscriber = GrpcSubscriberStub.create(subscriberStubSettings))
        {
            String subscriptionName = ProjectSubscriptionName.format(projectId, subscriptionId);
            PullRequest pullRequest =
                    PullRequest.newBuilder()
                            .setMaxMessages(numOfMessages)
                            .setReturnImmediately(false)
                            .setSubscription(subscriptionName)
                            .build();

            // Use pullCallable().futureCall to asynchronously perform this operation.
            while (true)
            {
                PullResponse pullResponse = subscriber.pullCallable().call(pullRequest);

                // Stop the program if the pull response is empty to avoid acknowledging
                // an empty list of ack IDs.
                if (pullResponse.getReceivedMessagesList().isEmpty())
                {
                    System.out.println("No message was pulled. Exiting.");
                    break;
                    //return;
                }

                List<String> ackIds = new ArrayList<>();

                for (ReceivedMessage message : pullResponse.getReceivedMessagesList())
                {
                    // Handle received message
                    // ...
                    System.out.println("messageid: " + message.getMessage().getMessageId());

                    String filename = message.getMessage().getAttributesMap().get("filename");

                    chunkMap.put(filename, message);

                    String checksum = message.getMessage().getAttributesMap().get("CRC32C");

                    //check for lastchunk arrival and register the filename
                    if ((checksum != null) && checksum.length() > 0)
                    {
                        String sequence = message.getMessage().getAttributesMap().get("sequence");
                        fileWithLastSequenceMap.put(filename, sequence);
                    }
                }
            }

            HashMap<String, String> fileCompleteMap = completnessCheck(fileWithLastSequenceMap);
//            HashMap<String, String> fileCompleteMap = new HashMap<String, String>();
//            List<String> ackIds = ackMessageList(fileCompleteMap);
  
            List<String> ackIds =ackMessageList(fileCompleteMap);
            AcknowledgeRequest acknowledgeRequest =
                    AcknowledgeRequest.newBuilder()
                            .setSubscription(subscriptionName)
                            .addAllAckIds(ackIds)
                            .build();

            // Use acknowledgeCallable().futureCall to asynchronously perform this operation.
            subscriber.acknowledgeCallable().call(acknowledgeRequest);


            //retains only filenames available in filecompletemap(all chunks available)
            Set<String> fileNameKeySet = fileCompleteMap.keySet();

            for (String filename : fileNameKeySet)
            {
                if (!chunkMap.containsKey(filename))
                {
                    chunkMap.remove(filename);
                }
            }
        }

        return chunkMap;
    }

    public List<String> ackMessageList(HashMap<String, String> fileCompleteMap)
    {
        List<String> myackIds = new ArrayList<>();

        for (Map.Entry<String, String> entry : fileCompleteMap.entrySet())
        {
            String fileName = entry.getKey();

            Collection<ReceivedMessage> pubsubMessageCollection = chunkMap.get(fileName);

            for (ReceivedMessage message : pubsubMessageCollection)
            {
                //add all the valid message ids
                myackIds.add(message.getAckId());
            }
        }

        return myackIds;
    }

    public HashMap<String, String> completnessCheck(HashMap<String, String> completeChunkFileMap)
    {
        HashMap<String, String> chunkFileMap = completeChunkFileMap;

        System.out.println("entering completeness check");

        List<String> completeFile = new ArrayList<>();

        for (String fileName : chunkFileMap.keySet())
        {
            //check if file is complete by checking all the other chunks are available using sequence no
            ArrayList<Integer> fileSequenceList = new ArrayList();

            Collection<ReceivedMessage> chunks = chunkMap.get(fileName);

            //collect all the sequence numbers for a file
            Iterator<ReceivedMessage> itr = chunks.iterator();
            while (itr.hasNext())
            {
                PubsubMessage pubsubMessage = itr.next().getMessage();
                if(pubsubMessage.getAttributesMap().get("sequence")!= null) {
                fileSequenceList.add(Integer.parseInt(pubsubMessage.getAttributesMap().get("sequence")));
            
                }
                }

            //check if all the sequence numbers are available based on last sequencenumber for the file
            Integer lastSequenceNumber = Integer.parseInt(completeChunkFileMap.get(fileName));

            for (int i = 1; i <= lastSequenceNumber; i++)
            {
                boolean sequenceCheck = fileSequenceList.contains(i);

                if (!sequenceCheck)
                {
                    System.out.println("File is incomplete: " + fileName + "sequence missing: " + i);
//                    chunkFileMap.remove(fileName);
                    chunkFileMap.remove(chunkFileMap.get(fileName));
                    break;
                }
            }

            System.out.println("File is complete: " + fileName);
        }

        System.out.println("Exiting completeness check");

        return chunkFileMap;
    }
    
//    public int method2() {
//    	System.out.println("abc");
//    	HashMap<String, String> fileCompleteMap = new HashMap<String, String>();
//    	List<String> ackMessage=ackMessageList(fileCompleteMap);
//        int res =  method4();
//         return res;
//    }
//    
//    public int method4() {
//    	System.out.println("abc");
//        return 10;
//    }
    
}