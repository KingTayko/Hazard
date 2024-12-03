package br.edu.hazard.telas

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.edu.hazard.Post
import br.edu.hazard.R
import br.edu.hazard.adapter.ItemListAdapter
import br.edu.hazard.dao.Item
import br.edu.hazard.databinding.ActivityEsporteScreenBinding
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore

class CinemaScreen : AppCompatActivity() {
    private lateinit var binding: ActivityEsporteScreenBinding
    private val itemList = mutableListOf<Item>()
    private lateinit var itemListAdapter: ItemListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEsporteScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = FirebaseFirestore.getInstance()

        // Configuração do RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inicializando o adaptador com a lista de itens
        itemListAdapter = ItemListAdapter(itemList)
        recyclerView.adapter = itemListAdapter

        // Referência à subcoleção Post_cinema
        val reference = db.collection("Categorias")
            .document("Cinema")
            .collection("Post_cinema")

        // Adicionando um listener para alterações em tempo real (SnapshotListener)
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

                            // Recupera o username diretamente do documento
                            val username = docChange.document.getString("username") ?: "Desconhecido"
                            item.username = username  // Adiciona o username ao item

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
