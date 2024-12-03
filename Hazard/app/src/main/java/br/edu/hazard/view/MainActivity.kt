package br.edu.hazard.view

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.hazard.Post
import br.edu.hazard.telas.CinemaScreen
import br.edu.hazard.databinding.ActivityMainBinding
import br.edu.hazard.telas.EsporteScreen
import br.edu.hazard.telas.MusicaScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializando o FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Verificar se o usuário está logado
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Usuário já está logado, redireciona para a tela ForYou
            val cinema = Intent(this, CinemaScreen::class.java)
            startActivity(cinema)
            finish()  // Finaliza a MainActivity para evitar que o usuário volte a essa tela
        }

        // Ação do botão de login
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val senha = binding.edtSenha.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Insira o email", Toast.LENGTH_SHORT).show()
            } else if (senha.isEmpty()) {
                Toast.makeText(this, "Insira a senha", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Login bem-sucedido
                            val user = auth.currentUser
                            user?.let {
                                val userId = it.uid
                                // Aqui você pode obter os dados do usuário no Firestore, se necessário
                                db.collection("Usuarios").document(userId).get()
                                    .addOnSuccessListener { document ->
                                        // Aqui você pode usar os dados do usuário
                                        Toast.makeText(this, "Login realizado com sucesso", Toast.LENGTH_SHORT).show()

                                        // Redireciona para a tela ForYou
                                        val cinema = Intent(this, CinemaScreen::class.java)
                                        startActivity(cinema)
                                        finish()  // Fecha a MainActivity
                                    }
                            }
                        } else {
                            // Falha no login
                            Toast.makeText(this, "Usuário não cadastrado!", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        // Ação do botão de cadastro
        binding.txvCadastro.setOnClickListener {
            val telaCadastro = Intent(this, SignIn::class.java)
            startActivity(telaCadastro)
        }

        binding.txvSenha.setOnClickListener{
            val email = binding.edtEmail.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(this, "Por favor, insira seu e-mail", Toast.LENGTH_SHORT).show()
            } else {
                Firebase.auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "E-mail de redefinição enviado.")
                            Toast.makeText(this, "E-mail enviado com sucesso!", Toast.LENGTH_SHORT).show()
                        } else {
                            val error = task.exception?.localizedMessage ?: "Erro desconhecido"
                            Log.e(TAG, "Erro ao enviar e-mail: $error")
                            Toast.makeText(this, "Erro ao enviar e-mail: $error", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
    }
}
}
