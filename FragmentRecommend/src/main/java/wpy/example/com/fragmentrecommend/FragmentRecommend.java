package wpy.example.com.fragmentrecommend;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import wpy.example.com.basicmodel.FileOperation;
import wpy.example.com.basicmodel.OkhttpAction;

import static android.app.Activity.RESULT_OK;

public class FragmentRecommend extends Fragment{
    ImageView map, submit, rechoose;
    private static int RESULT_LOAD_IMAGE = 1;
    boolean isImage = false;
    String picturePath = "",
            intentActionRecommend = "wpy.example.com.digit_image.Fragment.FragmentRecommend.recommend";
    IntentFilter intentFilter;
    MyBroadcastReceiver myBroadcastReceiver;
    Uri uri = null;
    RadioButton top, down;
    String type1 = "null";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        map = view.findViewById(R.id.submit_imageView);
        rechoose = view.findViewById(R.id.submit_rechoose);
        submit = view.findViewById(R.id.submit_submit);
        top = view.findViewById(R.id.recommend_top);
        down = view.findViewById(R.id.recommend_down);

        intentFilter = new IntentFilter();
        myBroadcastReceiver = new MyBroadcastReceiver();
        intentFilter.addAction(intentActionRecommend);
        getActivity().registerReceiver(myBroadcastReceiver, intentFilter);

        setAction(view);
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

//获取返回的数据，这里是android自定义的Uri地址
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            //获取选择照片的数据视图
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            //从数据视图中获取已选择图片的路径
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);
            cursor.close();
            //将图片显示到界面上
            FileOperation fileOperation = new FileOperation(getContext());
            Bitmap bitmap = fileOperation.getimage(picturePath);
            if (bitmap != null){
                map.setImageBitmap(bitmap);
                isImage = true;
            }else {
                Toast.makeText(getContext(), "图片不合法", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(myBroadcastReceiver);
    }

    public void setAction(View view) {
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.setImageDrawable(null);
                isImage = false;
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //设定结果返回
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImage == false || picturePath.equals("")){
                    Toast.makeText(getContext(), "请选择图片", Toast.LENGTH_SHORT).show();
                }else if (type1.equals("null")){
                    Toast.makeText(getContext(), "请选择上传衣物类型", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getContext(), "图片正在解析，请稍等片刻...", Toast.LENGTH_SHORT).show();
                    OkhttpAction okhttpAction = new OkhttpAction(getContext());
                    okhttpAction.getRecommend(intentActionRecommend);
                }
                map.setImageDrawable(null);
            }
        });

        top.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type1 = "top";
            }
        });

        down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                type1 = "down";
            }
        });

        rechoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.setImageDrawable(null);
                isImage = false;
                picturePath = "";
            }
        });
    }

    private class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(intentActionRecommend)){
                String data = intent.getStringExtra("data");
                Intent intent1 = new Intent("wpy.example.com.yixing.BasicActivity.next");
                intent1.putExtra("type", type1);
                intent1.putExtra("data", data);
                getContext().sendBroadcast(intent1);
            }
        }
    }
}
