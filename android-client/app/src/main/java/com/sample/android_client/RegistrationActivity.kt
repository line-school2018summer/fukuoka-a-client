package com.sample.android_client

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_registration.*
import java.util.*

const val LIMIT_USER_NAME_LENGTH = 20        // ユーザが登録できる名前の長さの限界

class RegistrationActivity : AppCompatActivity() {
    lateinit var prefs: SharedPreferences
    var selectedPhotoUri: Uri? = null   // アイコンにするために選択した画像のURI
    var userUID: String? = null         // Firebaseで発行されるUID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        prefs = getSharedPreferences(getString(com.sample.android_client.R.string.preference_key), AppCompatActivity.MODE_PRIVATE)

        register_button_register.setOnClickListener {
            performRegister()
        }

        back_to_login_textview.setOnClickListener {
            finish()
        }

        // アイコンを設定する
        select_photo_button_register.setOnClickListener {
            Log.d("RegisterationActivity", "Try to show photo selector")
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 画像選択に成功した場合
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)

            // 選択した画像を円形に表示する(最初に表示されていた丸は邪魔になるので透明にしている)
            select_photo_button_register.alpha = 0f
            selectphoto_imageview_register.setImageBitmap(bitmap)
        }
    }

    private fun performRegister() {
        val userId = userid_edittext_register.text.toString()
        val name = username_edittext_register.text.toString()
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        if (!isValidInputData(userId, name, email, password)) return

        // FirebaseAuthを用いたアカウント作成
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    // ユーザ登録に成功した場合
                    Log.d("RegisterActivity", "Successfully created user with uid: ${it.result.user.uid}")
                    userUID = it.result.user.uid

                    if (selectedPhotoUri == null) {
                        Log.d("RegistrationActivity", "アイコンが選択されていないのでデフォルトアイコンを設定する")
                        // TODO: デフォルトアイコンを設定する
                    }

                    val editor = prefs.edit()
                    editor.putString("email", email)
                    editor.putString("password", password)
                    editor.commit()

                    // 登録したユーザアイコンをFirebaseのストレージに保存する
                    // 実際はFirebaseではなく、しかるべき場所(EC2?)に保存するなどする
                    uploadImageToFirebaseStorage()

                    Toast.makeText(this, "ユーザ登録が完了しました！", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeActivity::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Log.d("RegisterActivity", "Failed to create user: ${it.message}")
                    Toast.makeText(this, it.message.toString(), Toast.LENGTH_SHORT).show()
                }

    }

    private fun isValidInputData(userId: String, name: String, email: String, password: String): Boolean {
        if (userId.isEmpty()) {
            Toast.makeText(this, "UserIDを入力してください", Toast.LENGTH_SHORT).show()
            return false
        }

        if (isAlreadyRegistered(userId)) {
            Toast.makeText(this, "このUserIDは既に登録されています", Toast.LENGTH_SHORT).show()
            return false
        }

        if (name.isEmpty()) {
            Toast.makeText(this, "Nameを入力してください", Toast.LENGTH_SHORT).show()
            return false
        }

        if (name.length > LIMIT_USER_NAME_LENGTH) {
            Toast.makeText(this, "名前は${LIMIT_USER_NAME_LENGTH}文字以内にしてください", Toast.LENGTH_SHORT).show()
            return false
        }

        if (email.isEmpty()) {
            Toast.makeText(this, "Emailを入力してください", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Passwordを入力してください", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun isAlreadyRegistered(userId: String): Boolean {
        // TODO: サーバのDBにアクセスして、userIdが既に登録していないか確かめる
        return false
    }


    // 以下はあくまでもサンプル
    // Firebaseのストレージとデータベースに保存するならこんな感じ
    // 消してしまってもOK

    private fun uploadImageToFirebaseStorage() {
        val url = selectedPhotoUri ?: return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/image/$filename")

        ref.putFile(url)
                .addOnSuccessListener {
                    Log.d("RegisterActivity", "Successfully uploaded image: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        saveUserToFirebaseDatabase(it.toString())
                    }
                }
    }

    private fun saveUserToFirebaseDatabase(profileImageUri: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")

        val user = User(uid, username_edittext_register.text.toString(), profileImageUri)

        ref.setValue(user)
                .addOnSuccessListener {
                    Log.d("RegisterActivity", "Finally we saved the user to Firebase Database!")
                }
    }

    data class User(val uid: String,
                    val userName: String,
                    val userIconURI: String)
}
