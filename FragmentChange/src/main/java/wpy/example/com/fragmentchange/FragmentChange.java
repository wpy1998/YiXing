package wpy.example.com.fragmentchange;

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

public class FragmentChange extends Fragment{
    ImageView map, submit, rechoose;
    private static int RESULT_LOAD_IMAGE = 1;
    private static int CAMREA_REQUEST_CODE = 2;
    boolean isImage = false;
    String picturePath = "",
            intentActionSendPicture = "wpy.example.com.fragmentchange.FragmentChange.sendPicture";
    IntentFilter intentFilter;
    MyBroadcastReceiver myBroadcastReceiver;
    Uri uri = null, photoUri;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change, container, false);
        map = view.findViewById(R.id.submit_imageView);
        rechoose = view.findViewById(R.id.submit_rechoose);
        submit = view.findViewById(R.id.submit_submit);

        intentFilter = new IntentFilter();
        myBroadcastReceiver = new MyBroadcastReceiver();
        intentFilter.addAction(intentActionSendPicture);
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
        }else if (requestCode == CAMREA_REQUEST_CODE && resultCode == RESULT_OK) {
            //判断返回的数据data是否为空，在三星s5、华为p7等机型上面有data为空的现象
            if (data != null && data.getData() != null) {
                uri = data.getData();
                if(data.hasExtra("data")){
                    Bitmap thunbnail = data.getParcelableExtra("data"); //获取照片的Bitmap对象，并设置
                    map.setImageBitmap(thunbnail);
                }
            }
            //如果data数据为空，就令uri==之前指定保存的照片的photoUri
            if(uri == null) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //设定结果返回
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(myBroadcastReceiver);
    }

    public void setAction(View view) {
        final Dialog dia = new Dialog(getContext());
        dia.setContentView(R.layout.dialog_choose);
        TextView camera, photo;
        camera = dia.findViewById(R.id.dialog_choose_camera);
        photo = dia.findViewById(R.id.dialog_choose_photo);
        dia.setCanceledOnTouchOutside(true);
        Window w = dia.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.width = 800;
        lp.height = 400;
        dia.onWindowAttributesChanged(lp);
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                //设定结果返回
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                dia.dismiss();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                SimpleDateFormat timeStampFormat =new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
                String filename =timeStampFormat.format(new Date());
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, filename);
                photoUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                startActivityForResult(intent, CAMREA_REQUEST_CODE);
                dia.dismiss();
            }
        });

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.setImageDrawable(null);
                isImage = false;
                dia.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isImage == false || picturePath.equals("")){
                    Toast.makeText(getContext(), "请选择图片", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "图片正在解析，请稍等片刻...", Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            FileOperation fileOperation = new FileOperation(getContext());
                            Bitmap bitmap = fileOperation.getimage(picturePath);
                            System.out.println(bitmap.getHeight() + ", " + bitmap.getWidth());
                            File dir = new File(fileOperation.projectDir);
                            if (!dir.exists()){
                                dir.mkdirs();
                            }
                            File file = new File(fileOperation.projectDir + "/" + "digitimage.jpg");
                            FileOutputStream fos;
                            try {
                                fos = new FileOutputStream(file);
                                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            OkhttpAction okhttpAction = new OkhttpAction(getContext());
                            okhttpAction.getChangeImage(fileOperation.projectDir + "/" + "digitimage.jpg",
                                    intentActionSendPicture);
                            picturePath = "";
                            isImage = false;
                        }
                    }).start();
                }
                map.setImageDrawable(null);
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
            if (action.equals(intentActionSendPicture)){
                String data = intent.getStringExtra("data");
                System.out.println(data);
                try{
                    JSONObject jsonObject = new JSONObject(data);
                    String message = jsonObject.getString("message");
                    String url = jsonObject.getString("res_url");
                    Intent intent1 = new Intent("wpy.example.com.yixing.BasicActivity.next");
                    intent1.putExtra("url", url);
                    context.sendBroadcast(intent1);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
