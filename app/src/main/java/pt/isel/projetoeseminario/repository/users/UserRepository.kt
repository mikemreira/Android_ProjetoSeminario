package pt.isel.projetoeseminario.repository.users

import pt.isel.projetoeseminario.model.ImageOutputModel
import pt.isel.projetoeseminario.model.ObrasOutputModel
import pt.isel.projetoeseminario.model.UserAuthInputModel
import pt.isel.projetoeseminario.model.UserLoginInputModel
import pt.isel.projetoeseminario.model.UserLoginOutputModel
import pt.isel.projetoeseminario.model.UserOutputModel
import pt.isel.projetoeseminario.model.UserSignupInputModel
import pt.isel.projetoeseminario.model.UserSignupOutputModel
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserRepository {
    @POST("/users/signin")
    fun login(@Body loginRequest: UserLoginInputModel): Call<UserLoginOutputModel>

    @POST("/users/signup")
    fun signup(@Body signupRequest: UserSignupInputModel): Call<UserSignupOutputModel>

    @GET("/users/me")
    fun getUserDetails(@Header("Authorization") token: String): Call<UserOutputModel>

    @GET("/users/me/imagem")
    fun getUserImage(@Header("Authorization") token: String, @Query("type") type: String): Call<ImageOutputModel>

    @GET("/obras")
    fun getUserConstructions(@Header("Authorization") token: String): Call<ObrasOutputModel>
}