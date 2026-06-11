package com.example.nnews.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.nnews.databinding.FragmentProfileBinding;
import com.example.nnews.ui.auth.LoginActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Tampilkan Email User
        if (currentUser != null) {
            binding.tvUserEmail.setText(currentUser.getEmail());
        }

        // Aksi klik tombol reset password
        binding.btnResetPassword.setOnClickListener(v -> resetPassword());

        // Aksi klik tombol logout
        binding.btnLogout.setOnClickListener(v -> logoutUser());
    }

    private void resetPassword() {
        if (currentUser != null && currentUser.getEmail() != null) {
            mAuth.sendPasswordResetEmail(currentUser.getEmail())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext(), "Tautan reset password telah dikirim ke email Anda", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(requireContext(), "Gagal mengirim tautan reset", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void logoutUser() {
        mAuth.signOut();
        Toast.makeText(requireContext(), "Berhasil Logout", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
