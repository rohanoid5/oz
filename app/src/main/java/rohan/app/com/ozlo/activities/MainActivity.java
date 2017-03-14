package rohan.app.com.ozlo.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import rohan.app.com.ozlo.R;
import rohan.app.com.ozlo.models.Root;
import rohan.app.com.ozlo.networks.APIService;
import rohan.app.com.ozlo.networks.ServiceFactory;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private SharedPreferences sharedPreferences;
    @Bind(R.id.horizontal_tab)
    TabLayout tabLayout;
    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.content_frame)
    FrameLayout contentFrame;

    private int[] tabIcons = {
            R.drawable.ic_chat_bubble_white_24dp,
            R.drawable.ic_wb_sunny_white_24dp,
            R.drawable.ic_tag_faces_white_24dp
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.do_not_move, R.anim.do_not_move);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
//        getSupportActionBar().setElevation(0);

        sharedPreferences = getSharedPreferences(getString(R.string.MY_TOKEN), MODE_PRIVATE);
        String accessTokenJson = sharedPreferences.getString(getString(R.string.accessToken), null);
        if (accessTokenJson != null) {
            Log.e(TAG, accessTokenJson);
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            finish();
            startActivity(intent);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction ft = fragmentManager.beginTransaction();
        final ChatFragment fragment = new ChatFragment();
        ft.replace(R.id.content_frame, fragment).commitAllowingStateLoss();

        final APIService apiService = ServiceFactory.provideService("121212");
        Call<Root> getRoot = apiService.getRoot();
        getRoot.enqueue(new Callback<Root>() {
            @Override
            public void onResponse(Call<Root> call, Response<Root> response) {
                if (response.isSuccessful())
                    Log.e(TAG, response.body().getMessage());
                else
                    Toast.makeText(MainActivity.this, "Not Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<Root> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });

        //tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.black));
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ChatFragment(), "ONE");
        adapter.addFragment(new ChatFragment(), "TWO");
       adapter.addFragment(new ChatFragment(), "THREE");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
