package com.gradel.parent.tencent.cmq.api.util;

import com.gradel.parent.tencent.cmq.api.enums.ErrorCodeEnum;
import com.gradel.parent.tencent.qcloud.cmq.CMQServerException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class CMQHttpUtil {
    /*
     * if we find the url is different with this.url we should new another connection
     *
     */
    private static URLConnection newHttpConnection(String url, boolean isKeepAlive) throws Exception {
        URLConnection connection = null;
        URL realUrl = new URL(url);
        if (url.toLowerCase().startsWith("https")) {
            HttpsURLConnection httpsConn = (HttpsURLConnection) realUrl.openConnection();
            httpsConn.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            connection = httpsConn;
        } else {
            connection = realUrl.openConnection();
        }
        connection.setRequestProperty("Accept", "*/*");
        if (isKeepAlive)
            connection.setRequestProperty("Connection", "Keep-Alive");
        connection.setRequestProperty("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        return connection;
    }

    public static String request(String method, String url, String req, boolean isKeepAlive, int connectTimeoutMilliseconds, int readTimeoutMilliseconds) throws Exception {
        String result = "";
        BufferedReader in = null;
        try {
            URLConnection connection = newHttpConnection(url, isKeepAlive);

            connection.setConnectTimeout(connectTimeoutMilliseconds);
            connection.setReadTimeout(readTimeoutMilliseconds);

            if (method.equals("POST")) {
                ((HttpURLConnection) connection).setRequestMethod("POST");

                connection.setDoOutput(true);
                connection.setDoInput(true);
                DataOutputStream out = new DataOutputStream(connection.getOutputStream());
                out.writeBytes(req);
                out.flush();
                out.close();
            }

            connection.connect();
            int status = ((HttpURLConnection) connection).getResponseCode();
            if (status != 200) {
                throw new CMQServerException(ErrorCodeEnum.FAILURE_HTTP_STATUS.getCode(), "http status:" + status, "");
            }

            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));

            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (in != null)
                    in.close();
            } catch (Exception e2) {
                throw e2;
            }
        }

        return result;
    }
}
