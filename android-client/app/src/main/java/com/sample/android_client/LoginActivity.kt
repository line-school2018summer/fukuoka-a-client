package com.sample.android_client

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences(getString(R.string.preference_key), AppCompatActivity.MODE_PRIVATE)
        val email = prefs.getString("email", "null")
        val password = prefs.getString("password", "null")

        if (email != "null" && password != "null") {
            FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener {
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    }
                    .addOnFailureListener {
                        RuntimeException("Can't log in.")
                    }
        } else {
            setContentView(R.layout.activity_login)

            login_button_login.setOnClickListener {
                performLogin()
            }

            go_to_registration_textview.setOnClickListener {
                val intent = Intent(this, RegistrationActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun performLogin() {
        val email = email_edittext_login.text.toString()
        val password = password_edittext_login.text.toString()

        // emailまたはpasswordが空だとログイン時にアプリがクラッシュしてしまうので
        // emptyであるかどうかをチェックしている
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "EmailまたはPasswordを入力してください", Toast.LENGTH_SHORT).show()
            return
        }

        // Firebase認証を用いたEmail/Passwordログイン
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    // ログインに成功した場合
                    Log.d("LoginActivity", "Successfully login with email/password: $email/****")
                    Toast.makeText(this, "ログインしました！", Toast.LENGTH_SHORT).show()

                    val editer = prefs.edit()
                    editer.putString("email", email)
                    editer.putString("password", password)
                    editer.commit()

                    val email_local = prefs.getString("email", "null")
                    val password_local = prefs.getString("password", "null")

                    Log.d("LoginActivity", "Email:$email_local")
                    Log.d("LoginActivity", "Password:$password_local")

                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d("LoginActivity", "Failed to login user: ${it.message}")
                    Toast.makeText(this, "ログインに失敗しました: ${it.message}", Toast.LENGTH_SHORT).show()
                }
    }
}
