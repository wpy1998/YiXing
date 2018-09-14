package wpy.example.com.yixing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends BasicActivity {
    EditText account, password, password2;
    TextView login, register;
    String acco, pass, pass2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        account = findViewById(R.id.register_account);
        password = findViewById(R.id.register_password);
        password2 = findViewById(R.id.register_password2);
        register = findViewById(R.id.register_Register);
        login = findViewById(R.id.register_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acco = account.getText().toString();
                pass = password.getText().toString();
                pass2 = password2.getText().toString();
                if (acco.equals("") || pass.equals("") || pass2.equals("")){
                    Toast.makeText(RegisterActivity.this, "请输入账户和密码", Toast.LENGTH_SHORT).show();
                }else if (!pass.equals(pass2)){
                    Toast.makeText(RegisterActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {acco = account.getText().toString();
                pass = password.getText().toString();
                pass2 = password2.getText().toString();
                if (acco.equals("") || pass.equals("") || pass2.equals("")){
                    Toast.makeText(RegisterActivity.this, "请输入账户和密码", Toast.LENGTH_SHORT).show();
                }else if (!pass.equals(pass2)){
                    Toast.makeText(RegisterActivity.this, "两次密码不一致", Toast.LENGTH_SHORT).show();
                }else {
                    finish();
                }
            }
        });
    }
}
