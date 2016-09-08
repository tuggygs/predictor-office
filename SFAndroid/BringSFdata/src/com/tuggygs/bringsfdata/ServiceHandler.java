package com.tuggygs.bringsfdata;

/**
 * Created by Turgay on 2/26/2016.
 */

import android.util.Log;
import android.webkit.WebView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class ServiceHandler {

    static String response = null;
    public final static int GET = 1;
    public final static int POST = 2;

    WebView webview;
    private String reqUrl = "https://login.salesforce.com/services/oauth2/authorize?response_type=token&display=touch&";
    private String consumerKey = "3MVG9A_f29uWoVQthjdFWYF0PiN7cPtdNCCIUYosSssveXeUhkIVbPI3rpwzzUcx6bqy9Y76Ca2GVef189FAA";

    public ServiceHandler() {

    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * */
    public String makeServiceCall(String url, int method) {
        return this.makeServiceCall(url, method, null);
    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     * */
    public String makeServiceCall(String url, int method, List<NameValuePair> params) {

        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if(method == POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }

                httpResponse = httpClient.execute(httpPost);

            }else if (method == GET) {
                // appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                Log.d("URLLLLLLLLL: ",url);
                HttpGet httpGet = new HttpGet(url);
                //httpGet.setHeader("Content-Type: ", "application/json");
               // String currSession = String.valueOf(UserInfo.getSessionId());
                httpGet.setHeader("Authorization", "Bearer " + "00D58000000KlI0!AQYAQCXpLcIBUmnukN6m.pdjltemjZIVIxsw.6SpvHpEAcMn0hzpWqxK8OJS8DdlgVGnyPq4vT.zah7dZCzpPpCD4APQPvfs");
                httpResponse = httpClient.execute(httpGet);
            }
            httpEntity = httpResponse.getEntity();
            response = EntityUtils.toString(httpEntity);
            Log.d("BEFORE RESPONSE: ",response);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }
}
