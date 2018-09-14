package wpy.example.com.fragmentrecommend;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import wpy.example.com.basicmodel.DownFile;
import wpy.example.com.basicmodel.RecommendImage;

@SuppressLint("ValidFragment")
public class FragmentShow extends Fragment{
    String data, type;
    TextView name;
    LinearLayout recommend;
    IntentFilter intentFilter;
    ImageView best;
    boolean isSet = false;
    PictureBroadcastReceiver pictureBroadcastReceiver;
    public FragmentShow(String data, String type){
        this.data = data;
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_show, container, false);
        name = view.findViewById(R.id.show_name);
        recommend = view.findViewById(R.id.show_recommend);
        best = view.findViewById(R.id.show_best);

        intentFilter = new IntentFilter();
        pictureBroadcastReceiver = new PictureBroadcastReceiver();
        intentFilter.addAction("pictureArrive");
        getActivity().registerReceiver(pictureBroadcastReceiver, intentFilter);

        if (type.equals("top")){
            name.setText("其他下装");
        }else {
            name.setText("其他上装");
        }

        try {
            JSONObject jsonObject = new JSONObject(data);
            String tops = jsonObject.getString("top");
            String bottoms = jsonObject.getString("bottom");
            JSONArray topArray = new JSONArray(tops);
            if (type.equals("down")){
                for (int i = 0; i < topArray.length(); i++){
                    String message = (String) topArray.get(i);
                    DownFile downFile = new DownFile(getContext(), "top");
                    downFile.nameO = message;
                    downFile.downImage("http://10.62.10.151:8080" + message);
                }
            }else {
                JSONArray downArray = new JSONArray(bottoms);
                for (int i = 0; i < downArray.length(); i++){
                    String message = (String) downArray.get(i);
                    DownFile downFile = new DownFile(getContext(), "down");
                    downFile.nameO = message;
                    downFile.downImage("http://10.62.10.151:8080" + message);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(pictureBroadcastReceiver);
    }

    private class PictureBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("pictureArrive")){
                byte[] bytes = intent.getByteArrayExtra("bitmap");
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                if (isSet == false){
                    isSet = true;
                    best.setImageBitmap(bitmap);
                }else {
                    RecommendImage recommendImage = new RecommendImage(getContext());
                    recommendImage.setImageView(bitmap);
                    recommend.addView(recommendImage);
                }
            }
        }
    }
}
