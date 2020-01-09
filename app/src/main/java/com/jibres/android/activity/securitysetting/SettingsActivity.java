package com.jibres.android.activity.securitysetting;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jibres.android.R;
import com.jibres.android.activity.language.LanguageActivity;
import com.jibres.android.activity.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.List;

import static com.jibres.android.activity.Constans.ON_CLICK_AC_LANGUAGE;
import static com.jibres.android.activity.Constans.ON_CLICK_PROFILE;


public class SettingsActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SettingAdapter adapter;
    List<SettingModel> item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        recyclerView = findViewById(R.id.recycler_view);
        item = new ArrayList<>();
        adapter = new SettingAdapter(item, this, this::onCliked);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        LinearLayoutManager sLayoutManager =
                new LinearLayoutManager(getApplicationContext(),RecyclerView.VERTICAL,false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        GetLanguage();
        recyclerView.setLayoutManager(sLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter.notifyDataSetChanged();
    }

    void GetLanguage() {

        item.add(new SettingModel(0,
                0,null,
                "App Setting",null,null,false));
        item.add(new SettingModel(ON_CLICK_AC_LANGUAGE,
                0,null,
                "Change Language",null,null,true));

        item.add(new SettingModel(ON_CLICK_PROFILE,0,
                "https://jibres.com/static/images/logo.png",
                "Javad Adib","98 919 519 1378","@JavadAdib",true));

        item.add(new SettingModel(0,
                0,null,
                "لیست فروشگاه‌ها",null,null,false));
        item.add(new SettingModel(0,0,
                "https://jibres.com/files/1/26-fd0b2a3f04aeaf73d419a9e677c40c88.jpg",
                "آهوی ایرانی",null,"آخرین فعالیت ۴ دقیقه قبل",true));
        item.add(new SettingModel(0,0,
                "https://upload.wikimedia.org/wikipedia/commons/thumb/3/30/Ic_add_circle_outline_48px.svg/1024px-Ic_add_circle_outline_48px.svg.png",
                "فروشگاه جدید",null,null,true));

        item.add(new SettingModel(0,
                R.drawable.ic_alarm,null,
                "اعلان‌ها",null,null,true));

        item.add(new SettingModel(0,
                R.drawable.ic_security,null,
                "حریم‌خصوصی و امنیت",null,null,true));

    }

    private void onCliked(int status) {
        Intent intents = null;
        switch (status){
            case 16:
                intents = new Intent(this, LanguageActivity.class);
                break;
            case 10:
                intents = new Intent(this, ProfileActivity.class);
                break;
        }
        if (intents!=null){
            intents.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intents);
        }
    }
}
