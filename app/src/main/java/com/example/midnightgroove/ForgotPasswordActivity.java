package com.example.midnightgroove; // تأكد أن هذا السطر يطابق اسم الباكج الحقيقي عندك فوق

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.midnightgroove.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etResetEmail;
    private Button btnResetPassword;
    private TextView tvBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // ربط العناصر بكود الـ XML
        etResetEmail = findViewById(R.id.etResetEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        tvBackToLogin = findViewById(R.id.tvBackToLogin);

        // كبسة إرسال رابط استعادة الباسورد
        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etResetEmail.getText().toString().trim();

                if (email.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ForgotPasswordActivity.this, "Reset link sent to " + email, Toast.LENGTH_SHORT).show();
                    finish(); // بيرجع للوج إن بعد الإرسال
                }
            }
        });

        // كبسة الرجوع للوج إن
        tvBackToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}