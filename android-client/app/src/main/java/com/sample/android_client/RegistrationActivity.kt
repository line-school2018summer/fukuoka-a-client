package com.sample.android_client

import android.app.Activity
import android.content.Intent
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

class RegistrationActivity : AppCompatActivity() {

    var selectedPhotoUri: Uri? = null   // アイコンにするために選択した画像のURI
    var userUID: String? = null         // Firebaseで発行されるUID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        register_button_register.setOnClickListener {
            performRegister()
        }

        already_have_account_textview.setOnClickListener {
            // TODO:LoginActivityに遷移する
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
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        // emailまたはpasswordが空だとFirebase登録時にアプリが強制終了してしまうので
        // emptyであるかどうかをチェックしている
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "EmailまたはPasswordを入力してください", Toast.LENGTH_SHORT).show()
            return
        }

        // FirebaseAuthを用いたアカウント作成
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    // ユーザ登録に成功した場合
                    Log.d("RegisterActivity", "Successfully created user with uid: ${it.result.user.uid}")
                    userUID = it.result.user.uid

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
