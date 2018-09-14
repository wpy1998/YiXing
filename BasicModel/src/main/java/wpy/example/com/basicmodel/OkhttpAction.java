package wpy.example.com.basicmodel;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static okhttp3.RequestBody.create;

public class OkhttpAction {
    Context context;
    public String web = "http://10.62.10.151:8080";
    public static final MediaType JSON= MediaType.parse("application/json; charset=utf-8");
    public OkhttpAction(Context context){
        this.context = context;
    }

    public void setPicture(final String filepath, final String intentAction){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient client = new OkHttpClient();
                    File file = new File(filepath);
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", file.getName(),
                                    RequestBody.create(MediaType.parse("image/jpeg"), file))
                            .build();

                    Request request = new Request.Builder().url(web + "/api/score").post(requestBody).build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String action = response.body().string();
                            System.out.println(action);
                            Intent intent = new Intent(intentAction);
                            intent.putExtra("data", action);
                            context.sendBroadcast(intent);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void getRecommend(final String intentAction){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(web + "/api/recommand")
                            .build();
                    Response response = client.newCall(request).execute();
                    if(response.isSuccessful())
                    {
                        //获取要访问资源的byte数组
                        String action = response.body().string();
                        Intent intent = new Intent(intentAction);
                        intent.putExtra("data", action);
                        System.out.println(action);
                        context.sendBroadcast(intent);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void getChangeImage(final String path, final String intentAction){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = "http://10.51.155.252:8080/api/star_swap";
                    OkHttpClient client = new OkHttpClient();
                    File file = new File(path);
                    RequestBody requestBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", file.getName(),
                                    RequestBody.create(MediaType.parse("image/jpeg"), file))
                            .build();

                    Request request = new Request.Builder().url(url).post(requestBody).build();
                    Call call = client.newCall(request);
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Toast.makeText(context, "fail", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            String action = response.body().string();
                            System.out.println(action);
                            Intent intent = new Intent(intentAction);
                            intent.putExtra("data", action);
                            context.sendBroadcast(intent);
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
