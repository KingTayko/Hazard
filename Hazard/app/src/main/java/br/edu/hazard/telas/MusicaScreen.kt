package br.edu.hazard.telas

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.hazard.Post
import br.edu.hazard.R
import br.edu.hazard.adapter.ItemListAdapter
import br.edu.hazard.dao.Item
import br.edu.hazard.databinding.ActivityMusicaScreenBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class MusicaScreen : AppCompatActivity() {
    private lateinit var binding: ActivityMusicaScreenBinding
    private val itemList = mutableListOf<Item>()
    private lateinit var itemListAdapter: ItemListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicaScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = FirebaseFirestore.getInstance()

        // Configuração do RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializando o adaptador com a lista de itens
        itemListAdapter = ItemListAdapter(itemList)
        recyclerView.adapter = itemListAdapter

        // Referência para a subcoleção "Post_musica"
        val reference = db.collection("Categorias")
            .document("Musica")
            .collection("Post_musica")

        // Adicionando um listener para alterações em tempo real (SnapshotListener)
        reference.addSnapshotListener { documentos, e ->
            if (e != null) {
                Log.e("MusicaScreen", "Erro ao ler os dados do Firestore", e)
                return@addSnapshotListener
            }

            if (documentos != null) {
                itemList.clear() // Limpa a lista antes de adicionar novos itens

                // Itera sobre os documentos retornados do Firestore
                for (docChange in documentos.documentChanges) {
                    when (docChange.type) {
                        DocumentChange.Type.ADDED -> {
                            // Converte o documento para o modelo Item
                            val item = docChange.document.toObject(Item::class.java)

                            // Recupera o username diretamente do documento
                            val username = docChange.document.getString("username") ?: "Desconhecido"
                            item.username = username  // Adiciona o username ao item

                            // Adiciona o item à lista
                            itemList.add(item)

                            // Notifica que os dados foram atualizados
                            itemListAdapter.notifyItemInserted(itemList.size - 1) // Notifica a inserção do item
                        }
                        DocumentChange.Type.MODIFIED, DocumentChange.Type.REMOVED -> {
                            // Tratamento de modificações e remoções, se necessário
                        }
                    }
                }
            }
        }

        // Direcionando para a página de post
        binding.btnPostar.setOnClickListener {
            val post = Intent(this, Post::class.java)
            startActivity(post)
        }



        //redirecionando para as paginas
        binding.txtcinema.setOnClickListener {
            startActivity(Intent(this, CinemaScreen::class.java))
            finish()
        }

        binding.txtmusica.setOnClickListener {
            startActivity(Intent(this, MusicaScreen::class.java))
            finish()
        }

        binding.txtesporte.setOnClickListener {
            startActivity(Intent(this, EsporteScreen::class.java))
            finish()
        }


        binding.imgUser.setOnClickListener{
            startActivity(Intent(this, UserScreen::class.java))

        }
    }
}
