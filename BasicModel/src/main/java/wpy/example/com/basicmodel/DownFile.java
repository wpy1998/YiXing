package wpy.example.com.basicmodel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownFile {
    Context context;
    String type;
    public DownFile(Context context, String type){
        this.context = context;
        this.type = type;
    }
    private static final int IS_SUCCESS = 1;
    private static final int IS_FAIL = 0;
    public String nameO;
    private Bitmap bitmap;
    public void downImage(final String url){
        OkHttpClient client = new OkHttpClient.Builder().readTimeout(7, TimeUnit.SECONDS).build();
        Request request = new Request.Builder().url(url)
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
            }
        });
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case IS_SUCCESS:
                    byte[] bytes = (byte[]) msg.obj;
                    bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    Intent intent = new Intent("pictureArrive");
                    intent.putExtra("bitmap", bytes);
                    intent.putExtra("type", type);
                    context.sendBroadcast(intent);
                    FileOperation fileOperation = new FileOperation(context);
                    File dir = new File(fileOperation.projectDir + "/pictures");
                    if (!dir.exists()){
                        dir.mkdirs();
                    }
                    File file = null;
                    if (type.equals("top")){
                        file = new File(fileOperation.projectDir + "/pictures/" + getTopName(nameO));
                    }else if (type.equals("down")){
                        file = new File(fileOperation.projectDir + "/pictures/" + getDownName(nameO));
                    }else {
                        System.out.println(nameO);
                        file = new File(fileOperation.projectDir + "/pictures/" + getChangeName(nameO));
                    }
                    FileOutputStream fos;
                    try {
                        fos = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                        fos.close();
                    } catch (Exception e) {
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

    private String getTopName(String path){
        String end = path.substring(9);
        return end;
    }

    private String getDownName(String path){
        String end = path.substring(12);
        return end;
    }

    private String getChangeName(String path){
        String end = path.substring(9);
        return end;
    }

    public void setNameO(String nameO){
        this.nameO = nameO;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }
}
