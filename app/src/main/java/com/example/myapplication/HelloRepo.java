package com.example.myapplication;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.icu.util.BuddhistCalendar;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class HelloRepo {
    public void hello(ExecutorService srv,Handler uiHandler) {
        srv.submit(() -> {
            try {
                URL url = new URL("http://10.51.47.157:8080/Hello/sayHello");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                BufferedReader reader= new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line="";
                String inputData="";
                StringBuilder buffer = new StringBuilder();
                while((line=reader.readLine())!=null){
                    buffer.append(line);
                }
                conn.disconnect();

                Message msg = new Message();
                msg.obj=buffer.toString();
                uiHandler.sendMessage(msg);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });}
    public interface CommentCallback {
        void onCommentsReceived(List<Comment> comments);
    }

    public void getComments(ExecutorService srv, CommentCallback callback, String course) {
        srv.submit(() -> {
            Log.d("AsyncTask", "Inside srv.submit");
            try {
                URL url = new URL("http://10.51.47.157:8080/Comment/getComments/" + course);
                Log.d("AsyncTask","inside http get request");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/JSON");
                JSONObject objToSend = new JSONObject();
                //objToSend.put("courseId", courseId);
                //objToSend.put("comment", comment);
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    StringBuilder buffer = new StringBuilder();
                    String line="";
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }
                    Log.d("asd", String.valueOf(buffer));
                     //Send the comments to the UI thread
                    Gson gson = new Gson();
                    Type commentListType = new TypeToken<List<Comment>>() {}.getType();
                    List<Comment> comments = gson.fromJson(buffer.toString(), commentListType);

                    // Send the comments to the callback
                    callback.onCommentsReceived(comments);
                    new Handler(Looper.getMainLooper()).post(() -> callback.onCommentsReceived(comments));

                } finally {
                    conn.disconnect();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void postComment(ExecutorService srv, Handler uiHandler, String comment, String courseId, String courseName) {
        srv.submit(() -> {
            try{
                try {
                    URL url = new URL("http://10.51.47.157:8080/Comment/postComment/");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/JSON");
                    JSONObject objToSend = new JSONObject();
                    objToSend.put("courseId", courseId);
                    objToSend.put("comment", comment);
                    String outputData = "{\"courseId\":\"" + courseId + "\",\"courseName\":\"" + courseName + "\",\"comment\":\"" + comment + "\"}";
                    BufferedOutputStream writer = new BufferedOutputStream(conn.getOutputStream());

                    writer.write(outputData.getBytes(StandardCharsets.UTF_8));
                    writer.flush();

                    // Simulating a delay to represent network request

                    // Send a message to update the UI
                    Message msg = new Message();
                    msg.obj = "Comment Posted: " + comment;
                    uiHandler.sendMessage(msg);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        });
    }
}