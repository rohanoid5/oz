package rohan.app.com.ozlo.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rohan.app.com.ozlo.R;
import rohan.app.com.ozlo.adapters.ChatAdapter;
import rohan.app.com.ozlo.helpers.Constant;
import rohan.app.com.ozlo.models.Bot;
import rohan.app.com.ozlo.models.Chat;
import rohan.app.com.ozlo.models.Human;
import rohan.app.com.ozlo.models.Root;
import rohan.app.com.ozlo.models.Weather;
import rohan.app.com.ozlo.networks.APIService;
import rohan.app.com.ozlo.networks.APIWeather;
import rohan.app.com.ozlo.networks.OpenWeatherFactory;
import rohan.app.com.ozlo.networks.ServiceFactory;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static org.xmlpull.v1.XmlPullParser.TEXT;

/**
 * Created by rohan on 09-03-2017.
 */

public class ChatFragment extends Fragment {

    String[] ozloGreets = new String[]{"Hey Buddy!", "How you doin'?", "My name is ozlo!"};

    @Bind(R.id.recycler_view)
    RecyclerView recyclerView;
    @Bind(R.id.answer_input)
    EditText answerInput;
    @Bind(R.id.submit_button)
    ImageButton submitButton;
    @Bind(R.id.parent_chat)
    RelativeLayout parentChat;
    @Bind(R.id.content)
    LinearLayout content;

    ArrayList<Object> items = new ArrayList<>();
    ChatAdapter mAdapter;
    int pos = 0;
    Call<Chat> getChatReply;
    APIWeather apiWeather;
    private String location = "Kolkata";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, rootView);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.MY_TOKEN), MODE_PRIVATE);
        String accessTokenJson = sharedPreferences.getString(getString(R.string.accessToken), null);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ChatAdapter(getActivity(), items);
        recyclerView.setAdapter(mAdapter);

        //getServer(0);

        content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = parentChat.getRootView().getHeight() - parentChat.getHeight();
                if (heightDiff > dpToPx(getActivity(), 200)) {
                    recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                }
            }
        });

        final APIService apiService = ServiceFactory.provideService(accessTokenJson);
        apiWeather = OpenWeatherFactory.provideService();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                items.add(new Human(answerInput.getText().toString(), TEXT));
                mAdapter.notifyItemInserted(items.size() - 1);
                items.add("s");
                mAdapter.notifyItemInserted(items.size() - 1);
                recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                getChatReply = apiService.getChatReply(answerInput.getText().toString());
                getChatReply.enqueue(new Callback<Chat>() {
                    @Override
                    public void onResponse(Call<Chat> call, Response<Chat> response) {
                        if (response.isSuccessful()) {
                            Chat res = response.body();
                            if (res.getForecast().equals(Constant.FORCAST)) {
                                if (res.getLocation() == 1)
                                    getWeather(res.getLocVal());
                                else
                                    getWeather(location);
                            }

                        } else
                            Toast.makeText(getActivity(), "Not Success", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(Call<Chat> call, Throwable t) {
                        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                answerInput.setText("");
            }
        });

        return rootView;
    }


    public float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics;
        if(context!=null) {
            metrics = context.getResources().getDisplayMetrics();
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
        }
        return 0;
    }

    private void getStarted(final int s) {
        if (s >= ozloGreets.length) return;
        items.add("s");
        mAdapter.notifyItemInserted(items.size() - 1);
        final APIService apiService = ServiceFactory.provideService("121212");
        Call<Root> getRoot = apiService.getRoot();
        getRoot.enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {
                if (response.isSuccessful()) {
                    mAdapter.removeAt(items.size() - 1);
                    int temp = s;
                    Log.e(TAG, String.valueOf(temp));
                    items.add(new Bot(ozloGreets[temp], TEXT));
                    mAdapter.notifyItemInserted(items.size() - 1);
                    if (temp < 4) {
                        temp = temp + 1;
                        getStarted(temp);
                    }
                } else
                    Toast.makeText(getActivity(), "Not Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Root> call, Throwable t) {
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getWeather(final String location) {
        final Call<Weather> getWeather = apiWeather.getWeather(location,
                Constant.WEATHER_API_KEY, Constant.METRIC);
        getWeather.enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                if (response.isSuccessful()) {
                    items.remove(items.size() - 1);
                    Weather weather = response.body();
                    items.add(new Bot("Here is the weather detail of "+location, TEXT));
                    mAdapter.notifyDataSetChanged();
                    items.add(weather);
                    mAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                } else {
                    items.remove(items.size() - 1);
                    items.add(new Bot("Sorry didn't get you.", TEXT));
                    mAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                }
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                items.remove(items.size() - 1);
                items.add(new Bot("Sorry didn't get you.", TEXT));
                mAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
            }
        });
    }

}
