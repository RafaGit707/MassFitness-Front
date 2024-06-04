package com.example.massfitness.util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Internetop {

    private static Internetop me = null;

    public static Internetop getInstance()
    {
        if (me == null)
        {
            synchronized (Internetop.class)
            {
                if (me == null)
                {
                    me = new Internetop();
                }
            }
        }
        return me;
    }

    public String postText(String url, List<Parametro> params)
    {
        int cont = 0;
        String res = okPostText(url, params);
        while ((cont < 5) && (res.equals("error.PIPE")))
        {
            ++cont;
            res = okPostText(url, params);
        }
        return res;
    }

    private String okPostText(String url, List<Parametro> params) {
        try {
            OkHttpClient client = new OkHttpClient();
            JSONObject jsonObject = new JSONObject();
            for (Parametro pair : params) {
                jsonObject.put(pair.getLlave(), pair.getValor());
            }
            RequestBody body = RequestBody.create(jsonObject.toString(), MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return "error.OKHttp";
            } else {
                return response.body().string();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return "error.PIPE";
        } catch (JSONException jsone) {
            jsone.printStackTrace();
            return "error.JSONException";
        }
    }

    public String putText(String url, List<Parametro> params)
    {
        int cont = 0;
        String res = okPutText(url, params);
        while ((cont < 5) && (res.equals("error.PIPE")))
        {
            ++cont;
            res = okPutText(url, params);
        }
        return res;
    }

    private String okPutText(String url, List<Parametro> params)
    {
        try
        {
            OkHttpClient client = new OkHttpClient();
            JSONObject jsonObject = new JSONObject();
            for (Parametro pair: params)
            {
                jsonObject.put(pair.getLlave(), pair.getValor());
            }
            RequestBody body = RequestBody.create(jsonObject.toString(),
                    MediaType.parse("application/json"));
            Request request = new Request.Builder()
                    .url(url)
                    .put(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
            {
                return "error.OKHttp";
            }
            else
            {
                String resultado=response.body().string();
                return resultado;
            }
        } catch (IOException ioe)
        {
            ioe.printStackTrace();
            return "error.PIPE";
        } catch (JSONException jsone)
        {
            jsone.printStackTrace();
            return "error.JSONException";
        }
    }

    public String getString(String url)
    {
        int cont = 0;
        String res = okGetString(url);
        while ((cont < 5) && (res.equals("error.IOException")))
        {
            ++cont;
            res = okGetString(url);
        }
        return res;
    }

    private String okGetString(String url)
    {
        try
        {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
            {
                return "error.OKHttp";
            }
            else
            {
                return response.body().string();
            }
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
            return "error.IOException";
        }
    }

    public String getText(String url, List<Parametro> params) {
        return executeWithRetry(() -> okGetText(url, params), 5);
    }

    private String okGetText(String url, List<Parametro> params) {
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        for (Parametro param : params) {
            urlBuilder.addQueryParameter(param.getLlave(), param.getValor());
        }
        String urlWithParams = urlBuilder.build().toString();
        Request request = new Request.Builder().url(urlWithParams).build();
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return "false";
            } else {
                return response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "false";
        }
    }
    private String executeWithRetry(RequestExecutor executor, int maxRetries) {
        int attempts = 0;
        String result;
        do {
            result = executor.execute();
            attempts++;
        } while (attempts < maxRetries && result.equals("error.PIPE"));
        return result;
    }

    private interface RequestExecutor {
        String execute();
    }

    public String deleteTask(String url)
    {
        int cont = 0;
        String res = okDeleteTask(url);
        while ((cont < 5) && (res.equals("error.IOException")))
        {
            ++cont;
            res = okDeleteTask(url);
        }
        return res;
    }

    private String okDeleteTask(String url)
    {
        try
        {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .delete()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful())
            {
                return "error.OKHttp";
            }
            else
            {
                return response.body().string();
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
            return "error.IOException";
        }
    }
}
