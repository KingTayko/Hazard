package br.edu.hazard.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.hazard.dao.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import br.edu.hazard.databinding.ActivitySignInBinding
import br.edu.hazard.telas.CinemaScreen

class SignIn : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnCadastrar.setOnClickListener {
            val username = binding.edtUsername.text.toString()
            val email = binding.edtEmail.text.toString()
            val senha = binding.edtSenha.text.toString()

            //verificando se as checkbox foram assionadas
            val cinema = binding.cbCinema.isChecked
            val esporte = binding.cbEsporte.isChecked
            val musica = binding.cbMusica.isChecked

            // Validação dos campos obrigatórios
            if (email.isEmpty()) {
                Toast.makeText(this, "Insira o email", Toast.LENGTH_SHORT).show()
            } else if (senha.isEmpty()) {
                Toast.makeText(this, "Insira a senha", Toast.LENGTH_SHORT).show()
            } else if (username.isEmpty()) {
                Toast.makeText(this, "Insira o username", Toast.LENGTH_SHORT).show()
            } else {
                // Verificar se o e-mail já existe no Firestore
                db.collection("Usuarios")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnSuccessListener { querySnapshot ->
                        if (querySnapshot.isEmpty) {
                            // Verificar se o username já existe no Firestore
                            db.collection("Usuarios")
                                .whereEqualTo("username", username)
                                .get()
                                .addOnSuccessListener { querySnapshot ->
                                    if (querySnapshot.isEmpty) {
                                        // Se o username não existir, criar o usuário
                                        auth.createUserWithEmailAndPassword(email, senha)
                                            .addOnCompleteListener(this) { task ->
                                                if (task.isSuccessful) {
                                                    val userId = auth.currentUser?.uid ?: return@addOnCompleteListener

                                                    // Salvando os dados do usuário
                                                    val user = Usuario(
                                                        username,
                                                        email,
                                                        senha,
                                                        cinema = cinema,
                                                        esporte = esporte,
                                                        musica = musica
                                                    )

                                                    // Salvando no Firestore
                                                    db.collection("Usuarios").document(userId).set(user)
                                                        .addOnSuccessListener {
                                                            Toast.makeText(this, "Usuário cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                                                            startActivity(Intent(this,CinemaScreen::class.java))
                                                            finish()
                                                        }
                                                        .addOnFailureListener {
                                                            Toast.makeText(this, "Erro ao salvar usuário", Toast.LENGTH_SHORT).show()
                                                        }
                                                } else {
                                                    Toast.makeText(this, "Falha na autenticação", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                    } else {
                                        // Se o username já existe, mostrar um aviso
                                        Toast.makeText(this, "Username já cadastrado. Escolha outro.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Erro ao verificar username", Toast.LENGTH_SHORT).show()
                                }
                        } else {
                            // Se o e-mail já existe, mostrar um aviso
                            Toast.makeText(this, "E-mail já cadastrado. Escolha outro.", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Erro ao verificar e-mail", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}
