package br.edu.hazard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.hazard.databinding.ActivityPostBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import android.content.Context
import android.os.Build
import br.edu.hazard.telas.CinemaScreen

class Post : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = FirebaseAuth.getInstance().currentUser

        // Função para enviar notificação
        fun sendNotification(context: Context, title: String, message: String, channelId: String) {
            // Verificar se é Android 8.0 ou superior, caso precise criar um canal de notificação
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(channelId, "Categoria Notifications", NotificationManager.IMPORTANCE_DEFAULT)
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }

            // Criar a notificação
            val notification = NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)  // Ícone da notificação
                .setContentTitle(title)  // Título
                .setContentText(message)  // Mensagem
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)  // Prioridade
                .build()

            // Exibir a notificação
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(0, notification)
        }

        // Botão de envio
        binding.btnEnviar.setOnClickListener {
            val desc = binding.edtDescricao.text.toString()

            // Verificando se as checkboxes foram selecionadas
            val cinema = binding.cbCinema.isChecked
            val esporte = binding.cbEsporte.isChecked
            val musica = binding.cbMusica.isChecked

            // Verificar se nenhum checkbox foi marcado
            if (!cinema && !esporte && !musica) {
                Toast.makeText(this, "Por favor, selecione ao menos uma categoria", Toast.LENGTH_SHORT).show()
                return@setOnClickListener  // Interrompe a execução do código
            }

            val userId = user!!.uid
            val db = FirebaseFirestore.getInstance()

            // Caminho até o documento do usuário logado
            val userRef = db.collection("Usuarios").document(userId)

            // Recuperando o nome do usuário a partir do Firestore
            userRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {

                        val username = documentSnapshot.getString("username") ?: "Nome não disponível"

                        // Caminho da coleção dentro do documento do usuário logado
                        val post_user = userRef.collection("Post_user")

                        // Dados do post
                        val post_usuario = hashMapOf(
                            "username" to username,
                            "descricao" to desc,
                            "cinema" to cinema,
                            "esporte" to esporte,
                            "musica" to musica
                        )

                        // Adicionando o post na coleção "Post_user"
                        post_user.add(post_usuario)
                            .addOnSuccessListener {
                                Toast.makeText(this, "Post realizado com sucesso!", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Erro ao realizar o post", Toast.LENGTH_SHORT).show()
                            }

                        // Realizando o post nas categorias selecionadas
                        if (cinema) {
                            val cinemaRef = db.collection("Categorias").document("Cinema")
                            val Post_cinema = cinemaRef.collection("Post_cinema")

                            val post_filmes = hashMapOf(
                                "nome" to username,
                                "descricao" to desc
                            )

                            Post_cinema.add(post_filmes)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Post realizado com sucesso na categoria Cinema!", Toast.LENGTH_SHORT).show()

                                    // Enviar a notificação para a categoria Cinema
                                    sendNotification(this, "Post no Cinema", "Você postou algo na categoria Cinema!", "CinemaChannel")
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Erro ao realizar o post na categoria Cinema", Toast.LENGTH_SHORT).show()
                                }
                        }

                        if (esporte) {
                            val esporteRef = db.collection("Categorias").document("Esporte")
                            val Post_esporte = esporteRef.collection("Post_esporte")

                            val post_esporte = hashMapOf(
                                "username" to username,
                                "descricao" to desc
                            )

                            Post_esporte.add(post_esporte)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Post realizado com sucesso na categoria Esporte!", Toast.LENGTH_SHORT).show()

                                    // Enviar a notificação para a categoria Esporte
                                    sendNotification(this, "Post no Esporte", "Você postou algo na categoria Esporte!", "EsporteChannel")
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Erro ao realizar o post na categoria Esporte", Toast.LENGTH_SHORT).show()
                                }
                        }

                        if (musica) {
                            val musicaRef = db.collection("Categorias").document("Musica")
                            val Post_musica = musicaRef.collection("Post_musica")

                            val post_musica = hashMapOf(
                                "username" to username,
                                "descricao" to desc
                            )

                            Post_musica.add(post_musica)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Post realizado com sucesso na categoria Música!", Toast.LENGTH_SHORT).show()

                                    // Enviar a notificação para a categoria Música
                                    sendNotification(this, "Post na Música", "Você postou algo na categoria Música!", "MusicaChannel")
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Erro ao realizar o post na categoria Música", Toast.LENGTH_SHORT).show()
                                }
                        }

                        // Redireciona para a página ForYou após o post
                        val cinema = Intent(this, CinemaScreen::class.java)
                        startActivity(cinema)

                    } else {
                        Toast.makeText(this, "Erro ao recuperar nome do usuário", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erro ao acessar o Firestore", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
