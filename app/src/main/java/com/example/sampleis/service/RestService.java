package com.example.sampleis.service;

import android.os.StrictMode;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

public class RestService {

    private static String URL_AUTH = "https://10.0.2.2:9443/api/identity/auth/v1.1/authenticate";
    private static String CLIENT_ID = "hxeDY775u13gNxXt4x_oFXHX69oa";
    private static String CLIENT_SECRET = "He8Y2apncV9iiF_rOw1T2NbCdToa";
    private static String URL_OAUTH2 = "https://10.0.2.2:9443/oauth2/token";
    private static String URL_USER_IDENTITY = "https://10.0.2.2:9443/api/identity/user/v1.0/me";
    public static String TOKEN = null;
    public static String ACCESS_TOKEN = null;

    public RestService(){
        System.setProperty("jsse.enableSNIExtension", "false");
        disableCertificate();
    }

    public RestService(String urlAuth) {
        URL_AUTH = urlAuth;
        System.setProperty("jsse.enableSNIExtension", "false");
        disableCertificate();
    }

    public boolean oauthLogin() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        StringBuilder sb;
        HttpsURLConnection con = null;
        Map<String, Object> response = new HashMap<>();

        try {
            String payload = "grant_type=client_credentials";
            byte[] data = payload.getBytes();

            con = getConnection(URL_OAUTH2, "POST", "application/x-www-form-urlencoded;charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic " + getBase64Encoded(CLIENT_ID, CLIENT_SECRET));
            con.setRequestProperty("Content-Length", Integer.toString(data.length));

//            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream(), "UTF-8");
            DataOutputStream dos = new DataOutputStream(con.getOutputStream());
            dos.write(data);

            JSONObject jsonResponse = getResponseAsJson(con);

            ACCESS_TOKEN = (String) jsonResponse.get("access_token");

            response.put("response", jsonResponse);
            response.put("headers", con.getHeaderFields());
            response.put("statusCode", con.getResponseCode());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        return false;
    }

    //curl -u hxeDY775u13gNxXt4x_oFXHX69oa:He8Y2apncV9iiF_rOw1T2NbCdToa -k
    // -d "grant_type=password&username=joaoo&password=joaoo"
    // -H "Content-Type:application/x-www-form-urlencoded" "https://localhost:9443/oauth2/token"

    public Map<String, Object> loginOAuthUser(String user, String pass) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Map<String, Object> response = new HashMap<>();
        HttpsURLConnection con = null;

        try {
            String payload = "grant_type=password&username=" + user + "&password=" + pass;
            byte[] data = payload.getBytes();

            con = getConnection(URL_OAUTH2, "POST", "application/x-www-form-urlencoded;charset=UTF-8");
            con.setRequestProperty("Authorization", "Basic " + getBase64Encoded(CLIENT_ID, CLIENT_SECRET));
            con.setRequestProperty("Content-Length", Integer.toString(data.length));

            DataOutputStream dos = new DataOutputStream(con.getOutputStream());
            dos.write(data);

            JSONObject jsonResponse = getResponseAsJson(con);

            ACCESS_TOKEN = (String) jsonResponse.get("access_token");

            response.put("response", jsonResponse);
            response.put("headers", con.getHeaderFields());
            response.put("statusCode", con.getResponseCode());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }

        return response;
    }

    public Map<String, Object> login(String user, String password) throws UnsupportedEncodingException {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String base64 = getBase64Encoded(user, password);
        StringBuilder sb;
        HttpsURLConnection connection = null;

        Map<String, Object> response = new HashMap<>();

        try {
            connection = getConnection(URL_AUTH, "POST", null);
            connection.setRequestProperty("Authorization", "Basic " + base64);

            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");

            String payload = "";
            writer.write(payload);

            JSONObject responseJson = getResponseAsJson(connection);

            response.put("response", responseJson);
            response.put("headers", connection.getHeaderFields());
            response.put("statusCode", connection.getResponseCode());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }

        return response;
    }

    private String getBase64Encoded(String user, String password) throws UnsupportedEncodingException {
        String toEncode = user + ":" + password;
        byte[] data = toEncode.getBytes("UTF-8");

        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    private JSONObject getResponseAsJson(HttpsURLConnection connection) throws IOException, JSONException {
        StringBuilder sb;
        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        sb = new StringBuilder();

        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        return new JSONObject(sb.toString());
    }

    public boolean createNewUser(JSONObject user) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        oauthLogin();
        HttpsURLConnection con = getConnection(URL_USER_IDENTITY, "POST", null);

        try {
            con.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);

            DataOutputStream dos = new DataOutputStream(con.getOutputStream());
            dos.write(user.toString().getBytes());

            if (con.getResponseCode() >= 200 && con.getResponseCode() < 300) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getUserInfo() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);

        HttpsURLConnection con = null;

        try {
            con = getConnection(URL_USER_IDENTITY, "GET", null);
            con.setDoOutput(false);
            con.setRequestProperty("Authorization", "Bearer " + TOKEN);

            if (con.getResponseCode() >= 200 && con.getResponseCode() < 300) {
                JSONObject responseJson = getResponseAsJson(con);
                return responseJson.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    public JSONObject createJsonUser(String username, String password, String givenName, String emailAddress, String lastName, String mobile) {
        JSONObject json = new JSONObject();
        JSONObject user = new JSONObject();
        try {
            user.put("username", username);
            user.put("realm", "PRIMARY");
            user.put("password", password);
            JSONArray claims = new JSONArray();

            JSONObject gName = new JSONObject();
            gName.put("uri", "http://wso2.org/claims/givenname");
            gName.put("value", givenName);

            JSONObject eAddress = new JSONObject();
            eAddress.put("uri", "http://wso2.org/claims/emailaddress");
            eAddress.put("value", emailAddress);

            JSONObject lName = new JSONObject();
            lName.put("uri", "http://wso2.org/claims/lastname");
            lName.put("value", lastName);

            JSONObject m = new JSONObject();
            m.put("uri", "http://wso2.org/claims/mobile");
            m.put("value", mobile);

            claims.put(gName);
            claims.put(eAddress);
            claims.put(lName);
            claims.put(m);
            user.put("claims", claims);

            json.put("user", user);
            json.put("properties", new JSONArray());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    private HttpsURLConnection getConnection(String urlString, String HttpMethod, String contentType) {
        HttpsURLConnection connection = null;
        URL url;
        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/json";
        }
        try {
            url = new URL(urlString);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setHostnameVerifier(org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            connection.setRequestMethod(HttpMethod);
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", contentType);

            connection.setDoInput(true);
            connection.setDoOutput(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    private void disableCertificate() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {

            }

            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }
        };

        // Install the all-trusting trust manager
        SSLContext sc = null;
        try {
            sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

//    curl -k -v -X POST -H "Authorization: Basic YWRtaW46YWRtaW4=" -H "Content-Type: application/json"
//            "https://localhost:9443/api/identity/auth/v1.1/authenticate"

//    curl -k -v -X POST -H "Content-Type: application/json" -d '{ "username": "admin","password": "admin"}'
//"https://localhost:9443/api/identity/auth/v1.1/authenticate"

}
