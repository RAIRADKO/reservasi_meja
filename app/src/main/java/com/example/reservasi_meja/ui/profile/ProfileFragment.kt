package com.example.reservasi_meja.ui.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.reservasi_meja.LoginActivity
import com.example.reservasi_meja.data.FirebaseHelper
import com.example.reservasi_meja.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadUserProfile()

        binding.btnSaveProfile.setOnClickListener {
            updateUserProfile()
        }

        binding.btnLogout.setOnClickListener {
            FirebaseHelper.logout()
            startActivity(Intent(requireActivity(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun loadUserProfile() {
        FirebaseHelper.getCurrentUser { user ->
            if (user != null) {
                binding.etName.setText(user.name)
                binding.etEmail.setText(user.email)
                binding.etPhone.setText(user.phone)
                binding.etEmail.isEnabled = false // Email tidak dapat diedit
            } else {
                Toast.makeText(context, "Gagal memuat data profil.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserProfile() {
        val userId = FirebaseHelper.getCurrentUserId() ?: return
        val newName = binding.etName.text.toString()
        val newPhone = binding.etPhone.text.toString()

        if (newName.isNotEmpty() && newPhone.isNotEmpty()) {
            FirebaseHelper.updateUserProfile(userId, newName, newPhone) { success, message ->
                if (success) {
                    Toast.makeText(context, "Profil berhasil diperbarui.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Gagal memperbarui profil: $message", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Nama dan Nomor Telepon tidak boleh kosong.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}