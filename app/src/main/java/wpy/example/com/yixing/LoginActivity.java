package wpy.example.com.yixing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BasicActivity {
    TextView login, register;
    EditText account, password;
    String account1, password1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.buttonLogin);
        register = findViewById(R.id.buttonRegister);
        account = findViewById(R.id.account);
        password = findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account1 = account.getText().toString();
                password1 = password.getText().toString();
                if (account1.equals("") || password1.equals("")){
                    Toast.makeText(LoginActivity.this, "请输入账户和密码", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
