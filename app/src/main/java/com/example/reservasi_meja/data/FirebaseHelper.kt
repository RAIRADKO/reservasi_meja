package com.example.reservasi_meja.data

import com.example.reservasi_meja.model.Reservation
import com.example.reservasi_meja.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

object FirebaseHelper {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    // Fungsi baru untuk mendapatkan data pengguna saat ini
    fun getCurrentUser(onDataLoaded: (User?) -> Unit) {
        val userId = getCurrentUserId()
        if (userId != null) {
            database.child("users").child(userId).get()
                .addOnSuccessListener { snapshot ->
                    val user = snapshot.getValue(User::class.java)
                    onDataLoaded(user)
                }
                .addOnFailureListener {
                    onDataLoaded(null)
                }
        } else {
            onDataLoaded(null)
        }
    }

    // Fungsi baru untuk memperbarui profil pengguna
    fun updateUserProfile(userId: String, newName: String, newPhone: String, onComplete: (Boolean, String?) -> Unit) {
        val updates = hashMapOf<String, Any>(
            "name" to newName,
            "phone" to newPhone
        )

        database.child("users").child(userId).updateChildren(updates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun registerUser(email: String, password: String, user: User, onComplete: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    database.child("users").child(userId).setValue(user.copy(id = userId))
                        .addOnCompleteListener {
                            onComplete(true, null)
                        }
                        .addOnFailureListener { e ->
                            onComplete(false, e.message)
                        }
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun loginUser(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message)
                }
            }
    }

    fun createReservation(reservation: Reservation, onComplete: (Boolean, String?) -> Unit) {
        val reservationId = database.child("reservations").push().key
        val newReservation = reservation.copy(id = reservationId ?: "")

        database.child("reservations").child(reservationId ?: "").setValue(newReservation)
            .addOnCompleteListener {
                onComplete(true, null)
            }
            .addOnFailureListener { e ->
                onComplete(false, e.message)
            }
    }

    // Mengubah fungsi ini agar lebih generik
    fun getUserReservations(userId: String, statusFilter: String? = null, onDataLoaded: (List<Reservation>) -> Unit) {
        val query = database.child("reservations").orderByChild("userId").equalTo(userId)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reservations = mutableListOf<Reservation>()
                for (child in snapshot.children) {
                    child.getValue(Reservation::class.java)?.let {
                        if (statusFilter == null || it.status == statusFilter) {
                            reservations.add(it)
                        }
                    }
                }
                // Urutkan berdasarkan tanggal dan waktu terbaru
                reservations.sortByDescending { it.date + it.time }
                onDataLoaded(reservations)
            }

            override fun onCancelled(error: DatabaseError) {
                onDataLoaded(emptyList())
            }
        })
    }

    fun logout() {
        auth.signOut()
    }
}