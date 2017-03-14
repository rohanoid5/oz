package rohan.app.com.ozlo.networks;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import rohan.app.com.ozlo.models.Weather;

/**
 * Created by rohan on 12-03-2017.
 */

public interface APIWeather {

    @GET("weather")
    Call<Weather> getWeather(@Query("q") String location,
                             @Query("appid") String appId,
                             @Query("units") String units);

}
