package rohan.app.com.ozlo.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rohan.app.com.ozlo.R;
import rohan.app.com.ozlo.models.AccessToken;
import rohan.app.com.ozlo.networks.APILogin;
import rohan.app.com.ozlo.networks.APIService;
import rohan.app.com.ozlo.networks.LoginFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class LoginActivity extends AppCompatActivity {

    @Bind(R.id.link_signup)
    TextView linkSignUp;
    @Bind(R.id.input_email)
    EditText inputEmail;
    @Bind(R.id.input_password)
    EditText inputPassword;
    @Bind(R.id.login_btn)
    AppCompatButton loginButton;

    String userName, password;
    public SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        sharedPreferences = getSharedPreferences(getString(R.string.MY_TOKEN), MODE_PRIVATE);

        linkSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        userName = String.valueOf(inputEmail.getText());
        password = String.valueOf(inputPassword.getText());

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.MyDialogBox);
                progressDialog.setIndeterminate(true);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Signing in...");
                progressDialog.show();

                final APILogin apiLogin = LoginFactory.provideService();
                Call<AccessToken> userLogin = apiLogin.userLogin(String.valueOf(inputEmail.getText()), String.valueOf(inputPassword.getText()));
                userLogin.enqueue(new Callback<AccessToken>() {
                    @Override
                    public void onResponse(Call<AccessToken> call, Response<AccessToken> response) {
                        progressDialog.dismiss();
                        if (response.isSuccessful()) {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(getString(R.string.accessToken), response.body().getToken());
                            editor.commit();
                            onLoginSuccess();
                        } else
                            Toast.makeText(LoginActivity.this, "Not Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<AccessToken> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });

            }
        });
    }

    public void onLoginSuccess() {
        Intent returnIntent = new Intent(LoginActivity.this, MainActivity.class);
        setResult(Activity.RESULT_OK, returnIntent);
        startActivity(returnIntent);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
