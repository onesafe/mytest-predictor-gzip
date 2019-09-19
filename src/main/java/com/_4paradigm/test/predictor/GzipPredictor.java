package com._4paradigm.test.predictor;

import com._4paradigm.prophet.rest.client.HttpExecution;
import com._4paradigm.prophet.rest.client.HttpOperator;
import com._4paradigm.prophet.rest.client.SyncHttpOperator;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.http.message.BasicHeader;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class GzipPredictor
{
    public static void main( String[] args ) throws Exception
    {
        System.out.println( "Hello World!" );

        long serStart = System.currentTimeMillis();
        String bodyStr = readFileFromResourceToString("predictor.json");
        byte[] bodyBytes = GzipUtils.compress(bodyStr);
        long serEnd = System.currentTimeMillis();
        System.out.println("Get Body cost time: " + (serEnd - serStart) + "ms");


        HttpOperator httpOperator = new SyncHttpOperator(10, 10);


        long predictStart = System.currentTimeMillis();
        for(int i=0; i<999; i++) {
            predictor(httpOperator, bodyBytes);
        }
        byte[] bytes = predictor(httpOperator, bodyBytes);
        long predictEnd = System.currentTimeMillis();
        System.out.println("Average 1000 cost time: " + (predictEnd - predictStart)/1000 + "ms");


        long printStart = System.currentTimeMillis();
        System.out.println(new String(bytes));
        long printEnd = System.currentTimeMillis();
        System.out.println("Print cost time: " + (printEnd -  printStart) + "ms");
    }


    private static byte[] predictor(HttpOperator httpOperator, byte[] bodyBytes) throws Exception {
        long postStart = System.currentTimeMillis();
        byte[] bytes = HttpExecution.post("http://172.16.32.7:31195/api/predict")
                .header(new BasicHeader("Content-Encoding", "gzip"))
                .rawBody(bodyBytes)
                .executeForRaw(httpOperator);
        long postEnd = System.currentTimeMillis();
        System.out.println("Rest predictor cost time: " + (postEnd - postStart) + "ms");
        return bytes;
    }


    public static String readFileFromResourceToString(String path) throws IOException {
        return Resources.toString(Resources.getResource(path), Charsets.UTF_8);
    }
}
