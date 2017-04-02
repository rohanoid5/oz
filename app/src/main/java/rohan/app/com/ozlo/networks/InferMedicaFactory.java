package rohan.app.com.ozlo.networks;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rohan.app.com.ozlo.helpers.Constant;

/**
 * Created by rohan on 31-03-2017.
 */

public class InferMedicaFactory {
    private static Retrofit retrofit = null;

    public static Retrofit buildService() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                okhttp3.Request original = chain.request();
                okhttp3.Request request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("app_id", Constant.INFER_MEDICA_APP_ID)
                        .header("app_key", Constant.INFER_MEDICA_APP_KEY)
                        .method(original.method(), original.body())
                        .build();

                return chain.proceed(request);
            }
        });

        OkHttpClient client = httpClient.build();
        retrofit = new Retrofit.Builder()
                .baseUrl(Constant.INFER_MEDICA_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit;
    }

    public static APIMedica provideService() {
        return buildService().create(APIMedica.class);
    }
}
