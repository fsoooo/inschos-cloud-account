package com.inschos.cloud.account.assist.kit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpKit {

    private static Logger log = LoggerFactory.getLogger(HttpKit.class);

    public static String readRequestBody(HttpServletRequest request) {
        BufferedReader reader = null;
        try {
            StringBuilder result = new StringBuilder();
            reader = request.getReader();

            for (String line = null; (line = reader.readLine()) != null; ) {
                result.append(line).append("\n");
            }
            String body = result.toString();

            if(log.isDebugEnabled()){
                log.debug(body);
            }

            return body;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static InputStream returnInputStream(String herf) {
        URL url = null;
        InputStream is =null;
        try {
            url = new URL(herf);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();//利用HttpURLConnection对象,我们可以从网络中获取网页数据.
            conn.setDoInput(true);
            conn.connect();
            is = conn.getInputStream(); //得到网络返回的输入流

        } catch (IOException e) {
            e.printStackTrace();
        }
        return is;
    }


}
