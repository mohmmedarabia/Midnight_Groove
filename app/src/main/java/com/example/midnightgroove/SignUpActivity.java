package com.example.midnightgroove; // تأكد أن هذا السطر يطابق اسم الباكج الحقيقي عندك فوق

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.midnightgroove.R;

public class SignUpActivity extends AppCompatActivity {

    private EditText etRegisterName, etRegisterEmail, etRegisterPassword;
    private Button btnSignUp;
    private TextView tvGoToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // ربط العناصر بكود الـ XML
        etRegisterName = findViewById(R.id.etRegisterName);
        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        etRegisterPassword = findViewById(R.id.etRegisterPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);

        // كبسة إنشاء الحساب
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etRegisterName.getText().toString().trim();
                String email = etRegisterEmail.getText().toString().trim();
                String password = etRegisterPassword.getText().toString().trim();

                if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignUpActivity.this, "Account Created Successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // بيرجع تلقائياً لشاشة اللوج إن
                }
            }
        });

        // كبسة العودة للوج إن لو عنده حساب أصلاً
        tvGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // بيغلق الشاشة الحالية وبرجع للي قبلها
            }
        });
    }
}