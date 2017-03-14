package rohan.app.com.ozlo.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import rohan.app.com.ozlo.R;
import rohan.app.com.ozlo.helpers.Constant;
import rohan.app.com.ozlo.models.Bot;
import rohan.app.com.ozlo.models.Chat;
import rohan.app.com.ozlo.models.Human;
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

    private LayoutInflater inflater;
    private Context c;
    private int type;
    private String content;
    private List<Object> listItem = new ArrayList<>();

    public ChatAdapter(Context c, List<Object> listItem) {
        inflater = LayoutInflater.from(c);
        this.c = c;
        //this.content = content;
        this.listItem = listItem;
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
//                vh = (ReplyHolder) holder;
//                configureReplyHolder();
                break;
            default:
                break;
        }
    }

    private void configureViewHolderWeather(WeatherDetailHolder weatherDetailHolder, int position) {
        final Weather weather = (Weather) listItem.get(position);

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

//        Picasso.with(c).load("https://raw.githubusercontent.com/rohanoid5/weatherArts/master/Archive/Flat/art_light_clouds.png")
//                .into(weatherDetailHolder.descImage);

        if (weather.getWeather().get(0).getId() > Constant.THUNDERSTORM && weather.getWeather().get(0).getId() < Constant.THUNDERSTORM + 50) {
            Picasso.with(c).load("https://raw.githubusercontent.com/rohanoid5/weatherArts/master/Archive/Flat/art_storm.png")
                    .into(weatherDetailHolder.descImage);
        } else if (weather.getWeather().get(0).getId() == Constant.CLEAR_SKY) {
            Picasso.with(c).load("https://raw.githubusercontent.com/rohanoid5/weatherArts/master/Archive/Flat/art_clear.png")
                    .into(weatherDetailHolder.descImage);
        } else if (weather.getWeather().get(0).getId() > Constant.SHOWER_RAIN && weather.getWeather().get(0).getId() < Constant.SHOWER_RAIN + 50) {
            Picasso.with(c).load("https://raw.githubusercontent.com/rohanoid5/weatherArts/master/Archive/Flat/art_light_rain.png")
                    .into(weatherDetailHolder.descImage);
        } else if (weather.getWeather().get(0).getId() > Constant.SNOW && weather.getWeather().get(0).getId() < Constant.SNOW + 50) {
            Picasso.with(c).load("https://raw.githubusercontent.com/rohanoid5/weatherArts/master/Archive/Flat/art_snow.png")
                    .into(weatherDetailHolder.descImage);
        } else if (weather.getWeather().get(0).getId() > Constant.RAIN && weather.getWeather().get(0).getId() < Constant.RAIN + 50) {
            Picasso.with(c).load("https://raw.githubusercontent.com/rohanoid5/weatherArts/master/Archive/Flat/art_rain.png")
                    .into(weatherDetailHolder.descImage);
        } else if (weather.getWeather().get(0).getId() == Constant.FEW_CLOUDS) {
            Picasso.with(c).load("https://raw.githubusercontent.com/rohanoid5/weatherArts/master/Archive/Flat/art_light_clouds.png")
                    .into(weatherDetailHolder.descImage);
        } else if (weather.getWeather().get(0).getId() == Constant.BROKEN_CLOUDS) {
            Picasso.with(c).load("https://raw.githubusercontent.com/rohanoid5/weatherArts/master/Archive/Flat/art_clouds.png")
                    .into(weatherDetailHolder.descImage);
        } else if (weather.getWeather().get(0).getId() == Constant.SCATTERED_CLOUD) {
            Picasso.with(c).load("https://raw.githubusercontent.com/rohanoid5/weatherArts/master/Archive/Flat/art_light_clouds.png")
                    .into(weatherDetailHolder.descImage);
        } else if (weather.getWeather().get(0).getId() == Constant.HAZE) {
            Picasso.with(c).load("https://raw.githubusercontent.com/rohanoid5/weatherArts/master/Archive/Flat/art_fog.png")
                    .into(weatherDetailHolder.descImage);
        }

        weatherDetailHolder.tempMain.setText(weather.getMain().getTemp().toString());
        weatherDetailHolder.tempMin.setText(weather.getMain().getTempMin()+"°C");
        weatherDetailHolder.tempMax.setText(weather.getMain().getTempMax()+"°C");
        //weatherDetailHolder.descTitle.setText(weather.getWeather().get(0).getDescription());
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
        meHolder.mImage.setVisibility(View.GONE);
        meHolder.progressBar.setVisibility(View.GONE);
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
        CardView mCard;
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
        CardView mCard;
        @Bind(R.id.me_thumb_image)
        ImageView mImage;
        @Bind(R.id.loadingPanel)
        RelativeLayout loadingPanel;
        @Bind(R.id.progress_bar)
        ProgressBar progressBar;
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

    public class WeatherDetailHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.parent_layout)
        LinearLayout parentLayout;
        @Bind(R.id.temp_max)
        TextView tempMax;
        @Bind(R.id.temp_min)
        TextView tempMin;
        @Bind(R.id.temp_main)
        TextView tempMain;
//        @Bind(R.id.desc_title)
//        TextView descTitle;
        @Bind(R.id.desc_image)
        ImageView descImage;
        @Bind(R.id.rain_info)
        TextView rainInfo;

        public WeatherDetailHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
