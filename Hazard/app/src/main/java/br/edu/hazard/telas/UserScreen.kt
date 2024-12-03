package br.edu.hazard.telas

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.hazard.Post
import br.edu.hazard.R
import br.edu.hazard.adapter.ItemListAdapter
import br.edu.hazard.dao.Item
import br.edu.hazard.databinding.ActivityUserScreenBinding
import br.edu.hazard.view.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class UserScreen : AppCompatActivity() {
    private lateinit var binding: ActivityUserScreenBinding
    private val itemList = mutableListOf<Item>()
    private lateinit var itemListAdapter: ItemListAdapter

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val db = FirebaseFirestore.getInstance()

        // Configuração do RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializando o adaptador com a lista de itens
        itemListAdapter = ItemListAdapter(itemList)
        recyclerView.adapter = itemListAdapter

        val user = FirebaseAuth.getInstance().currentUser
        val userId = user!!.uid

        val reference = db.collection("Usuarios")
            .document(userId)
            .collection("Post_user")

        reference.addSnapshotListener { documentos, e ->
            if (e != null) {
                // Caso ocorra um erro
                return@addSnapshotListener
            }

            if (documentos != null) {
                // Limpa a lista antes de adicionar novos itens
                itemList.clear()

                // Itera sobre os documentos retornados do Firestore
                for (docChange in documentos.documentChanges) {
                    when (docChange.type) {
                        DocumentChange.Type.ADDED -> {
                            // Converte o documento para o modelo Item
                            val item = docChange.document.toObject(Item::class.java)

                            // Agora, vamos recuperar o username do usuário
                            val username = docChange.document.getString("username") ?: "Desconhecido"
                            item.username = username  // Adicionando o username ao item

                            // Adiciona o item à lista
                            itemList.add(item)

                            // Notifica que os dados foram atualizados
                            itemListAdapter.notifyDataSetChanged()
                        }
                        DocumentChange.Type.MODIFIED -> {
                            // Ações para modificações (opcional)
                        }
                        DocumentChange.Type.REMOVED -> {
                            // Ações para remoções (opcional)
                        }
                    }
                }
            }
        }

        //Direcionando a pagina de post
        binding.btnPostar.setOnClickListener {
            val post = Intent(this, Post::class.java)
            startActivity(post)
            finish()
        }



        // Alterar categoria 'cinema'
        binding.txtcinema.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            val userId = user!!.uid  // Obtemos o ID do usuário logado

            val reference = db.collection("Usuarios").document(userId)

            reference.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val currentCinemaStatus = document.getBoolean("cinema") ?: false
                    val newCinemaStatus = !currentCinemaStatus  // Alterna o status

                    reference.update("cinema", newCinemaStatus)
                        .addOnSuccessListener {
                            val toastMessage = if (newCinemaStatus) "Cinema adicionado à sua ForYou" else "Cinema removido da sua ForYou"
                            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()


                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao atualizar Cinema", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }

        // Alterar categoria 'musica'
        binding.txtmusica.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            val userId = user!!.uid

            val reference = FirebaseFirestore.getInstance()
                .collection("Usuarios")
                .document(userId)

            reference.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val currentMusicaStatus = document.getBoolean("musica") ?: false
                    val newMusicaStatus = !currentMusicaStatus  // Alterna o status

                    reference.update("musica", newMusicaStatus)
                        .addOnSuccessListener {
                            val toastMessage = if (newMusicaStatus) "Música adicionada à sua ForYou" else "Música removida da sua ForYou"
                            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()

                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao atualizar Música", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }

        // Alterar categoria 'esporte'
        binding.txtesporte.setOnClickListener {
            val user = FirebaseAuth.getInstance().currentUser
            val userId = user!!.uid

            val reference = FirebaseFirestore.getInstance()
                .collection("Usuarios")
                .document(userId)

            reference.get().addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val currentEsporteStatus = document.getBoolean("esporte") ?: false
                    val newEsporteStatus = !currentEsporteStatus  // Alterna o status

                    reference.update("esporte", newEsporteStatus)
                        .addOnSuccessListener {
                            val toastMessage = if (newEsporteStatus) "Esporte adicionado à sua ForYou" else "Esporte removido da sua ForYou"
                            Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()


                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao atualizar Esporte", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }

        binding.imglogout.setOnClickListener {

            FirebaseAuth.getInstance().signOut()

            // Exibe uma mensagem de sucesso
            Toast.makeText(this, "Logout realizado com sucesso!", Toast.LENGTH_SHORT).show()

            val loginIntent = Intent(this, MainActivity::class.java)
            startActivity(loginIntent)
            finish()

        }
    }
}