package wpy.example.com.fragmentchange;

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

import java.io.File;

import wpy.example.com.basicmodel.DownFile;
import wpy.example.com.basicmodel.DownFile2;
import wpy.example.com.basicmodel.FileOperation;
import wpy.example.com.basicmodel.OkhttpAction;
import wpy.example.com.basicmodel.RecommendImage;

@SuppressLint("ValidFragment")
public class FragmentResult extends Fragment{
    String url;
    ImageView imageView;
    Bitmap bitmap;
    PictureBroadcastReceiver pictureBroadcastReceiver;
    IntentFilter intentFilter;
    public FragmentResult(String url){
        this.url = url;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_result, container, false);

        imageView = view.findViewById(R.id.result_image);

        intentFilter = new IntentFilter();
        pictureBroadcastReceiver = new PictureBroadcastReceiver();
        intentFilter.addAction("pictureArrive");
        getActivity().registerReceiver(pictureBroadcastReceiver, intentFilter);

        OkhttpAction okhttpAction = new OkhttpAction(getContext());
        DownFile downFile = new DownFile(getContext(), "change");
        downFile.setNameO(url);
        downFile.downImage("http://10.51.155.252:8080" + url);
        System.out.println("http://10.51.155.252:8080" + url);

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
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
