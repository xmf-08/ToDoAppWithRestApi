package xmf.developer.todowithrestapi.retrofit

import retrofit2.Call
import retrofit2.http.*
import xmf.developer.todowithrestapi.models.MyReqDelete
import xmf.developer.todowithrestapi.models.MyTodo
import xmf.developer.todowithrestapi.models.MyTodo2

interface MyRetrofitService {

    @GET("plan")
    fun getAllToDo(): Call<List<MyTodo>>

    @POST("plan/")
    fun addTodo(@Body myTodo2:MyTodo2):Call<MyTodo>

    @DELETE("plan/{id}/")
    fun deleteTodo(@Path("id") id:Int):Call<MyReqDelete>

    @PATCH("plan/{id}/")
    fun updateTodo(@Path("id") id:Int, @Body myToDoRequest: MyTodo2): Call<MyTodo>
}