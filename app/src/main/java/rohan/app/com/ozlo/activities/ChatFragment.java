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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rohan.app.com.ozlo.R;
import rohan.app.com.ozlo.adapters.ChatAdapter;
import rohan.app.com.ozlo.helpers.Constant;
import rohan.app.com.ozlo.helpers.PrefManager;
import rohan.app.com.ozlo.models.Bot;
import rohan.app.com.ozlo.models.Chat;
import rohan.app.com.ozlo.models.Choice;
import rohan.app.com.ozlo.models.Condition;
import rohan.app.com.ozlo.models.Diagnosis;
import rohan.app.com.ozlo.models.DiagnosisReport;
import rohan.app.com.ozlo.models.Evidence;
import rohan.app.com.ozlo.models.Human;
import rohan.app.com.ozlo.models.Item;
import rohan.app.com.ozlo.models.LookUp;
import rohan.app.com.ozlo.models.Mention;
import rohan.app.com.ozlo.models.Parse;
import rohan.app.com.ozlo.models.Root;
import rohan.app.com.ozlo.models.Text;
import rohan.app.com.ozlo.models.Weather;
import rohan.app.com.ozlo.networks.APIMedica;
import rohan.app.com.ozlo.networks.APIService;
import rohan.app.com.ozlo.networks.APIWeather;
import rohan.app.com.ozlo.networks.InferMedicaFactory;
import rohan.app.com.ozlo.networks.OpenWeatherFactory;
import rohan.app.com.ozlo.networks.ServiceFactory;

import static android.R.attr.max;
import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;
import static org.xmlpull.v1.XmlPullParser.TEXT;

/**
 * Created by rohan on 09-03-2017.
 */

public class ChatFragment extends Fragment {

    String[] ozloGreets = new String[]{"Hi", "My name is Ozee.", "I'm here to help you diagnose your possible diseases.",
    "Ask me anything like ", "'I a have headache.' or 'I have a pain in my back.'"};

    String[] ozloHello = new String[]{"Hi!", "Hey! What's up?", "How are you feeling today?",
            "Hi. Tell me about your symptoms.", "Ozee's here!"};

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

    private ArrayList<Object> items = new ArrayList<>();
    private List<Choice> optionItem = new ArrayList<>();
    private List<Evidence> evidenceItem = new ArrayList<>();
    private Set<Condition> conditionItem = new HashSet<>();
    private Item selectedItem = new Item();

