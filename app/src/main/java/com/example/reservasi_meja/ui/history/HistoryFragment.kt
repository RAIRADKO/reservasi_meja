package com.example.reservasi_meja.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reservasi_meja.R
import com.example.reservasi_meja.data.FirebaseHelper
import com.example.reservasi_meja.databinding.FragmentHistoryBinding
import com.example.reservasi_meja.model.Reservation
import com.example.reservasi_meja.ui.history.adapter.ReservationHistoryAdapter

class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var reservationAdapter: ReservationHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reservationAdapter = ReservationHistoryAdapter(emptyList())
        binding.rvReservations.layoutManager = LinearLayoutManager(context)
        binding.rvReservations.adapter = reservationAdapter

        setupFilterSpinner()
        loadReservations()
    }

    private fun setupFilterSpinner() {
        val statuses = listOf("Semua", "pending", "confirmed", "cancelled")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statuses)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFilter.adapter = adapter

        binding.spinnerFilter.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedStatus = if (position == 0) null else statuses[position]
                loadReservations(selectedStatus)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        }
    }

    private fun loadReservations(statusFilter: String? = null) {
        FirebaseHelper.getCurrentUserId()?.let { userId ->
            FirebaseHelper.getUserReservations(userId, statusFilter) { reservations ->
                if (reservations.isEmpty()) {
                    binding.tvNoReservations.visibility = View.VISIBLE
                    binding.rvReservations.visibility = View.GONE
                } else {
                    binding.tvNoReservations.visibility = View.GONE
                    binding.rvReservations.visibility = View.VISIBLE
                    reservationAdapter.updateData(reservations)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}