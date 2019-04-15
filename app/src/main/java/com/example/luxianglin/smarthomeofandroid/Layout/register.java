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
import com.example.luxianglin.smarthomeofandroid.Socket.RegisterService;

import java.io.IOException;

public class register extends AppCompatActivity {
    Button btn1;
    Button btn2;
    EditText t1;
    EditText t2;
    EditText t3;
    EditText t4;
    String username;
    String ip;
    RegisterService registerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        btn1 = (Button) findViewById(R.id.RegisterRegist);
        btn2 = (Button) findViewById(R.id.Cancel);
        t1 = (EditText) findViewById(R.id.RUserText);
        t2 = (EditText) findViewById(R.id.RPassText);
        t3 = (EditText) findViewById(R.id.SPassText);
        t4 = (EditText) findViewById(R.id.Tel);
        Intent intent = getIntent();
        Bundle data = intent.getExtras();
        username = data.getString("user");
        ip = data.getString("IP");
        t1.setText(username);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = t1.getText().toString();
                String pass = t2.getText().toString();
                String spass = t3.getText().toString();
                String tel = t4.getText().toString();
                if (pass.equals(spass)) {
                    registerService = new RegisterService(ip);
                    new Thread(registerService).start();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    registerService.sendMsg("RegisterData:" + username + "," + pass + "," + tel);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (RegisterService.result != null) {
                        if (RegisterService.result.equals("RegisterSucceed")) {
                            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                        } else if (RegisterService.result.equals("RegisterFailed")) {
                            Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                registerService.sendMsg("exit");
                try {
                    if (registerService.getsocket() != null) {
                        registerService.getsocket().close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                try {
//                    registerService.sendMsg("exit");
//                    Thread.sleep(1000);
//                    registerService.getWriter().close();
//                    registerService.getReader().close();
//                    registerService.getsocket().close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(register.this, login.class);
                startActivity(intent);
            }
        });
    }
}
