package wpy.example.com.fragmentscore;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import wpy.example.com.basicmodel.DownFile;
import wpy.example.com.basicmodel.DownFile2;
import wpy.example.com.basicmodel.OkhttpAction;
import wpy.example.com.basicmodel.RecommendImage;

@SuppressLint("ValidFragment")
public class FragmentEnd extends Fragment{
    Button reSubmit;
    TextView end;
    ImageView topI, downI;
    LinearLayout topRecommend, downRecommend;
    String intentActionTop = "wpy.example.com.digit_image.Fragment.FragmentEnd.top",
            intentActionDown = "wpy.example.com.digit_image.Fragment.FragmentEnd.down",
            intentActionRecommend = "wpy.example.com.digit_image.Fragment.FragmentEnd.recommend";
    IntentFilter intentFilter;
    MyBroadcastReceiver myBroadcastReceiver;
    PictureBroadcastReceiver pictureBroadcastReceiver;
    Bitmap bitmapTop, bitmapDown;
    double score;
    String uuid;
    OkhttpAction okhttpAction;

    public FragmentEnd(String uuid, double score){
        this.score = score;
        this.uuid = uuid;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_end, container, false);
        reSubmit = view.findViewById(R.id.reSubmit);
        end = view.findViewById(R.id.end_message);
        topI = view.findViewById(R.id.end_top);
        downI = view.findViewById(R.id.end_down);
        topRecommend = view.findViewById(R.id.end_top_recommend);
        downRecommend = view.findViewById(R.id.end_down_recommend);

        initView();
        setAction();
        return view;
    }

    private void initView() {
        String end1 = null;
        if(score > 50){
            end1 = "您此套着装评分为" + (int)score + "分\n上衣朴素淡雅愈显高雅气质\n下身穿着时尚却显得更加潇洒\n";
        }else {
            end1 = "您此套着装评分为" + (int)score + "分\n上衣朴素淡雅合适\n";
        }
        end.setText(end1);

        myBroadcastReceiver = new MyBroadcastReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(intentActionDown);
        intentFilter.addAction(intentActionTop);
        intentFilter.addAction(intentActionRecommend);
        intentFilter.addAction("pictureArrive");
        pictureBroadcastReceiver = new PictureBroadcastReceiver();
        getActivity().registerReceiver(pictureBroadcastReceiver, intentFilter);
        getActivity().registerReceiver(myBroadcastReceiver, intentFilter);

        topRecommend.removeAllViews();
        downRecommend.removeAllViews();

        okhttpAction = new OkhttpAction(getContext());
        DownFile2 downFile2 = new DownFile2(getContext(), "/api/outimage/");
        downFile2.getImage(uuid, "top", intentActionTop);
    }

    public void setAction(){
        reSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        final Dialog dia = new Dialog(getContext());
        dia.setContentView(R.layout.dialog_recommend_image);
        final ImageView dialogImage = dia.findViewById(R.id.dialog_image);
        dia.setCanceledOnTouchOutside(true);
        Window w = dia.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.width = 800;
        lp.height = 1200;
        dia.onWindowAttributesChanged(lp);
        dialogImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dia.dismiss();
            }
        });

        topI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogImage.setImageBitmap(bitmapTop);
                dia.show();
            }
        });

        downI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogImage.setImageBitmap(bitmapDown);
                dia.show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(myBroadcastReceiver);
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(intentActionTop)){
                DownFile2 downFile2 = new DownFile2(getContext(), "/api/outimage/");
                downFile2.getImage(uuid, "down", intentActionDown);
                Bitmap bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/Digitimage/top.jpg");
                bitmapTop = bm;
                topI.setImageBitmap(bm);
            }else if (action.equals(intentActionDown)){
                okhttpAction.getRecommend(intentActionRecommend);
                Bitmap bm = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() +
                        "/Digitimage/down.jpg");
                bitmapDown = bm;
                downI.setImageBitmap(bm);
            }else if (action.equals(intentActionRecommend)){
                try {
                    String data = intent.getStringExtra("data");
                    JSONObject jsonObject = new JSONObject(data);
                    String tops = jsonObject.getString("top");
                    String bottoms = jsonObject.getString("bottom");
                    JSONArray topArray = new JSONArray(tops);
                    for (int i = 0; i < topArray.length(); i++){
                        String message = (String) topArray.get(i);
                        DownFile downFile = new DownFile(getContext(), "top");
                        downFile.nameO = message;
                        downFile.downImage("http://10.62.10.151:8080" + message);
                    }
                    JSONArray downArray = new JSONArray(bottoms);
                    for (int i = 0; i < downArray.length(); i++){
                        String message = (String) downArray.get(i);
                        DownFile downFile = new DownFile(getContext(), "down");
                        downFile.nameO = message;
                        downFile.downImage("http://10.62.10.151:8080" + message);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private class PictureBroadcastReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("pictureArrive")){
                byte[] bytes = intent.getByteArrayExtra("bitmap");
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                String type = intent.getStringExtra("type");
                if (type.equals("top")){
                    RecommendImage recommendImage = new RecommendImage(context);
                    recommendImage.setImageView(bitmap);
                    topRecommend.addView(recommendImage);
                }else {
                    RecommendImage recommendImage = new RecommendImage(context);
                    recommendImage.setImageView(bitmap);
                    downRecommend.addView(recommendImage);
                }
            }
        }
    }
}
