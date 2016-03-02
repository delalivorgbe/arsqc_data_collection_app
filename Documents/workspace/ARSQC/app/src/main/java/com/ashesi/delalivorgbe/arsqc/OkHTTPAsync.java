package com.ashesi.delalivorgbe.arsqc;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by delalivorgbe on 11/1/15.
 */
public class OkHTTPAsync extends AsyncTask<File, Integer, String> {


    public interface OkHttpCallback {
        void call();
    }

    private OkHttpCallback callerActivity;

    private File fileToUpload;

    private Context appContext;

    public MainActivity mActivity;


    public OkHTTPAsync(File upFile, MainActivity activity){
        callerActivity = (OkHttpCallback)activity;
        mActivity = activity;
        fileToUpload = upFile;
        appContext = ApplicationContextProvider.getContext();
    }

    public OkHTTPAsync(File upFile){
        fileToUpload = upFile;
    }

    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");


    private OkHttpClient getClient() {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, TimeUnit.MINUTES);
        client.setReadTimeout(10, TimeUnit.MINUTES);
        return client;
    }


    @Override
    protected String doInBackground(File... params){

        RequestBody requestBody = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart("fileToUpload", fileToUpload.getName(),
                        RequestBody.create(MEDIA_TYPE_MARKDOWN, fileToUpload))
                .build();

        //.url("http://54.69.212.93/uploadarsqc.php")

        Request request = new Request.Builder()
                .url("http://cs.ashesi.edu.gh/arsqc/ayorkor.php")
                .post(requestBody)
                .build();

        Response response = null;
        try {
            response = getClient().newCall(request).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String responseBody = response.body().string();
            System.out.println(responseBody);

            if(responseBody.equals("success")){
                System.out.println("successfully uploaded " + fileToUpload.getName());
                if(fileToUpload.delete()){
                    System.out.println("successfully deleted " + fileToUpload.getName());
                    mActivity.resetUploadButton();
                }else{
                    System.out.println("Delete failed "+ fileToUpload.getName());
                }
            }else{

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }
}
