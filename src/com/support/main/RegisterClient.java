package com.support.main;

import java.io.IOException;
import java.util.Set;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.support.network.HttpClient;
import com.support.utilities.Constants;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterClient {
    private static final String PREFS_NAME = "ANHSettings";
    private static final String REGID_SETTING_NAME = "ANHRegistrationId";
    private String Backend_Endpoint;
    SharedPreferences settings;
    private String authorizationHeader;

    public RegisterClient(Context context, String backendEnpoint) {
        super();
        this.settings = context.getSharedPreferences(PREFS_NAME, 0);
    }

    public String getAuthorizationHeader() {
        return authorizationHeader;
    }

    public void setAuthorizationHeader(String authorizationHeader) {
        this.authorizationHeader = authorizationHeader;
    }

    public void register(String handle, Set<String> tags) throws IOException, JSONException {

        String registrationId = retrieveRegistrationIdOrRequestNewOne(handle);

        JSONObject deviceInfo = new JSONObject();
        deviceInfo.put("Platform", "gcm");
        deviceInfo.put("Handle", handle);
        deviceInfo.put("Tags", new JSONArray(tags));

        int statusCode = upsertRegistration(registrationId, deviceInfo);

        if (statusCode == HttpStatus.SC_OK) {
            return;
        } else if (statusCode == HttpStatus.SC_GONE){
            settings.edit().remove(REGID_SETTING_NAME).commit();
            registrationId = retrieveRegistrationIdOrRequestNewOne(handle);
            statusCode = upsertRegistration(registrationId, deviceInfo);
            if (statusCode != HttpStatus.SC_OK) {
                Log.e("RegisterClient", "Error upserting registration: " + statusCode);
                throw new RuntimeException("Error upserting registration");
            }
        } else {
            Log.e("RegisterClient", "Error upserting registration: " + statusCode);
            throw new RuntimeException("Error upserting registration");
        }

    }

    private int upsertRegistration(String registrationId, JSONObject deviceInfo)
            throws IOException {

        return put(Constants.URL + "/api/Register/PutRegistration/"+registrationId, deviceInfo.toString());

    }

    private String retrieveRegistrationIdOrRequestNewOne(String handle) throws IOException {
        if (settings.contains(REGID_SETTING_NAME))
            return settings.getString(REGID_SETTING_NAME, null);

        String response = null;
        HttpClient client = new HttpClient();
        try {
            response = client.post(Constants.URL + "/api/Register/PostRegister?handle=" + handle,"");

        } catch (Exception e) {
            e.printStackTrace();
        }
        String registrationId = response;
        registrationId = registrationId.substring(1, registrationId.length()-1);
        settings.edit().putString(REGID_SETTING_NAME, registrationId).commit();

        return registrationId;
    }

    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    public int put(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .header("Authorization","Basic "+LoginActivity.AUTHORIZE )
                .url(url)
                .put(body)
                .build();

        Response response = client.newCall(request).execute();
        return response.code();
    }
}