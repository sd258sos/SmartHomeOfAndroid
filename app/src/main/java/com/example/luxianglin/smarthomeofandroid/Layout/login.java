package com.example.luxianglin.smarthomeofandroid.Layout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.luxianglin.smarthomeofandroid.R;
import com.example.luxianglin.smarthomeofandroid.Socket.LoginService;
import com.example.luxianglin.smarthomeofandroid.Util.CustomDialog;

import java.io.IOException;

public class login extends AppCompatActivity {
    Button btn1;
    Button btn2;
    EditText t1;
    EditText t2;
    EditText t3;
    LoginService loginService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        btn1 = (Button) findViewById(R.id.Login);
        btn2 = (Button) findViewById(R.id.LoginRegister);
        t1 = (EditText) findViewById(R.id.UserText);
        t2 = (EditText) findViewById(R.id.PassText);
        t3 = (EditText) findViewById(R.id.IPText);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = t1.getText().toString();
                String password = t2.getText().toString();
                String ip = t3.getText().toString();


                if (ip != null && (!ip.equals(""))) {
                    loginService = new LoginService(ip);
                    new Thread(loginService).start();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    loginService.sendMsg("LoginData:" + username + "," + password);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (LoginService.result != null) {
                        if (LoginService.result.equals("LoginSucceed")) {
                            Intent SmartActivity = new Intent(login.this, MainActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("user", username);
                            bundle.putString("IP", ip);
                            SmartActivity.putExtras(bundle);
                            startActivity(SmartActivity);
                        } else if (LoginService.result.equals("LoginFailed")) {
                            Toast.makeText(getApplicationContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "服务器出现错误，请检查IP地址", Toast.LENGTH_SHORT).show();

                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    loginService.sendMsg("exit");
                    try {
                        if (loginService.getsocket() != null) {
                            loginService.getsocket().close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "IP地址不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = t1.getText().toString();
                String ip = t3.getText().toString();
                if (ip != null && (!ip.equals(""))) {
                    Intent RegisterActivity = new Intent(login.this, register.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("user", username);
                    bundle.putString("IP", ip);
                    RegisterActivity.putExtras(bundle);
                    startActivity(RegisterActivity);
                } else {
                    Toast.makeText(getApplicationContext(), "IP地址不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}



