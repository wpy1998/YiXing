package wpy.example.com.basicmodel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownFile2 {
    private Context context;
    public DownFile2(Context context, String url){
        this.context = context;
        this.uuidWeb = web + url;
    }
    private String web = "http://10.62.10.151:8080";
    public String uuidWeb;
    public void getImage(final String uuid, final String type, final String intentAction){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String dirWeb = uuidWeb + type + "/" + uuid;
                intentaction = intentAction;
                nameType = type;
                fileOperation  = new FileOperation(context);
                OkHttpClient client = new OkHttpClient.Builder().readTimeout(7, TimeUnit.SECONDS).build();
                Request request = new Request.Builder().url(dirWeb)
                        .get().build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        System.out.println("Fail");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Message message = handler.obtainMessage();
                        if (response.isSuccessful()) {
                            message.what = IS_SUCCESS;
                            message.obj = response.body().bytes();
                            handler.sendMessage(message);
                        } else {
                            handler.sendEmptyMessage(IS_FAIL);
                        }
                        Intent intent = new Intent(intentAction);
                        context.sendBroadcast(intent);
                    }
                });
            }
        }).start();
    }

    private static final int IS_SUCCESS = 1;
    private static final int IS_FAIL = 0;
    private String nameType = null;
    private String intentaction;
    FileOperation fileOperation;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IS_SUCCESS:
                    byte[] bytes = (byte[]) msg.obj;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    File dir = new File(fileOperation.projectDir);
                    if (!dir.exists()){
                        dir.mkdirs();
                    }
                    File file = new File(fileOperation.projectDir + "/" + nameType + ".jpg");
                    FileOutputStream fos;
                    try {
                        fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case IS_FAIL:
                    break;
                default:
                    break;
            }
        }
    };
}
