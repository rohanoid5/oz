
package rohan.app.com.ozlo.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Chat {

    @SerializedName("forecast")
    @Expose
    private String forecast;
    @SerializedName("location")
    @Expose
    private Integer location;
    @SerializedName("rain")
    @Expose
    private Integer rain;
    @SerializedName("locVal")
    @Expose
    private String locVal;

    public String getForecast() {
        return forecast;
    }

    public void setForecast(String forecast) {
        this.forecast = forecast;
    }

    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

    public Integer getRain() {
        return rain;
    }

    public void setRain(Integer rain) {
        this.rain = rain;
    }

    public String getLocVal() {
        return locVal;
    }

    public void setLocVal(String locVal) {
        this.locVal = locVal;
    }

}
