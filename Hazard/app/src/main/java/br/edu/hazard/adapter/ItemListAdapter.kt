package br.edu.hazard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.edu.hazard.R
import br.edu.hazard.dao.Item
import com.bumptech.glide.Glide


class ItemListAdapter(val itemList: List<Item>) : RecyclerView.Adapter<ItemListAdapter.ItemViewHolder>() {

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewUsername: TextView = itemView.findViewById(R.id.txv_username)  // Novo campo para o username
        val textViewDesc: TextView = itemView.findViewById(R.id.txv_desc)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_posts, parent, false)
        return ItemViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]

        // Definindo o username
        holder.textViewUsername.text = item.username  // Exibe o username do usuário

        // Definindo a descrição e imagem
        holder.textViewDesc.text = item.descricao

    }
}
