package com.support.network;

import android.os.Handler;

import com.support.main.LoginActivity;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.MultipartBody;

/**
 * Created by Jeffery on 3/25/2016.
 */
public class HttpClient {


    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    private Handler mHandler;


    public String postCompany(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .header("Authorization","Basic anNvcm9rbzphYmMxMjM=" )
                .url(url)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String getLogin(String url) throws IOException {
        Request request = new Request.Builder()
                .header("Authorization", "Basic anNvcm9rbzphYmMxMjM=")
                .url(url)
                .build();


        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String putLogin(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .header("Authorization", "Basic anNvcm9rbzphYmMxMjM=")
                .url(url)
                .put(body)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }


    public String deleteCompany(String url) throws IOException {
        Request request = new Request.Builder()
                .header("Authorization", "Basic anNvcm9rbzphYmMxMjM=")
                .url(url)
                .delete()
                .build();


        Response response = client.newCall(request).execute();
        return response.body().string();

    }

    public String post(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .header("Authorization","Basic "+LoginActivity.AUTHORIZE )
                .url(url)
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String put(String url, String json) throws IOException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .header("Authorization", "Basic " + LoginActivity.AUTHORIZE)
                .url(url)
                .put(body)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }


    public String get(String url) throws IOException {
        Request request = new Request.Builder()
                .header("Authorization", "Basic " + LoginActivity.AUTHORIZE).url(url)
                .build();


        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String delete(String url) throws IOException {
        Request request = new Request.Builder()
                .header("Authorization","Basic "+LoginActivity.AUTHORIZE )
                .url(url)
                .delete()
                .build();


        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String uploadFile(String serverURL, byte[] file) {

        final String[] message = new String[1];
        try {

            RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"ic_launcher.png\""),
                            RequestBody.create(MediaType.parse("image/png"), file))
                    .build();

            Request request = new Request.Builder()
                    .header("Authorization", "Basic anNvcm9rbzphYmMxMjM=")

                    .url(serverURL)
                            .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                  message[0] = response.body().string().replace("\"", "");
                }
            });

            return message[0];
        } catch (Exception ex) {
            // Handle the error
        }
        return message[0];
    }
}



