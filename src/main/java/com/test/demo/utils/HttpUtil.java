package com.test.demo.utils;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: wanjun
 * Date: 8/2/13
 * Time: 4:23 PM
 */
public class HttpUtil {

    private final static Logger log = Logger.getLogger(HttpUtil.class);

    public static String doGet(String url,Map<String,Object> params) throws MalformedURLException,IOException{
        try{
            StringBuilder urlParams = new StringBuilder();
            urlParams.append(url);
            urlParams.append("?");
            if(params != null){
                for(String key:params.keySet()){
                    urlParams.append(key);
                    urlParams.append("=");
                    urlParams.append(params.get(key));
                    urlParams.append("&");
                }
            }
            log.info("url:"+urlParams.toString());
            URL urlURL = new URL(urlParams.toString());
            HttpURLConnection connection = (HttpURLConnection) urlURL.openConnection();
            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = null;
            StringBuilder lines = new StringBuilder();
            while ((line = reader.readLine()) != null){
                lines.append(line);
            }
            reader.close();
            connection.disconnect();
            log.info("result:"+lines.toString());
            return lines.toString();
        } catch (MalformedURLException e){
            e.printStackTrace();
            throw e;
        } catch (IOException e){
            e.printStackTrace();
            throw e;
        }
    }
}
