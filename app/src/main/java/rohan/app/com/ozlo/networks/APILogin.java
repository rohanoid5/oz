package rohan.app.com.ozlo.networks;

import android.text.Editable;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import rohan.app.com.ozlo.models.AccessToken;
import rohan.app.com.ozlo.models.Registration;
import rohan.app.com.ozlo.models.User;

/**
 * Created by rohan on 08-03-2017.
 */

public interface APILogin {

    @FormUrlEncoded
    @POST("/users/authenticate")
    Call<AccessToken> userLogin(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("/users/register")
    Call<Registration> userRegister(@Field("name") String name, @Field("email") String email,
                                    @Field("username") String userName, @Field("password") String password);


}
