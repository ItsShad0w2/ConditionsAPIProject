package com.example.statisticsapisproject;

import static java.lang.Character.getType;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ConditionAPI implements CallBack
{
    private String conditionMet = "";
    private static final String fileName = "conditions.json";
    private APIService apiService;
    private final String baseUrl = "https://www.smartraveller.gov.au/";

    public void setReader()
    {
        Interceptor interceptor = chain ->
        {
            Request original = chain.request();
            Request request = original.newBuilder()
                    .header("User-Agent", "Android Statistics App/1.0")
                    .method(original.method(), original.body())
                    .build();
            return chain.proceed(request);
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .protocols(Arrays.asList(Protocol.HTTP_1_1))
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(APIService.class);
    }

    public void getConditions(String country, Context context, CallBack callBack) throws IOException
    {
        if(checkTimeStamp(context))
        {
            Log.d("APIDebug", "Processing the data..");
            apiService.getConditions().enqueue(new Callback<String>()
            {
                @Override
                public void onResponse(Call<String> call, Response<String> response)
                {
                    if (response.isSuccessful() && response.body() != null)
                    {
                        String conditions = response.body();

                        try
                        {
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<Condition>>() {}.getType();
                            List<Condition> conditionsList = gson.fromJson(conditions, listType);

                            if(conditionsList != null && !conditionsList.isEmpty())
                            {

                                Log.d("APIDebug ", "First condition was set "  + conditionsList);

                                writeData(conditions, context);

                                for(Condition condition : conditionsList)
                                {
                                    if(condition.getTitle() != null && condition.getTitle().equalsIgnoreCase(country))
                                    {
                                        conditionMet = gson.toJson(condition);
                                        break;
                                    }
                                }
                                callBack.onSuccess(conditionMet);
                            }
                            else
                            {
                                Log.d("APIDebug", "Conditions list is empty");
                            }
                        }
                        catch (FileNotFoundException e)
                        {
                            throw new RuntimeException(e);
                        }

                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t)
                {
                    Log.d("APIDebug", "Error because " + t.getMessage());
                    t.printStackTrace();
                    callBack.onFailure(t.getMessage());
                }
            });
        }
        else
        {
            Log.d("APIDebug", "Haven't gotten the data");
            try
            {
                List<Condition> conditions = readData(context);
                conditionMet = "";

                if(conditions != null)
                {
                    Log.d("APIDebug", "Condition was read");
                    Gson gson = new Gson();

                    for(Condition condition : conditions)
                    {
                        if(condition.getTitle() != null && condition.getTitle().equalsIgnoreCase(country))
                        {
                            conditionMet = gson.toJson(condition);
                            break;
                        }
                    }

                    callBack.onSuccess(conditionMet);
                }
                else
                {
                    callBack.onFailure("Country not found");
                }
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }

        }

    }

    public void writeData(String conditionsJson, Context context) throws FileNotFoundException
    {
        try
        {
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            OutputStreamWriter outPutStreamWriter = new OutputStreamWriter(fileOutputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outPutStreamWriter);

            outPutStreamWriter.write(conditionsJson);
            bufferedWriter.close();
            outPutStreamWriter.close();
            fileOutputStream.close();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean checkTimeStamp(Context context)
    {
        File file = context.getFileStreamPath(fileName);

        if(file.exists())
        {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            String lastModifiedDate = dateFormat.format(new Date(file.lastModified()));
            String currentDate = dateFormat.format(new Date(System.currentTimeMillis()));

            return !lastModifiedDate.equals(currentDate);
        }

        return true;
    }

    public List<Condition> readData(Context context) throws IOException
    {
        try
        {
            InputStream inputStream = context.openFileInput(fileName);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder stringBuilder = new StringBuilder();
            String line = bufferedReader.readLine();

            while (line != null)
            {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }

            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();

            String json = stringBuilder.toString();

            Gson gson = new Gson();
            Type listType = new TypeToken<List<Condition>>(){}.getType();

            return gson.fromJson(json, listType);
        }
        catch(FileNotFoundException e)
        {
            return null;
        }
    }


    @Override
    public void onSuccess(String conditions)
    {

    }

    @Override
    public void onFailure(String error)
    {

    }
}
