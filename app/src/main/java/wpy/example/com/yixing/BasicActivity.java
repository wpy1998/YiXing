package wpy.example.com.yixing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

public class BasicActivity extends AppCompatActivity{
    public boolean isFragmentsAction = false;
    public int fragmentNumber = 0;
    public String fragmentName = null;
    public String intentActionFront = "wpy.example.com.yixing.BasicActivity.front",
            intentActionNext = "wpy.example.com.yixing.BasicActivity.next";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar.isShowing()){
            actionBar.hide();
        }
    }

    @Override
    public void finish() {
        if (isFragmentsAction){
            if (fragmentNumber == 0){
                super.finish();
            }else {
                Intent intent = new Intent(intentActionFront);
                sendBroadcast(intent);
            }
        }else super.finish();
    }
}
