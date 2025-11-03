package com.example.statisticsapisproject;

import com.google.gson.annotations.SerializedName;

public class Condition
{
    @SerializedName("title")
    private String title;
    @SerializedName("field_overall_advice_level")
    private String field_overall_advice_level;
    @SerializedName("field_last_update")
    private String field_last_update;
    @SerializedName("changed")
    private String changed;
    @SerializedName("field_url")
    private String field_url;

    public Condition(String title, String field_overall_advice_level, String field_last_update, String changed, String field_url)
    {
        this.title = title;
        this.field_overall_advice_level = field_overall_advice_level;
        this.field_last_update = field_last_update;
        this.changed = changed;
        this.field_url = field_url;
    }

    public String getTitle()
    {
        return title;
    }

    public String getField_overall_advice_level()
    {
        return field_overall_advice_level;
    }

    public String getField_last_update()
    {
        return field_last_update;
    }

    public String getChanged()
    {
        return changed;
    }

    public String getField_url()
    {
        return field_url;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setField_overall_advice_level(String field_overall_advice_level)
    {
        this.field_overall_advice_level = field_overall_advice_level;
    }

    public void setField_last_update(String field_last_update)
    {
        this.field_last_update = field_last_update;
    }

    public void setChanged(String changed)
    {
        this.changed = changed;
    }

    public void setField_url(String field_url)
    {
        this.field_url = field_url;
    }

    public String toString()
    {
        return "Condition:" +
                ",\n Advice='" + field_overall_advice_level + '\'' +
                "\n" +
                ",\n detailedAdvice'" + field_last_update + '\'' +
                "\n" +
                ",\n lastUpdated='" + changed + '\'' +
                "\n";
    }

}
