package rohan.app.com.ozlo.networks;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rohan.app.com.ozlo.models.Diagnosis;
import rohan.app.com.ozlo.models.DiagnosisReport;
import rohan.app.com.ozlo.models.LookUp;
import rohan.app.com.ozlo.models.Mention;
import rohan.app.com.ozlo.models.Parse;
import rohan.app.com.ozlo.models.Text;

/**
 * Created by rohan on 31-03-2017.
 */

public interface APIMedica {

    @POST("parse")
    Call<Parse> getMentions(@Body Text text);

    @GET("lookup")
    Call<List<LookUp>> getLookups(@Query("phrase") String phrase);

    @POST("diagnosis")
    Call<Diagnosis> postDiagnose(@Body DiagnosisReport diagnosisReport);

}
