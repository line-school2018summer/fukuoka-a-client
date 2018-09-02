package com.sample.android_client

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        login_button_login.setOnClickListener {
            performLogin()
        }

        back_to_registration_textview.setOnClickListener {
            finish()
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

                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d("LoginActivity", "Failed to login user: ${it.message}")
                    Toast.makeText(this, "ログインに失敗しました: ${it.message}", Toast.LENGTH_SHORT).show()
                }
    }
}
