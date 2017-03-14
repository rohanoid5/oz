package rohan.app.com.ozlo.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
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
import rohan.app.com.ozlo.models.Registration;
import rohan.app.com.ozlo.networks.APILogin;
import rohan.app.com.ozlo.networks.LoginFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SignUpActivity extends AppCompatActivity {

    @Bind(R.id.link_signin)
    TextView linkSignIn;
    @Bind(R.id.input_email)
    EditText inputEmail;
    @Bind(R.id.input_password)
    EditText inputPassword;
    @Bind(R.id.input_name)
    EditText inputName;
    @Bind(R.id.input_username)
    EditText inputUsername;
    @Bind(R.id.sign_up_btn)
    AppCompatButton signUpButton;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        linkSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

        sharedPreferences = getSharedPreferences(getString(R.string.MY_TOKEN), MODE_PRIVATE);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(SignUpActivity.this, R.style.MyDialogBox);
                progressDialog.setIndeterminate(true);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Signing up...");
                progressDialog.show();
                final APILogin apiLogin = LoginFactory.provideService();
                Call<Registration> userRegister = apiLogin.userRegister(String.valueOf(inputName.getText()),
                        String.valueOf(inputEmail.getText()),
                        String.valueOf(inputUsername.getText()),
                        String.valueOf(inputPassword.getText()));
                userRegister.enqueue(new Callback<Registration>() {
                    @Override
                    public void onResponse(Call<Registration> call, Response<Registration> response) {
                        if (response.isSuccessful()) {
                            Call<AccessToken> userLogin = apiLogin.userLogin(String.valueOf(inputUsername.getText()),
                                    String.valueOf(inputPassword.getText()));
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
                                        Toast.makeText(SignUpActivity.this, "Not Success", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<AccessToken> call, Throwable t) {
                                    Toast.makeText(SignUpActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            });
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Registration> call, Throwable t) {
                        Toast.makeText(SignUpActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    public void onLoginSuccess() {
        Intent returnIntent = new Intent(SignUpActivity.this, MainActivity.class);
        setResult(Activity.RESULT_OK, returnIntent);
        startActivity(returnIntent);
        finish();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

}
