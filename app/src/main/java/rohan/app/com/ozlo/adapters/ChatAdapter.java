package rohan.app.com.ozlo.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;
import flepsik.github.com.progress_ring.ProgressRingView;
import rohan.app.com.ozlo.R;
import rohan.app.com.ozlo.activities.ChatFragment;
import rohan.app.com.ozlo.helpers.Constant;
import rohan.app.com.ozlo.models.Bot;
import rohan.app.com.ozlo.models.Choice;
import rohan.app.com.ozlo.models.Condition;
import rohan.app.com.ozlo.models.Evidence;
import rohan.app.com.ozlo.models.Human;
import rohan.app.com.ozlo.models.Item;
import rohan.app.com.ozlo.models.Mention;
import rohan.app.com.ozlo.models.Text;
import rohan.app.com.ozlo.models.Weather;

import static android.content.ContentValues.TAG;

/**
 * Created by rohan on 10-03-2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_BOT = 0;
    private static final int TYPE_ME = 1;
    private static final int TYPE_LOAD = 2;
    private static final int TYPE_FOOTER = 3;
    private static final int TYPE_REPLY = 4;
    private static final int TYPE_WEATHER_CARD = 5;
    private static final int TYPE_CONDITIONS = 6;

    private LayoutInflater inflater;
    private Context c;
    private int type;
    private String content;
    private List<Object> listItem = new ArrayList<>();
    private List<Choice> optionItem = new ArrayList<>();
    //private List<Condition> conditionItem = new ArrayList<>();
    private Set<Condition> conditionItem = new HashSet<>();
    private Item selectedItem;
    ReplyHolder vh;
    ChatFragment f;
    private ConditionHolder vhCond;

    public ChatAdapter(Context c, Item selectedItem, List<Object> listItem, Set<Condition> conditionItem, ChatFragment f) {
        inflater = LayoutInflater.from(c);
        this.c = c;
        this.selectedItem = selectedItem;
        this.conditionItem = conditionItem;
        this.listItem = listItem;
        this.f = f;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_BOT) {
            View view = inflater.inflate(R.layout.bot_layout, parent, false);
            return new BotHolder(view);
        } else if (viewType == TYPE_ME) {
            View view = inflater.inflate(R.layout.me_layout, parent, false);
            return new HumanHolder(view);
        } else if (viewType == TYPE_LOAD) {
            View view = inflater.inflate(R.layout.loader_indicator, parent, false);
            return new LoaderHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = inflater.inflate(R.layout.chat_footer_layout, parent, false);
            return new FooterHolder(view);
        } else if (viewType == TYPE_WEATHER_CARD) {
            View view = inflater.inflate(R.layout.weather_detail_card, parent, false);
            return new WeatherDetailHolder(view);
        } else if (viewType == TYPE_REPLY) {
            View view = inflater.inflate(R.layout.chat_reply_layout, parent, false);
            return new ReplyHolder(view);
        } else if (viewType == TYPE_CONDITIONS) {
            View view = inflater.inflate(R.layout.conditions_list, parent, false);
            return new ConditionHolder(view);
        } else {
            throw new RuntimeException("Unknown type");
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == listItem.size()) {
            return TYPE_FOOTER;
        }
        if (listItem.get(position) instanceof Human) {
            return TYPE_ME;
        } else if (listItem.get(position) instanceof Bot) {
            return TYPE_BOT;
        } else if (listItem.get(position) instanceof String) {
            return TYPE_LOAD;
        } else if (listItem.get(position) instanceof Weather) {
            return TYPE_WEATHER_CARD;
        } else if (listItem.get(position) instanceof Integer) {
            return TYPE_REPLY;
        } else if (listItem.get(position) instanceof Condition) {
            return TYPE_CONDITIONS;
        }
        return -1;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_ME:
                HumanHolder meHolder = (HumanHolder) holder;
                configureViewHolderHuman(meHolder, position);
                break;
            case TYPE_BOT:
                BotHolder botHolder = (BotHolder) holder;
                configureViewHolderBot(botHolder, position);
                break;
            case TYPE_FOOTER:
                FooterHolder footHolder = (FooterHolder) holder;
                break;
            case TYPE_LOAD:
                LoaderHolder loadHolder = (LoaderHolder) holder;
                loadingDots(loadHolder);
                break;
            case TYPE_WEATHER_CARD:
                WeatherDetailHolder weatherDetailHolder = (WeatherDetailHolder) holder;
                configureViewHolderWeather(weatherDetailHolder, position);
                break;
            case TYPE_REPLY:
                vh = (ReplyHolder) holder;
                configureReplyHolder();
                break;
            case TYPE_CONDITIONS:
                vhCond = (ConditionHolder) holder;
                configureConditionHolder();
                break;
            default:
                break;
        }
    }

    private void configureConditionHolder() {
//        for (int i = 0; i < conditionItem.size(); i++) {
        for(Condition condition : conditionItem) {
            LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.conditions_item, null);

            final ProgressRingView progressView = (ProgressRingView) v.findViewById(R.id.progress_view);
            final TextView progressText = (TextView) v.findViewById(R.id.progress_percentage);
            final TextView diseaseName = (TextView) v.findViewById(R.id.disease_name);

            double progress = condition.getProbability();
            float progFloat = (float) (progress * 100);
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(1);

            progressView.setProgress((float) progress);
            progressText.setText(df.format(progFloat) + "%");
            diseaseName.setText(condition.getName());

            vhCond.suggestionList.addView(v);

        }
        conditionItem.clear();
        conditionItem.removeAll(conditionItem);
    }

    private void configureReplyHolder() {
        optionItem = selectedItem.getChoices();
        Log.e(TAG, selectedItem.getId());
        for (int i = 0; i < optionItem.size(); i++) {
            LayoutInflater vi = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.quick_replies_layout, null);
            final AppCompatButton btn = (AppCompatButton) v.findViewById(R.id.add_sym_button);
            btn.setText(optionItem.get(i).getLabel());
            final TextView tv = (TextView) v.findViewById(R.id.dummy_text);
            Log.e("OPTIONS", optionItem.get(i).getLabel() + " " + i);
            tv.setText(optionItem.get(i).getId());
            vh.suggestionList.addView(v);
            //final CardView sParent = (CardView) v.findViewById(R.id.card_view_outer);
            final int finalI = i;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listItem.get(listItem.size() - 3) instanceof Condition)
                        removeAt(listItem.size() - 3);
                    removeAt(listItem.size() - 1);

                    conditionItem.clear();
                    conditionItem.removeAll(conditionItem);

                    Evidence evidence = new Evidence();
                    evidence.setChoiceId(String.valueOf(tv.getText()));
                    evidence.setId(selectedItem.getId());
                    f.contDiagnosing(evidence);
                    optionItem.clear();
                    f.add(btn.getText().toString());
                    vh.suggestionList.removeAllViews();
                    vhCond.suggestionList.removeAllViews();

                }
            });
        }
        optionItem.clear();
        optionItem.removeAll(optionItem);
    }

    public void deleteSuggestions() {
        if (listItem.size() == 0 || vh == null) return;
        removeAt(listItem.size()-1);
        vh.suggestionList.removeAllViews();
        optionItem.clear();
    }

    private void configureViewHolderWeather(WeatherDetailHolder weatherDetailHolder, int position) {
        final Weather weather = (Weather) listItem.get(position);
        String weatherDetails = "";

        if (weather.getMain().getTemp() < 10)
            weatherDetailHolder.parentLayout.setBackground(ContextCompat.getDrawable(c, R.drawable.bg_gradient_cold));
        else if (weather.getMain().getTemp() > 10 && weather.getMain().getTemp() < 21)
            weatherDetailHolder.parentLayout.setBackground(ContextCompat.getDrawable(c, R.drawable.bg_gradient_pleasant));
        else if (weather.getMain().getTemp() > 20 && weather.getMain().getTemp() < 31)
            weatherDetailHolder.parentLayout.setBackground(ContextCompat.getDrawable(c, R.drawable.bg_gradient_sunny));
        else if (weather.getMain().getTemp() > 30 && weather.getMain().getTemp() < 50)
            weatherDetailHolder.parentLayout.setBackground(ContextCompat.getDrawable(c, R.drawable.bg_gradient_hot));
        else
            weatherDetailHolder.parentLayout.setBackground(ContextCompat.getDrawable(c, R.drawable.bg_gradient_cprimary));

        int weatherCond = weather.getWeather().get(0).getId();
        //Toast.makeText(c, weatherCond, Toast.LENGTH_SHORT).show();
        Log.e(TAG, String.valueOf(weather.getWeather().get(0).getId()));

        if (weather.getWeather().get(0).getId() > Constant.THUNDERSTORM && weather.getWeather().get(0).getId() < Constant.THUNDERSTORM + 50) {
            Picasso.with(c).load(Constant.ART_STORM)
                    .into(weatherDetailHolder.descImage);
        } else if (weather.getWeather().get(0).getId() == Constant.CLEAR_SKY) {
            Picasso.with(c).load(Constant.ART_CLEAR)
                    .into(weatherDetailHolder.descImage);
        } else if (weather.getWeather().get(0).getId() > Constant.SHOWER_RAIN && weather.getWeather().get(0).getId() < Constant.SHOWER_RAIN + 50) {
            Picasso.with(c).load(Constant.ART_LIGHT_RAIN)
                    .into(weatherDetailHolder.descImage);
        } else if (weather.getWeather().get(0).getId() > Constant.SNOW && weather.getWeather().get(0).getId() < Constant.SNOW + 50) {
            Picasso.with(c).load(Constant.ART_SNOW)
                    .into(weatherDetailHolder.descImage);
        } else if (weather.getWeather().get(0).getId() > Constant.RAIN && weather.getWeather().get(0).getId() < Constant.RAIN + 50) {
            Picasso.with(c).load(Constant.ART_RAIN)
                    .into(weatherDetailHolder.descImage);
        } else if (weather.getWeather().get(0).getId() == Constant.FEW_CLOUDS) {
            Picasso.with(c).load(Constant.ART_LIGHT_CLOUDS)
                    .into(weatherDetailHolder.descImage);
        } else if (weather.getWeather().get(0).getId() == Constant.BROKEN_CLOUDS) {
            Picasso.with(c).load(Constant.ART_BROKEN_CLOUDS)
                    .into(weatherDetailHolder.descImage);
        } else if (weather.getWeather().get(0).getId() == Constant.SCATTERED_CLOUD) {
            Picasso.with(c).load(Constant.ART_BROKEN_CLOUDS)
                    .into(weatherDetailHolder.descImage);
        } else if (weather.getWeather().get(0).getId() == Constant.HAZE) {
            Picasso.with(c).load(Constant.ART_HAZE)
                    .into(weatherDetailHolder.descImage);
        } else
            Picasso.with(c).load(Constant.ART_CLEAR)
                    .into(weatherDetailHolder.descImage);

        if (weather.getWeather().get(0).getId() >= Constant.CLEAR_SKY)
                weatherDetails = "The Sky seems pretty clear to me.";
        else if (weather.getWeather().get(0).getId() == Constant.FEW_CLOUDS)
                weatherDetails = "I see a few clouds here and there.";
        else if (weather.getWeather().get(0).getId() >= Constant.SCATTERED_CLOUD)
                weatherDetails = "Broken and scattered clouds all over the big blue sky";
        else if (weather.getWeather().get(0).getId() >= Constant.SHOWER_RAIN)
            weatherDetails = "It might rain today. Better keep an umbrella.";
        else if (weather.getWeather().get(0).getId() >= Constant.RAIN)
            weatherDetails = "Heads up! Heavy rain's comin' up.";
        else if (weather.getWeather().get(0).getId() >= Constant.THUNDERSTORM)
            weatherDetails = "Woh! THUNDERSTORMS. Wrath of Zeus is upon us.";
        else if (weather.getWeather().get(0).getId() >= Constant.SNOW)
            weatherDetails = "It's time to throw snowball at people.";
        else if (weather.getWeather().get(0).getId() >= 700)
            weatherDetails = "Beware of "+weather.getWeather().get(0).getDescription();
        else if (weather.getWeather().get(0).getId() >= 900)
            weatherDetails = "Something big is coming.";
        else
            weatherDetails = "I DON'T KNOW!!!";

        weatherDetailHolder.tempMain.setText(weather.getMain().getTemp().toString());
        weatherDetailHolder.tempMin.setText(weather.getMain().getTempMin()+"°C");
        weatherDetailHolder.tempMax.setText(weather.getMain().getTempMax()+"°C");
        weatherDetailHolder.descDetail.setText(weatherDetails);
    }

    private void loadingDots(LoaderHolder vh) {
        //loadingView.setVisibility(View.VISIBLE);
        AlphaAnimation animation1 = new AlphaAnimation(0.2f, 0.8f);
        animation1.setDuration(600);
        animation1.setRepeatCount(Animation.INFINITE);
        animation1.setRepeatMode(Animation.RESTART);
        vh.firstDot.startAnimation(animation1);

        animation1 = new AlphaAnimation(0.2f, 0.8f);
        animation1.setDuration(600);
        animation1.setStartOffset(100);
        animation1.setRepeatCount(Animation.INFINITE);
        animation1.setRepeatMode(Animation.RESTART);
        vh.secondDot.startAnimation(animation1);

        animation1 = new AlphaAnimation(0.2f, 0.8f);
        animation1.setDuration(600);
        animation1.setStartOffset(200);
        animation1.setRepeatCount(Animation.INFINITE);
        animation1.setRepeatMode(Animation.RESTART);
        vh.thirdDot.startAnimation(animation1);
    }

    private void configureViewHolderBot(BotHolder botHolder, int position) {
        final Bot user = (Bot) listItem.get(position);
        botHolder.mInput.setText(user.getInput());
    }

    private void configureViewHolderHuman(HumanHolder meHolder, int position) {
        Human user = (Human) listItem.get(position);
        meHolder.mInput.setText(user.getInput());
        meHolder.mInput.setVisibility(View.VISIBLE);
    }

    public void removeAt(int position) {
        listItem.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listItem.size());
    }

    @Override
    public int getItemCount() {
        if (listItem == null) {
            return 0;
        }
        if (listItem.size() == 0) {
            return 1;
        }
        return listItem.size() + 1;
    }

    class BotHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.bot_text)
        TextView mInput;
        //  ImageView mIcon;
        @Bind(R.id.card_view_outer)
        RelativeLayout mCard;
//        @Bind(R.id.indicators)
//        LinearLayout dots;

        BotHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class HumanHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.me_text)
        TextView mInput;
        @Bind(R.id.card_view_outer)
        RelativeLayout mCard;
        @Bind(R.id.message_status)
        ImageView messageStatus;

        HumanHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

//    class ReplyHolder extends RecyclerView.ViewHolder {
//        @Bind(R.id.root_wrapper)
//        LinearLayout suggestionList;
//
//        ReplyHolder(View view) {
//            super(view);
//            ButterKnife.bind(this, view);
//        }
//    }

    class FooterHolder extends RecyclerView.ViewHolder {
        FooterHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class LoaderHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.circle_one)
        ImageView firstDot;
        @Bind(R.id.circle_two)
        ImageView secondDot;
        @Bind(R.id.circle_three)
        ImageView thirdDot;

        LoaderHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class ReplyHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.root_wrapper)
        LinearLayout suggestionList;

        ReplyHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class ConditionHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.root_wrapper)
        LinearLayout suggestionList;

        ConditionHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public class WeatherDetailHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.parent_layout)
        LinearLayout parentLayout;
        @Bind(R.id.temp_max)
        TextView tempMax;
        @Bind(R.id.temp_min)
        TextView tempMin;
        @Bind(R.id.temp_main)
        TextView tempMain;
        @Bind(R.id.desc_detail)
        TextView descDetail;
        @Bind(R.id.desc_image)
        ImageView descImage;

        public WeatherDetailHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
