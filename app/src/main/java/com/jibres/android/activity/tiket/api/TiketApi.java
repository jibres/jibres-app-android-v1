package com.jibres.android.activity.tiket.api;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.jibres.android.JibresApplication;
import com.jibres.android.keys;
import com.jibres.android.managers.AppManager;
import com.jibres.android.managers.UrlManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class TiketApi {
    public static void list(Context context,String page,TiketListener.listTiket listener){
        if (page==null){
            page = "1";
        }
        StringRequest request =
                new StringRequest(Request.Method.GET, UrlManager.get.tiket_list(context,page),
                        response -> {
                            try {
                                JSONObject mainObject = new JSONObject(response);
                                if (mainObject.getBoolean("ok")){
                                    JSONObject pagination = mainObject.getJSONObject("pagination");
                                    int total_page = pagination.getInt("total_page");
                                    JSONArray result = mainObject.getJSONArray("result");
                                    listener.onReceived(String.valueOf(result),total_page);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                listener.onFiled(true);
                            }
                        }, e -> listener.onFiled(false))
                        // Send Headers
                {
                    @Override
                    public Map<String, String> getHeaders()  {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("appkey", keys.appkey);
                        headers.put("apikey", AppManager.getApikey(context));
                        return headers;
                    }

                };
        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        JibresApplication.getInstance().addToRequestQueue(request);
    }

    public static void viewTiket(Context context,String tiket,TiketListener.viewTiket listener){
        StringRequest request =
                new StringRequest(Request.Method.GET, UrlManager.get.tiket_view(context,tiket),
                        response -> {
                            listener.onReceived(response);
                            try {
                                JSONObject mainObject = new JSONObject(response);
                                if (mainObject.getBoolean("ok")){
                                    JSONArray result = mainObject.getJSONArray("result");
                                    listener.onReceived(String.valueOf(result));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                listener.onFiled(true);
                            }
                        }, e -> listener.onFiled(false))
                        // Send Headers
                {
                    @Override
                    public Map<String, String> getHeaders()  {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("appkey", keys.appkey);
                        headers.put("apikey", AppManager.getApikey(context));
                        return headers;
                    }

                };
        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        JibresApplication.getInstance().addToRequestQueue(request);
    }

    public static void replay(Context context, String TIKET, String MASSAGE, String TITLE ,
                              TiketListener.replay listener){
        StringRequest request =
                new StringRequest(Request.Method.POST, UrlManager.get.tiket_replay(context,TIKET),
                        response -> {
                            try {
                                JSONObject mainObject = new JSONObject(response);
                                try {
                                    if (mainObject.getBoolean("ok")){
                                        JSONArray msg = mainObject.getJSONArray("msg");
                                        for (int i = 0 ; i<= msg.length();i++){
                                            JSONObject object = msg.getJSONObject(i);
                                            listener.onReceived(object.getString("text"),true);
                                        }
                                    }else {
                                        JSONArray msg = mainObject.getJSONArray("msg");
                                        for (int i = 0 ; i<= msg.length();i++){
                                            JSONObject object = msg.getJSONObject(i);
                                            listener.onReceived(object.getString("text"),false);
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                listener.onFiled(true);
                            }
                        }, e -> listener.onFiled(false))
                {
                    @Override
                    public Map<String, String> getHeaders()  {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("appkey", keys.appkey );
                        headers.put("apikey", AppManager.getApikey(context) );
                        return headers;
                    }
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @SuppressLint("HardwareIds")
                    @Override
                    public byte[] getBody() {
                        final Map<String,String> body = new HashMap<>();
                        body.put("content", MASSAGE );
                        if (TITLE != null){
                            body.put("title", TITLE );
                        }
                        return new Gson().toJson(body).getBytes(StandardCharsets.UTF_8);
                    }
                };
        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        JibresApplication.getInstance().addToRequestQueue(request);
    }

    public static void addTiket(Context context, String TITLE, String MASSAGE ,
                                TiketListener.addTiket listener){
        StringRequest request =
                new StringRequest(Request.Method.POST, UrlManager.get.tiket_add(context),
                        response -> {
                            try {
                                JSONObject mainObject = new JSONObject(response);
                                try {
                                    if (mainObject.getBoolean("ok")){
                                        String id = mainObject.getJSONObject("result").getString("id");
                                        JSONArray msg = mainObject.getJSONArray("msg");
                                        for (int i = 0 ; i<= msg.length();i++){
                                            JSONObject object = msg.getJSONObject(i);
                                            listener.onReceived(object.getString("text"),true,id);
                                        }
                                    }else {
                                        JSONArray msg = mainObject.getJSONArray("msg");
                                        for (int i = 0 ; i<= msg.length();i++){
                                            JSONObject object = msg.getJSONObject(i);
                                            listener.onReceived(object.getString("text"),false,null);
                                        }
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                listener.onFiled(true);
                            }
                        }, e -> listener.onFiled(false))
                {
                    @Override
                    public Map<String, String> getHeaders()  {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("appkey", keys.appkey );
                        headers.put("apikey", AppManager.getApikey(context) );
                        return headers;
                    }
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @SuppressLint("HardwareIds")
                    @Override
                    public byte[] getBody() {
                        final Map<String,String> body = new HashMap<>();
                        body.put("content", MASSAGE );
                        if (TITLE != null){
                            body.put("title", TITLE );
                        }
                        return new Gson().toJson(body).getBytes(StandardCharsets.UTF_8);
                    }
                };
        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        JibresApplication.getInstance().addToRequestQueue(request);
    }


    public static void setStatus(Context context, String TIKET, String status,
                                 TiketListener.setStatus listener){
        StringRequest request =
                new StringRequest(Request.Method.PUT, UrlManager.get.tiket_set_status(context,TIKET)
                        , response -> {
                    try {
                        JSONObject mainObject = new JSONObject(response);
                        try {
                            if (mainObject.getBoolean("ok")){
                                JSONArray msg = mainObject.getJSONArray("msg");
                                for (int i = 0 ; i<= msg.length();i++){
                                    JSONObject object = msg.getJSONObject(i);
                                    listener.onReceived(object.getString("text"),true);
                                }
                            }else {
                                JSONArray msg = mainObject.getJSONArray("msg");
                                for (int i = 0 ; i<= msg.length();i++){
                                    JSONObject object = msg.getJSONObject(i);
                                    listener.onReceived(object.getString("text"),false);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFiled(true);
                    }
                }, e -> listener.onFiled(false))
                {
                    @Override
                    public Map<String, String> getHeaders()  {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("appkey", keys.appkey );
                        headers.put("apikey", AppManager.getApikey(context) );
                        return headers;
                    }
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @SuppressLint("HardwareIds")
                    @Override
                    public byte[] getBody() {
                        final Map<String,String> body = new HashMap<>();
                        body.put("status", status);
                        return new Gson().toJson(body).getBytes(StandardCharsets.UTF_8);
                    }
                };
        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        JibresApplication.getInstance().addToRequestQueue(request);
    }

    public static void setSolved(Context context, String TIKET, boolean solved,
                                 TiketListener.setStatus listener){
        StringRequest request =
                new StringRequest(Request.Method.PUT, UrlManager.get.tiket_set_solved(context,TIKET)
                        , response -> {
                    try {
                        JSONObject mainObject = new JSONObject(response);
                        try {
                            if (mainObject.getBoolean("ok")){
                                JSONArray msg = mainObject.getJSONArray("msg");
                                for (int i = 0 ; i<= msg.length();i++){
                                    JSONObject object = msg.getJSONObject(i);
                                    listener.onReceived(object.getString("text"),true);
                                }
                            }else {
                                JSONArray msg = mainObject.getJSONArray("msg");
                                for (int i = 0 ; i<= msg.length();i++){
                                    JSONObject object = msg.getJSONObject(i);
                                    listener.onReceived(object.getString("text"),false);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        listener.onFiled(true);
                    }
                }, e -> listener.onFiled(false))
                {
                    @Override
                    public Map<String, String> getHeaders()  {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("appkey", keys.appkey );
                        headers.put("apikey", AppManager.getApikey(context) );
                        return headers;
                    }
                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @SuppressLint("HardwareIds")
                    @Override
                    public byte[] getBody() {
                        final Map<String,Boolean> body = new HashMap<>();
                        body.put("solved", solved);
                        return new Gson().toJson(body).getBytes(StandardCharsets.UTF_8);
                    }
                };
        request.setRetryPolicy(
                new DefaultRetryPolicy(
                        DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        JibresApplication.getInstance().addToRequestQueue(request);
    }









    void a(){
        /*SimpleMultiPartRequest
                smr = new SimpleMultiPartRequest(Request.Method.POST, URL,
                response -> {
                    Log.d("amingoliss", "onErrorResponse: "+response );

                },
                error -> Log.e("amingoliss", "onErrorResponse: "+error.getMessage()))
        {
            @Override
            public Map<String, String> getHeaders()  {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("appkey", keys.appkey );
                headers.put("apikey", "" );
                return headers;
            }
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @SuppressLint("HardwareIds")
            @Override
            public byte[] getBody() {
                final Map<String,String> body = new HashMap<>();
                body.put("content", "" );
                return new Gson().toJson(body).getBytes(StandardCharsets.UTF_8);
            }
        };

        smr.addStringParam("param string", " data text");
        smr.addFile("param file", "");
        JibresApplication.getInstance().addToRequestQueue(smr);*/
    }
}
