package com.example.midnightgroove; // تأكد أن هذا السطر يطابق اسم الباكج الحقيقي عندك فوق

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.midnightgroove.ForgotPasswordActivity;
import com.example.midnightgroove.MainActivity;
import com.example.midnightgroove.R;
import com.example.midnightgroove.SignUpActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword, tvGoToSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // ربط العناصر بكود الـ XML
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvGoToSignUp = findViewById(R.id.tvGoToSignUp);

        // 1. كبسة تسجيل الدخول (تنقلك للشاشة الرئيسية للموسيقى)
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // فحص سريع إذا الخانات فاضية
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                } else {
                    // الدخول للشاشة الرئيسية مباشرة
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // إغلاق شاشة اللوج إن عشان ما يرجع لها بالزر الخلفي
                }
            }
        });

        // 2. كبسة الانتقال لشاشة نسيت كلمة السر (Forgot Password)
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });

        // 3. كبسة الانتقال لشاشة إنشاء حساب جديد (Sign Up)
        tvGoToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }
}