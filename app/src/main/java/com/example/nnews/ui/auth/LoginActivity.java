package com.example.nnews.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nnews.MainActivity;
import com.example.nnews.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        binding.btnLogin.setOnClickListener(v -> handleLogin());

        binding.tvToRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });

        // Aksi klik Lupa Password
        binding.tvForgotPassword.setOnClickListener(v -> handleForgotPassword());
    }

    private void handleLogin() {
        String username = binding.etLoginUsername.getText().toString().trim();
        String password = binding.etLoginPassword.getText().toString().trim();

        // Reset error visual sebelumnya
        binding.tilLoginUsername.setError(null);
        binding.tilLoginPassword.setError(null);

        boolean isValid = true;

        if (TextUtils.isEmpty(username)) {
            binding.tilLoginUsername.setError("Email tidak boleh kosong");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            binding.tilLoginUsername.setError("Format email tidak valid");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            binding.tilLoginPassword.setError("Password tidak boleh kosong");
            isValid = false;
        }

        if (!isValid) return; // Hentikan proses jika ada yang tidak valid

        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    } else {
                        // Menampilkan pesan gagal yang lebih spesifik
                        Toast.makeText(LoginActivity.this, "Login Gagal. Periksa email dan password Anda.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void handleForgotPassword() {
        String email = binding.etLoginUsername.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            binding.tilLoginUsername.setError("Masukkan email Anda di sini untuk mereset password");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilLoginUsername.setError("Format email tidak valid");
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Tautan pemulihan telah dikirim ke " + email, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Gagal mengirim email: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}