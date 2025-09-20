package com.example.reservasi_meja.ui.reservation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.reservasi_meja.data.FirebaseHelper
import com.example.reservasi_meja.databinding.FragmentReservationBinding
import com.example.reservasi_meja.model.Reservation
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ReservationFragment : Fragment() {
    private var _binding: FragmentReservationBinding? = null
    private val binding get() = _binding!!

    private var selectedDate: String? = null
    private var selectedTime: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        binding.etTime.setOnClickListener {
            showTimePicker()
        }

        binding.btnSubmit.setOnClickListener {
            createReservation()
        }
    }

    private fun showDatePicker() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Pilih Tanggal Reservasi")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selectedDateMillis ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selectedDateMillis
            val formatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
            selectedDate = formatter.format(calendar.time)
            binding.etDate.setText(selectedDate)
        }
        datePicker.show(parentFragmentManager, "DATE_PICKER")
    }

    private fun showTimePicker() {
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(19)
            .setMinute(0)
            .setTitleText("Pilih Waktu Reservasi")
            .build()

        timePicker.addOnPositiveButtonClickListener {
            selectedTime = String.format(Locale.getDefault(), "%02d:%02d", timePicker.hour, timePicker.minute)
            binding.etTime.setText(selectedTime)
        }
        timePicker.show(parentFragmentManager, "TIME_PICKER")
    }

    private fun createReservation() {
        val guests = binding.etNumberOfGuests.text.toString().toIntOrNull()
        val tableNumber = binding.etTableNumber.text.toString().toIntOrNull()

        if (selectedDate.isNullOrEmpty() || selectedTime.isNullOrEmpty() || guests == null || tableNumber == null) {
            Toast.makeText(context, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = FirebaseHelper.getCurrentUserId()
        if (userId != null) {
            FirebaseHelper.getCurrentUser { user ->
                if (user != null) {
                    val newReservation = Reservation(
                        userId = userId,
                        customerName = user.name,
                        date = selectedDate!!,
                        time = selectedTime!!,
                        guests = guests,
                        tableNumber = tableNumber
                    )
                    FirebaseHelper.createReservation(newReservation) { success, message ->
                        if (success) {
                            Toast.makeText(context, "Reservasi berhasil dibuat", Toast.LENGTH_SHORT).show()
                            binding.etDate.setText("")
                            binding.etTime.setText("")
                            binding.etNumberOfGuests.setText("")
                            binding.etTableNumber.setText("")
                        } else {
                            Toast.makeText(context, "Reservasi gagal: $message", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}