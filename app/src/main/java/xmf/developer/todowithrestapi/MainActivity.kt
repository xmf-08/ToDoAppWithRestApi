package xmf.developer.todowithrestapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import retrofit2.Call
import xmf.developer.todowithrestapi.models.MyTodo
import xmf.developer.todowithrestapi.retrofit.ApiClient
import retrofit2.Callback
import retrofit2.Response
import xmf.developer.todowithrestapi.adapters.MyTodoAdapter
import xmf.developer.todowithrestapi.databinding.ActivityMainBinding
import xmf.developer.todowithrestapi.databinding.ItemDialogBinding
import xmf.developer.todowithrestapi.models.MyReqDelete
import xmf.developer.todowithrestapi.models.MyTodo2

private const val TAG = "MainActivity"
class MainActivity : AppCompatActivity(),MyTodoAdapter.RvAction {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var myTodoAdapter: MyTodoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        myTodoAdapter = MyTodoAdapter(this)
        binding.rv.adapter = myTodoAdapter
        loadTodo()

        binding.btnAdd.setOnClickListener {
            addToDo()
        }
        binding.mySwipe.setOnRefreshListener {
            loadTodo()
        }
    }

    private fun addToDo() {
            val dialog = AlertDialog.Builder(this).create()
            val dialogBinding = ItemDialogBinding.inflate(layoutInflater)
            dialogBinding.progressBar.visibility = View.GONE
            dialog.setView(dialogBinding.root)
            dialogBinding.btnAdd.setOnClickListener {
                val myTodo2 = MyTodo2(
                    dialogBinding.tvTitle.text.toString(),
                    dialogBinding.tvText.text.toString(),
                    dialogBinding.spinnerStatus.selectedItem.toString(),
                    dialogBinding.tvDeadline.text.toString()
                )
                dialogBinding.progressBar.visibility = View.VISIBLE
                ApiClient.getApiService().addTodo(myTodo2)
                    .enqueue(object : Callback<MyTodo> {
                        override fun onResponse(call: Call<MyTodo>, response: Response<MyTodo>) {

                            if (response.isSuccessful) {
                                dialogBinding.progressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@MainActivity,
                                    "${response.body()?.id} id bilan saqlandi",
                                    Toast.LENGTH_SHORT
                                ).show()
                                dialog.dismiss()
                            }
                        }

                        override fun onFailure(call: Call<MyTodo>, t: Throwable) {
                            dialogBinding.progressBar.visibility = View.GONE
                            Toast.makeText(this@MainActivity, "Xatolik yuz berdi", Toast.LENGTH_SHORT)
                                .show()


                        }
                    })

            }
            dialog.show()


        }

    private fun loadTodo(){
        ApiClient.getApiService().getAllToDo()
            .enqueue(object : Callback<List<MyTodo>>{
                override fun onResponse(
                    call: Call<List<MyTodo>>,
                    response: Response<List<MyTodo>>,
                ) {
                    if (response.isSuccessful){
                        binding.myProgress.visibility = View.GONE
                        myTodoAdapter.list.clear()
                        myTodoAdapter.list.addAll(response.body()!!)
                        myTodoAdapter.notifyDataSetChanged()
                        binding.mySwipe.isRefreshing = false
                    }
                }

                override fun onFailure(call: Call<List<MyTodo>>, t: Throwable) {
                    binding.myProgress.visibility = View.GONE
                    Toast.makeText(
                        this@MainActivity,
                        "Internetga bog'lanishni tekshiring",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.mySwipe.isRefreshing = false
                }
            })
    }
    override fun deleteTodo(myTodo: MyTodo) {
        ApiClient.getApiService().deleteTodo(myTodo.id)
            .enqueue(object : Callback<MyReqDelete> {
                override fun onResponse(call: Call<MyReqDelete>, response: Response<MyReqDelete>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@MainActivity,
                            "Delete ${response.code()} : ${myTodo.id}",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadTodo()
                    } else {
                        Toast.makeText(this@MainActivity, "${response.code()}", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<MyReqDelete>, t: Throwable) {
                    Toast.makeText(
                        this@MainActivity,
                        "O'chirishda Internet bilan muammo",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })
    }

    override fun updateTodo(myTodo: MyTodo) {
        val dialog = AlertDialog.Builder(this).create()
        val dialogBinding = ItemDialogBinding.inflate(layoutInflater)
        dialogBinding.spinnerStatus.visibility = View.VISIBLE
        dialogBinding.progressBar.visibility = View.GONE

        dialogBinding.apply {
            tvTitle.setText(myTodo.sarlavha)
            tvDeadline.setText(myTodo.oxirgi_muddat)
            tvText.setText(myTodo.matn)
            when(myTodo.holat){
                "Yangi" -> spinnerStatus.setSelection(0)
                "Qilinayotgan" -> spinnerStatus.setSelection(1)
                "Tugagan" -> spinnerStatus.setSelection(2)
            }
        }

        dialog.setView(dialogBinding.root)
        dialogBinding.btnAdd.setOnClickListener {

            myTodo.sarlavha = dialogBinding.tvTitle.text.toString()
            myTodo.matn = dialogBinding.tvText.text.toString()
            myTodo.holat = dialogBinding.spinnerStatus.selectedItem.toString()
            myTodo.oxirgi_muddat = dialogBinding.tvDeadline.text.toString()

            ApiClient.getApiService().updateTodo(
                myTodo.id,
                MyTodo2(myTodo.sarlavha, myTodo.matn, myTodo.holat, myTodo.oxirgi_muddat)
            ).enqueue(object : Callback<MyTodo>{
                override fun onResponse(call: Call<MyTodo>, response: Response<MyTodo>) {
                    Toast.makeText(
                        this@MainActivity,
                        "${response.body()} o'zgartirildi",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialog.cancel()
                }

                override fun onFailure(call: Call<MyTodo>, t: Throwable) {
                    Toast.makeText(this@MainActivity, "O'zgartirishda muammo", Toast.LENGTH_SHORT)
                        .show()
                }
            })

        }
        dialog.show()
    }
}