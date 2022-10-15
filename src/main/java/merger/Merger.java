package merger;

import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.ReceivedMessage;
import gcs.GoogleCloudStorage;
import org.apache.commons.collections4.MultiValuedMap;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

public class Merger
{
    private static final String DIGEST_ALGORITHM_SHA1 = "SHA256";
    private static GoogleCloudStorage gcs = new GoogleCloudStorage();
    private byte[] buffer = new byte[8 * 1024 * 1024];

    public void mergeChunks(MultiValuedMap<String, ReceivedMessage> chunkMap) throws IOException
    {
        try
        {
            for (String fileName : chunkMap.keySet())
            {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                Collection<ReceivedMessage> receivedMessages = chunkMap.get(fileName);

                for (ReceivedMessage receivedMessage : receivedMessages)
                {
                    PubsubMessage msg = receivedMessage.getMessage();
                    int sequence = Integer.parseInt(msg.getAttributesMap().get("sequence"));
                    int bytesread = Integer.parseInt(msg.getAttributesMap().get("bytesread"));

                    System.out.println("sequence is " + sequence);
                    byte[] data = msg.getData().toByteArray();

                    outputStream.write(data, 0, bytesread);
                    outputStream.flush();
                }

                byte[] fileCompleteData = outputStream.toByteArray();
                copytoFile(fileCompleteData);
                gcs.write_to_bucket(fileCompleteData, fileName);
                outputStream.reset();
                outputStream.close();
            }
        }
        finally
        {
           // outputStream.close();
        }
    }

    public void copytoFile(byte[] fileCompleteData) throws IOException
    {
        try (OutputStream outputStream = new FileOutputStream("./src/main/hello" + Math.random() + ".txt"))
        {
            System.out.println("buffer legnth" + fileCompleteData.length);
            outputStream.write(fileCompleteData, 0, fileCompleteData.length);
            //uploader.outputStream.writeTo(outputStream);
        }
    }
}
