package com.example.statisticsapisproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements CallBack
{
    ImageView levelView;
    TextView conditionView, urlView;
    EditText editTextCountry;
    Button buttonSearch;

    ConditionAPI conditionAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextCountry = findViewById(R.id.editTextCountry);
        conditionView = findViewById(R.id.conditionView);
        urlView = findViewById(R.id.urlView);
        buttonSearch = findViewById(R.id.buttonSearch);
        levelView = findViewById(R.id.levelView);

        conditionAPI = new ConditionAPI();
        conditionAPI.setReader();

        buttonSearch.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String country = editTextCountry.getText().toString();

                country = adjustments(country);

                try
                {
                    conditionAPI.getConditions(country, MainActivity.this, MainActivity.this);

                }
                catch (IOException e)
                {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void onSuccess(String conditions)
    {
        levelView.setVisibility(View.VISIBLE);
        Gson gson = new Gson();
        Condition condition = null;

        if(conditions != null && !conditions.isEmpty())
        {
            condition = gson.fromJson(conditions, Condition.class);

            condition.setField_last_update(corrections(condition.getField_last_update()));

            if(condition.getField_overall_advice_level().equals("Exercise a high degree of caution"))
            {
                levelView.setImageResource(R.drawable.level2);
            }
            else
            {
                if(condition.getField_overall_advice_level().equals("Reconsider your need to travel"))
                {
                    levelView.setImageResource(R.drawable.level3);
                }

                if(condition.getField_overall_advice_level().equals("Do not travel"))
                {
                    levelView.setImageResource(R.drawable.level4);
                }

                if(condition.getField_overall_advice_level().equals("Exercise normal safety precautions"))
                {
                    levelView.setImageResource(R.drawable.level1);
                }
            }

            conditionView.setText(condition.toString());
            urlView.setText("Url to conditions " + condition.getField_url());
        }
        else
        {
            if(editTextCountry.getText().toString().equalsIgnoreCase("Australia"))
            {
                levelView.setImageResource(R.drawable.level1);
                condition = new Condition(
                        "Australia",
                        "Exercise normal safety precautions",
                        "As last checked and reviewed, travelling in Australia is safe for all kinds of travellers. You may want to be alert for any kinds of protests that are occurring in the country due to potential unrest and violence if not being cautious. Take note that this is not from an official travel advisory source and might be not up to date. Please refer to the Url below for the current and official advice of the country.",
                        format(),
                "https://travel.gc.ca/destinations/australia");

                conditionView.setText(condition.toString());
                urlView.setText("Url to conditions: " +condition.getField_url());
            }
            else
            {
                conditionView.setText("No conditions found for this country or it doesn't exist. Make sure you're typing the country's name correctly.");
                urlView.setText("");
                levelView.setVisibility(View.INVISIBLE);
            }
        }
    }

    public String format()
    {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        return formatDate.format(new Date(currentTime));
    }
    @Override
    public void onFailure(String error)
    {
        conditionView.setText("An error as occured.");
    }

    public String adjustments(String country)
    {
        country = country.toLowerCase();
        if(country.equals("north korea"))
        {
            country = "North Korea (Democratic People&#039;s Republic of Korea)";
        }

        if(country.equals("south korea"))
        {
            country = "South Korea (Republic of Korea)";
        }

        if(country.equals("us") || country.equals("united states") || country.equals("usa"))
        {
            country = "United States of America";
        }

        if(country.equals("england") || country.equals("great britain") || country.equals("uk") || country.equals("britain"))
        {
            country = "United Kingdom";
        }

        return country;
    }

    public String corrections(String conditionUpdate)
    {
        return conditionUpdate.replace("&amp;**", "'");
    }
}