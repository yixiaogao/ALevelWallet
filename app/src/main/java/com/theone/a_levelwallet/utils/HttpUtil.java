package com.theone.a_levelwallet.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class HttpUtil {
    /* URL 常量  */
    public static final String URL = "http://a-levelwallet.zicp.net:33845/AlevelWallet/";

    /* 通过URL获得 httpGet对象  */
    public static HttpGet getHttpGet(String url) {
        HttpGet request = new HttpGet(url);
        return request;
    }

    /* 通过URL获得 httpPost对象  */
    public static HttpPost getHttpPost(String url) {
        HttpPost request = new HttpPost(url);
        return request;
    }

    /* 通过HttpGet获得 httpResponse对象  */
    public static HttpResponse getHttpResponse(HttpGet request) throws ClientProtocolException, IOException {
        HttpResponse response = new DefaultHttpClient().execute(request);
        return response;
    }

    /* 通过HttpPost获得 httpResponse对象  */
    public static HttpResponse getHttpResponse(HttpPost request) throws ClientProtocolException, IOException {
        HttpResponse response = new DefaultHttpClient().execute(request);
        return response;
    }


    /* 通过URL发送post请求, 返回请求结果     */
    public static String queryStringForPost(String url) {
        //获得HttpPost实例
        HttpPost request = HttpUtil.getHttpPost(url);
        String result = null;

        try {
            //获得HttpResponse实例
            HttpResponse response = HttpUtil.getHttpResponse(request);
            //判断请求是否成功
            if (response.getStatusLine().getStatusCode() == 200) {
                //返回请求结果
                result = EntityUtils.toString(response.getEntity(),"UTF-8");
                System.out.println("response.getEntity："+result);
                //解决中文乱码问题
//                StringBuffer sb = new StringBuffer();
//                HttpEntity entity = response.getEntity();
//                InputStream is = entity.getContent();
//                BufferedReader br = new BufferedReader(
//                        new InputStreamReader(is, "UTF-8"));
//                String data = "";
//                while ((data = br.readLine()) != null) {
//                    sb.append(data);
//                }
//                result = sb.toString();
                return result;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            result = "网络异常";
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            result = "网络异常";
            return result;
        }
        return null;
    }

    /* 通过HttpPost发送get请求, 返回请求结果     */
    public static String queryStringForGet(String url) {
        //获得HttpGet实例
        HttpGet request = HttpUtil.getHttpGet(url);
        String result = null;

        try {
            //获得HttpResponse实例
            HttpResponse response = HttpUtil.getHttpResponse(request);
            //判断请求是否成功
            if (response.getStatusLine().getStatusCode() == 200) {
                //返回请求结果
                result = EntityUtils.toString(response.getEntity());
                return result;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            result = "网络异常";
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            result = "网络异常";
            return result;
        }
        return null;
    }

    /* 通过HttpPost发送post请求, 返回请求结果     */
    public static String queryStringForPost(HttpPost request) {
        //返回的结果字符
        String result = "";

        try {
            //获得HttpResponse实例
            HttpResponse response = HttpUtil.getHttpResponse(request);
            //判断请求是否成功
            if (response.getStatusLine().getStatusCode() == 200) {
                //返回请求结果
                result = EntityUtils.toString(response.getEntity());
                return result;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            result = "网络异常";
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            result = "网络异常";
            return result;
        }

        return null;
    }


}