    ChatAdapter mAdapter;
    Diagnosis res;
    String accessTokenJson;
    APIService apiService;
    int pos = 0;
    int j = 0;
    Call<Chat> getChatReply;
    APIWeather apiWeather;
    private String location = "Kolkata";
    private boolean isDingos = false;
    private DiagnosisReport diagnosisReport;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        diagnosisReport = new DiagnosisReport();
        diagnosisReport.setAge(20);
        diagnosisReport.setSex("male");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);
        ButterKnife.bind(this, rootView);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(getString(R.string.MY_TOKEN), MODE_PRIVATE);
        accessTokenJson = sharedPreferences.getString(getString(R.string.accessToken), null);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new ChatAdapter(getActivity(), selectedItem, items, conditionItem, ChatFragment.this);
        recyclerView.setAdapter(mAdapter);

        items.add(new Bot(ozloHello[3], TEXT));
        mAdapter.notifyItemInserted(items.size() - 1);
        recyclerView.smoothScrollToPosition(mAdapter.getItemCount());

        PrefManager prefManager = new PrefManager(getActivity(), PrefManager.PREF_NAME_GREETING);
        if (prefManager.isFirstTimeLaunch()) {
            prefManager.setFirstTimeLaunch(false);
            getStarted(0);
        }

        content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = parentChat.getRootView().getHeight() - parentChat.getHeight();
                if (heightDiff > dpToPx(getActivity(), 200)) {
                    recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                }
            }
        });

        apiService = ServiceFactory.provideService(accessTokenJson);
        apiWeather = OpenWeatherFactory.provideService();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                mAdapter.deleteSuggestions();
                optionItem.clear();
                evidenceItem.clear();
                items.add(new Human(answerInput.getText().toString(), TEXT));
                mAdapter.notifyItemInserted(items.size() - 1);
                items.add("s");
                mAdapter.notifyItemInserted(items.size() - 1);
                recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                String replyText = answerInput.getText().toString();
                getParseResponse(replyText);
                answerInput.setText("");
            }
        });

        return rootView;
    }

    public void witInteract(String text) {
        getChatReply = apiService.getChatReply(text);
        getChatReply.enqueue(new Callback<Chat>() {
            @Override
            public void onResponse(Call<Chat> call, Response<Chat> response) {
                if (items.get(items.size() - 1) instanceof String) {
                    items.remove(items.size() - 1);
                    mAdapter.notifyDataSetChanged();
                }
                if (response.isSuccessful()) {
                    Chat res = response.body();
                    if (res.getForecast().equals(Constant.CONTEXT_GREETINGS)) {

                        Random random = new Random();
                        int r = random.nextInt(ozloHello.length);

                        items.add(new Bot(ozloHello[r], TEXT));
                        mAdapter.notifyItemInserted(items.size() - 1);
                        recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                    }

                } else {
                    Toast.makeText(getActivity(), "Not Success", Toast.LENGTH_SHORT).show();
                    items.add(new Bot("Sorry didn't get you.", TEXT));
                    mAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                }
            }

            @Override
            public void onFailure(Call<Chat> call, Throwable t) {
                if (items.get(items.size() - 1) instanceof String) {
                    items.remove(items.size() - 1);
                    mAdapter.notifyDataSetChanged();
                }
                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                items.add(new Bot("Sorry didn't get you.", TEXT));
                mAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
            }
        });
    }

    public void getParseResponse(final String text) {
        Text text1 = new Text();
        text1.setText(text);
        final APIMedica apiMedica = InferMedicaFactory.provideService();
        Call<Parse> getMentions = apiMedica.getMentions(text1);
        getMentions.enqueue(new Callback<Parse>() {
            @Override
            public void onResponse(Call<Parse> call, Response<Parse> response) {
                if (response.isSuccessful()) {
                    Parse res = response.body();

                    if (res.getMentions().size() == 0) {
                        witInteract(text);
                        return;
                    }

                    if (items.get(items.size() - 1) instanceof String)
                        items.remove(items.size() - 1);
                    Mention mention = res.getMentions().get(0);
                    Evidence evidence = new Evidence();
                    evidence.setId(mention.getId());
                    evidence.setChoiceId(mention.getChoiceId());
                    evidenceItem.add(evidence);
                    getDiagnosed();
                } else {
                    if (items.get(items.size() - 1) instanceof String)
                        items.remove(items.size() - 1);
//                    optionItem.clear();
                    items.add(new Bot("Sorry didn't get you.", TEXT));
                    mAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                }
            }

            @Override
            public void onFailure(Call<Parse> call, Throwable t) {
                if (items.get(items.size() - 1) instanceof String)
                    items.remove(items.size() - 1);
//                optionItem.clear();
                items.add(new Bot("Sorry didn't get you.", TEXT));
                mAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
            }
        });
    }

    public float dpToPx(Context context, float valueInDp) {
        DisplayMetrics metrics;
        if (context != null) {
            metrics = context.getResources().getDisplayMetrics();
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
        }
        return 0;
    }

    public void getDiagnosed() {

        conditionItem.clear();
        final APIMedica apiMedica = InferMedicaFactory.provideService();
        diagnosisReport.setEvidence(evidenceItem);

        Log.e(TAG+"CHAT", diagnosisReport.getSex() + " " + diagnosisReport.getAge());
        for (Evidence e : diagnosisReport.getEvidence())
            Log.e(TAG+"CHAT", e.getId() + " " + e.getChoiceId());

        Call<Diagnosis> getDiagnosis = apiMedica.postDiagnose(diagnosisReport);
        getDiagnosis.enqueue(new Callback<Diagnosis>() {
            @Override
            public void onResponse(Call<Diagnosis> call, Response<Diagnosis> response) {
                if (response.isSuccessful()) {
                    if (items.get(items.size() - 1) instanceof String)
                        items.remove(items.size() - 1);
                    res = response.body();
                    String question = res.getQuestion().getText();
                    items.add(new Bot(question, TEXT));
                    mAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(mAdapter.getItemCount());

                    for (Condition condition : res.getConditions()) {
                        conditionItem.add(condition);
                    }
                    mAdapter.notifyDataSetChanged();
                    items.add(new Condition());
                    mAdapter.notifyItemInserted(items.size()-1);
                    recyclerView.smoothScrollToPosition(mAdapter.getItemCount());

                    fillOptions(0, res);

                } else {
                    Log.e(TAG, String.valueOf(response.errorBody()));
                    items.remove(items.size() - 1);
                    items.add(new Bot("Sorry didn't get you.", TEXT));
                    mAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
                }
            }

            @Override
            public void onFailure(Call<Diagnosis> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_LONG).show();
                items.remove(items.size() - 1);
                items.add(new Bot("Sorry didn't get you.", TEXT));
                mAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
            }
        });
    }

    void hideKeyboard() {
        View v = getActivity().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void fillOptions(int i, Diagnosis diagnosis) {

        if (i == diagnosis.getQuestion().getItems().size()) {
            j = 0;
            res = null;

            items.add("s");
            mAdapter.notifyItemInserted(items.size() - 1);
            recyclerView.smoothScrollToPosition(mAdapter.getItemCount());

            if (evidenceItem != null) getDiagnosed();
            return;
        }

        List<Item> qItem = diagnosis.getQuestion().getItems();
        items.add(new Bot(qItem.get(i).getName(), TEXT));
        mAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
        for (Choice choice : qItem.get(i).getChoices()) {
            optionItem.add(choice);
        }
        selectedItem.setId(diagnosis.getQuestion().getItems().get(i).getId());
        selectedItem.setChoices(optionItem);
        mAdapter.notifyDataSetChanged();
        items.add(1);
        mAdapter.notifyItemInserted(items.size()-1);
        recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
        j = i + 1;
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

    private void getWeather(String location) {
        if (location.equals("Mumbai") || location.equals("mumbai"))
            location = "Bombay";
        final Call<Weather> getWeather = apiWeather.getWeather(location,
                Constant.WEATHER_API_KEY, Constant.METRIC);
        final String finalLocation = location;
        getWeather.enqueue(new Callback<Weather>() {
            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                if (response.isSuccessful()) {
                    items.remove(items.size() - 1);
                    Weather weather = response.body();
                    items.add(new Bot("Here is the weather detail of " + finalLocation, TEXT));
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

    public void add(String s) {
        conditionItem.clear();
        items.add(new Human(s, TEXT));
        mAdapter.notifyItemInserted(items.size() - 1);
        recyclerView.smoothScrollToPosition(mAdapter.getItemCount());
        fillOptions(j, res);
    }

    public void contDiagnosing(Evidence evidence) {
        evidenceItem.add(evidence);
        diagnosisReport.setAge(20);
        diagnosisReport.setSex("male");
    }
}
