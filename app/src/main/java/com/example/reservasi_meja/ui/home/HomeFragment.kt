package com.example.reservasi_meja.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.reservasi_meja.R
import com.example.reservasi_meja.data.FirebaseHelper
import com.example.reservasi_meja.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Perbaikan: Mengambil nama pengguna dari FirebaseHelper
        FirebaseHelper.getCurrentUser { user ->
            val displayName = user?.name ?: "Pengguna"
            binding.tvGreeting.text = "Halo, $displayName 👋"
        }

        binding.fabReservation.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_reservationFragment)
        }

        // Perbaikan: Menampilkan reservasi aktif dari Firebase
        FirebaseHelper.getCurrentUserId()?.let { userId ->
            FirebaseHelper.getUserReservations(userId, "confirmed") { reservations ->
                if (reservations.isNotEmpty()) {
                    val activeReservation = reservations.first()
                    binding.tvDate.text = activeReservation.date
                    binding.tvTime.text = activeReservation.time
                    binding.tvTable.text = "Meja #${activeReservation.tableNumber}"
                    binding.cardActiveReservation.visibility = View.VISIBLE
                } else {
                    binding.cardActiveReservation.visibility = View.GONE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}