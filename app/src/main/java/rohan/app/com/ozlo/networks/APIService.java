package rohan.app.com.ozlo.networks;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rohan.app.com.ozlo.models.Chat;
import rohan.app.com.ozlo.models.Root;

/**
 * Created by rohan on 06-12-2016.
 */

public interface APIService {

    @GET("/")
    Call<Root> getRoot();

    @FormUrlEncoded
    @POST("/chat")
    Call<Chat> getChatReply(@Field("message") String message);

}
