package gcs;

import com.google.cloud.WriteChannel;
import com.google.cloud.storage.*;
import com.google.common.io.ByteStreams;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class GoogleCloudStorage
{
    public static String projectid = "cherish-serenity-dev";
    public static String bucketName =  "serenity-1";

    /*String projectid =  System.getenv("projectid");
    String bucketName =  System.getenv("gcsbucket");*/
    ZonedDateTime zonedDateTime = ZonedDateTime.now(ZoneId.of("Z"));

    public static Storage getGCSService()
    {
        Storage storage = StorageOptions.newBuilder().setProjectId(projectid).build().getService();
        return storage;
    }

    public void write_to_bucket(byte[] fileCompleteData, String fileName)
    {
        Storage storage = getGCSService();
        BlobId myblobId = BlobId.of(bucketName, fileName);
        BlobInfo myblobinfo = BlobInfo.newBuilder(myblobId).build();
        storage.create(myblobinfo, fileCompleteData);
    }

//    public void write_to_bucket(InputStream inputStream, String fileName) throws IOException
//    {
//        Storage storage = getGCSService();
//        BlobInfo myblobinfo = getBlobInfo(bucketName,fileName);
//
//        try (WriteChannel writer = storage.writer(myblobinfo))
//        {
//            //System.out.println("***Inputstream***"+ inputStream);
//            //System.out.println("***writer***"+ writer);
//            ByteStreams.copy(inputStream, Channels.newOutputStream(writer));
//        }
//        catch (StorageException ex)
//        {
//            if (!(400 == ex.getCode() && "invalid".equals(ex.getReason())))
//            {
//                throw ex;
//            }
//        }
//    }
//
//    private BlobInfo getBlobInfo(String bucketname, String filename)
//    {
//        BlobId myblobId = BlobId.of(bucketname, filename);
//        BlobInfo myblobinfo = BlobInfo.newBuilder(myblobId).build();
//        return myblobinfo;
//    }
//
//    public String getPath(Map<String,String> queyParamMap) throws ParseException
//    {
//        String orgId = queyParamMap.get("orgname").toString();
//        String accountId = queyParamMap.get("accountid").toString();
//        String deviceId = queyParamMap.get("deviceid").toString();
//        String sessionId = queyParamMap.get("session").toString();
//        String timeStamp = queyParamMap.get("timestamp").toString();
//        String sequence = queyParamMap.get("sequence").toString();
//        String presence = queyParamMap.get("presence").toString();
//
//        String formattedDate = dateTimeFormatter(Long.valueOf(timeStamp));
//        String fileName = sessionId + "-" + timeStamp + "-" + sequence + "-" + presence;
//        return String.format("%s/%s/%s/%s/%s", orgId,accountId,deviceId,formattedDate, fileName);
//    }
//
//    public String dateTimeFormatter(Long epochTime) throws ParseException
//    {
//        //System.out.println("**********Epoch time is*********"+ epochTime);
//        Calendar cal = Calendar.getInstance();
//        //Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("IST"));
//        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd yyyy HH:mm:ss.SSS zzz");
//
//        Instant instant = Instant.ofEpochSecond(epochTime);
//        String dateTime = instant.toString();
//
//        Date myDate = Date.from(instant);
//
//        //SimpleDateFormat formatter = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
//        String formattedDate = formatter.format(myDate);
//
//        //System.out.println("********Hello Raw*********" + dateTime);
//        //System.out.println("********Formatted*********" + formattedDate);
//
//        cal.setTime(myDate);
//
//        int year = cal.get(Calendar.YEAR);
//        int month = cal.get(Calendar.MONTH) + 1;
//        int day = cal.get(Calendar.DAY_OF_MONTH);
//        int hour = cal.get(Calendar.HOUR);
//
//        /*System.out.println("Year: "+ year);
//        System.out.println("month: "+ month);
//        System.out.println("day: "+ day);
//        System.out.println("hour: "+ hour);*/
//
//        return String.format("%s-%s-%s-%s", year, month,day,hour);
//
//    }
}
