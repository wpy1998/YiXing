package wpy.example.com.basicmodel;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class RecommendImage extends LinearLayout{
    Context context;
    ImageView imageView, dialogImage;
    Dialog dia;
    Bitmap hcBitmap;
    public RecommendImage(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    private void initView() {
        LayoutInflater.from(context).inflate(R.layout.item_image, this, true);
        imageView = findViewById(R.id.image_image);

        dia = new Dialog(context);
        dia.setContentView(R.layout.dialog_recommend_image);
        dialogImage = dia.findViewById(R.id.dialog_image);
        dia.setCanceledOnTouchOutside(true);
        Window w = dia.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.width = 800;
        lp.height = 1200;
        dia.onWindowAttributesChanged(lp);

        imageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogImage.setImageBitmap(hcBitmap);
                dia.show();
            }
        });

        dialogImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dia.dismiss();
            }
        });
    }

    public void setImageView(Bitmap bitmap){
        imageView.setImageBitmap(bitmap);
        hcBitmap = bitmap;
    }


}
