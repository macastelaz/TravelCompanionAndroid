package com.castelcode.cruisecompanion.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class JsonObjectRequestFactory {
    private static final String TAG = "JSON_OBJ_REQ_FACTORY";


    public static JsonObjectRequest createCurrencyConversionJsonObjectRequest(Context context,
                                                                              String url,
                                                                              String backupUrl){
        return new JsonObjectRequest(Request.Method.GET, url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject rates = response.getJSONObject("rates");
                            ConversionConstants.CAD = Double.valueOf(rates.get("CAD").toString());
                            ConversionConstants.EUR = Double.valueOf(rates.get("EUR").toString());
                            ConversionConstants.USD = Double.valueOf(rates.get("USD").toString());
                            ConversionConstants.GBP = Double.valueOf(rates.get("GBP").toString());
                            ConversionConstants.MXN = Double.valueOf(rates.get("MXN").toString());
                        } catch (JSONException e) {
                            Log.i(TAG, "Error while converting response: " + e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Attempting to hit the Fixer API Service");
                if(!backupUrl.equals("")) {
                    JsonObjectRequest backupRequest =
                            JsonObjectRequestFactory.createCurrencyConversionJsonObjectRequest(
                                    context, backupUrl, "");
                    RequestQueueSingleton.getInstance(context.getApplicationContext())
                            .addToRequestQueue(backupRequest);
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                try {
                    PackageInfo pInfo =
                            context.getPackageManager().getPackageInfo(
                                    context.getApplicationContext().getPackageName(), 0);

                    String uaHeader = context.getApplicationContext().getPackageName() + " - "
                            + pInfo.versionName ;
                    params.put("User-Agent", uaHeader);
                    Log.i(TAG, "USER AGENT: " + uaHeader);

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                return params;
            }
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                    if (cacheEntry == null) {
                        cacheEntry = new Cache.Entry();
                    }
                    final long cacheHitButRefreshed = 60 * 60 * 1000; // in 60 minutes cache will be hit, but also refreshed on background
                    final long cacheExpired = 60 * 60 * 1000; // in 60 minutes this cache entry expires completely
                    long now = System.currentTimeMillis();
                    final long softExpire = now + cacheHitButRefreshed;
                    final long ttl = now + cacheExpired;
                    cacheEntry.data = response.data;
                    cacheEntry.softTtl = softExpire;
                    cacheEntry.ttl = ttl;
                    String headerValue;
                    headerValue = response.headers.get("Date");
                    if (headerValue != null) {
                        cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    headerValue = response.headers.get("Last-Modified");
                    if (headerValue != null) {
                        cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                    }
                    cacheEntry.responseHeaders = response.headers;
                    final String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    return Response.success(new JSONObject(jsonString), cacheEntry);
                } catch (UnsupportedEncodingException e) {
                    Log.e(TAG, e.toString());
                    return Response.error(new ParseError(e));
                } catch (JSONException e) {
                    Log.e(TAG, e.toString());
                    return Response.error(new ParseError(e));
                }
            }

            @Override
            protected void deliverResponse(JSONObject response) {
                super.deliverResponse(response);
            }

            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }

            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }
        };
    }

}
