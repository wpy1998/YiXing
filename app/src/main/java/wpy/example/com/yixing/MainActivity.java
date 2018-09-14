package wpy.example.com.yixing;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import wpy.example.com.fragmentchange.FragmentChange;
import wpy.example.com.fragmentchange.FragmentResult;
import wpy.example.com.fragmentrecommend.FragmentRecommend;
import wpy.example.com.fragmentrecommend.FragmentShow;
import wpy.example.com.fragmentscore.FragmentEnd;
import wpy.example.com.fragmentscore.FragmentScore;

public class MainActivity extends BasicActivity {
    ImageView Score, Change, Recommend;
    FragmentChange fragmentChange;
    FragmentRecommend fragmentRecommend;
    FragmentScore fragmentScore;
    private String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.CAMERA};
    IntentFilter intentFilter;
    MyBroadcastReceiver myBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setAction();
    }

    private void setAction() {
        Score.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentScore = new FragmentScore();
                Score.setImageDrawable(null);
                Recommend.setImageDrawable(null);
                Change.setImageDrawable(null);
                Score.setImageResource(R.drawable.mainpage_lighting);
                Recommend.setImageResource(R.drawable.function);
                Change.setImageResource(R.drawable.person);
                replaceFragment(fragmentScore);
                fragmentName = "fragmentScore";
            }
        });

        Recommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentRecommend = new FragmentRecommend();
                Score.setImageDrawable(null);
                Recommend.setImageDrawable(null);
                Change.setImageDrawable(null);
                Score.setImageResource(R.drawable.mainpage);
                Recommend.setImageResource(R.drawable.function_lighting);
                Change.setImageResource(R.drawable.person);
                replaceFragment(fragmentRecommend);
                fragmentName = "fragmentRecommend";
            }
        });

        Change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentChange = new FragmentChange();
                Score.setImageDrawable(null);
                Recommend.setImageDrawable(null);
                Change.setImageDrawable(null);
                Score.setImageResource(R.drawable.mainpage);
                Recommend.setImageResource(R.drawable.function);
                Change.setImageResource(R.drawable.person_lighting);
                replaceFragment(fragmentChange);
                fragmentName = "fragmentChange";
            }
        });

        Score.performClick();
    }

    private void initView() {
        intentFilter = new IntentFilter();
        myBroadcastReceiver = new MyBroadcastReceiver();
        intentFilter.addAction(intentActionFront);
        intentFilter.addAction(intentActionNext);
        registerReceiver(myBroadcastReceiver, intentFilter);

        isFragmentsAction = true;
        ActivityCompat.requestPermissions(this, permissions, 1);
        Score = findViewById(R.id.main_score);
        Change = findViewById(R.id.main_change);
        Recommend = findViewById(R.id.main_recommend);
    }


    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.main_frameLayout, fragment);
        transaction.commit();
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(intentActionFront)){
                if (fragmentName.equals("fragmentScore")){
                    if (fragmentNumber == 1){
                        replaceFragment(fragmentScore);
                        fragmentNumber--;
                    }else if (fragmentNumber == 0){
                        finish();
                    }
                }else if (fragmentName.equals("fragmentRecommend")){
                    if (fragmentNumber == 1){
                        replaceFragment(fragmentRecommend);
                        fragmentNumber--;
                    }else if (fragmentNumber == 0){
                        finish();
                    }
                }else if (fragmentName.equals("fragmentChange")){
                    if (fragmentNumber == 1){
                        replaceFragment(fragmentChange);
                        fragmentNumber--;
                    }else if (fragmentNumber == 0){
                        finish();
                    }
                }
            }else if (action.equals(intentActionNext)){
                if (fragmentName.equals("fragmentScore")){
                    double score = intent.getDoubleExtra("score", 0);
                    String uuid = intent.getStringExtra("uuid");
                    replaceFragment(new FragmentEnd(uuid, score));
                    if (fragmentNumber == 0){
                        fragmentNumber++;
                    }else {
                    }
                }else if (fragmentName.equals("fragmentRecommend")){
                    String data = intent.getStringExtra("data");
                    String type = intent.getStringExtra("type");
                    replaceFragment(new FragmentShow(data, type));
                    if (fragmentNumber == 0){
                        fragmentNumber++;
                    }else {
                    }
                }else if (fragmentName.equals("fragmentChange")){
                    String url = intent.getStringExtra("url");
                    replaceFragment(new FragmentResult(url));
                    if (fragmentNumber == 0){
                        fragmentNumber++;
                    }else {
                    }
                }
            }
        }
    }
}
