package com.sun.alwayssunny.Service;

import android.util.Base64;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HttpGet {

    private Map<String, String> params = new HashMap<String, String>();
    private String charset;
    private String surl;

    public HttpGet(String url, String charset) {
        this.surl = url;
        this.charset = charset;
    }

    public void addFormField(String name, String value) {
        params.put(name, value);
    }

    public void addFormFieldSecure(String name, String value) {
        String result =  Base64.encodeToString(value.getBytes(), Base64.DEFAULT).replace("\n", "");
        params.put(name, result);
    }

    public String finish() throws Exception {
        // adapted from HttpPost

        StringBuilder queryString = new StringBuilder();
        for (String key : params.keySet()) {
            if (queryString.length() != 0)
                queryString.append('&');
            else
                queryString.append('?');
            queryString.append(key);
            queryString.append('=');
            queryString.append(params.get(key));
        }

        URL url = new URL(surl + queryString);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        Reader in = new BufferedReader(new InputStreamReader(
                conn.getInputStream(), charset));
        StringBuffer rv = new StringBuffer();
        for (int c; (c = in.read()) >= 0; rv.append((char) c))
            ;
        return rv.toString();
    }

}
