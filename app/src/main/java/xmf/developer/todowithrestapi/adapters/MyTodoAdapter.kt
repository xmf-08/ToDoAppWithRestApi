package xmf.developer.todowithrestapi.adapters

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import xmf.developer.todowithrestapi.databinding.ItemRvBinding
import xmf.developer.todowithrestapi.models.MyTodo

class MyTodoAdapter( val rvAction:RvAction, val list: ArrayList<MyTodo> = ArrayList()) :
        RecyclerView.Adapter<MyTodoAdapter.Vh>() {
    inner class Vh(private val itemRvBinding: ItemRvBinding) :
        RecyclerView.ViewHolder(itemRvBinding.root) {
        fun onBind(myTodo: MyTodo, position: Int) {
            itemRvBinding.apply {
                tvName.text = myTodo.sarlavha
                tvDeadline.text = myTodo.oxirgi_muddat
                tvStatus.text = myTodo.holat
                tvMatn.text = myTodo.matn

                itemRvBinding.root.setOnLongClickListener {
                    rvAction.deleteTodo(myTodo)
                    true
                }
                itemRvBinding.root.setOnClickListener {
                    rvAction.updateTodo(myTodo)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(ItemRvBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) =
        holder.onBind(list[position], position)

    override fun getItemCount(): Int = list.size

    interface RvAction {
        fun deleteTodo(myTodo: MyTodo)
        fun updateTodo(myTodo: MyTodo)
    }
}
