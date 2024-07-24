package pt.isel.projetoeseminario.viewModels

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.isel.projetoeseminario.model.ImageOutputModel
import pt.isel.projetoeseminario.model.ObrasOutputModel
import pt.isel.projetoeseminario.model.UserLoginOutputModel
import pt.isel.projetoeseminario.model.UserOutputModel
import pt.isel.projetoeseminario.model.UserSignupOutputModel
import pt.isel.projetoeseminario.services.UserService

sealed class FetchState {
    data object Idle : FetchState()
    data object Loading : FetchState()
    data class Success(val token: String? = null) : FetchState()
    data class Error(val message: String) : FetchState()
}


class UserViewModel(application: Application): AndroidViewModel(application) {
    private val service = UserService()
    private val _loginResult = MutableLiveData<UserLoginOutputModel?>()
    private val _signupResult = MutableLiveData<UserSignupOutputModel?>()
    private val _fetchProfileResult = MutableLiveData<UserOutputModel?>()
    private val _fetchImageResult = MutableLiveData<ImageOutputModel?>()
    private val _fetchObraResult = MutableLiveData<ObrasOutputModel?>()
    private val _loginState = MutableStateFlow<FetchState>(FetchState.Idle)
    private val _signupState = MutableStateFlow<FetchState>(FetchState.Idle)
    private val _fetchProfileState = MutableStateFlow<FetchState>(FetchState.Idle)
    private val _fetchImageState = MutableStateFlow<FetchState>(FetchState.Idle)
    private val _fetchObrasState = MutableStateFlow<FetchState>(FetchState.Idle)
    val loginResult: LiveData<UserLoginOutputModel?> = _loginResult
    val signupResult: LiveData<UserSignupOutputModel?> = _signupResult
    val fetchProfileResult: LiveData<UserOutputModel?> = _fetchProfileResult
    val fetchImageResult: LiveData<ImageOutputModel?> = _fetchImageResult
    val fetchObraResult: LiveData<ObrasOutputModel?> = _fetchObraResult
    val loginState: StateFlow<FetchState> = _loginState
    val signupState: StateFlow<FetchState> = _signupState
    val fetchProfileState: StateFlow<FetchState> = _fetchProfileState
    val fetchImageState: StateFlow<FetchState> = _fetchImageState
    val fetchObrasState: StateFlow<FetchState> = _fetchObrasState
    private val sharedPreferences: SharedPreferences = application.getSharedPreferences("users", Context.MODE_PRIVATE)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = FetchState.Loading
            service.login(email, password) { response ->
                if (response == null)
                    _loginState.value = FetchState.Error("E-mail or password invalid")
                else {
                    with(sharedPreferences.edit()) {
                        putString("user_token", response.token)
                        apply()
                    }
                    _loginState.value = FetchState.Success(response.token)
                }
                _loginResult.postValue(response)
            }
        }
    }

    fun signup(username: String, email: String, password: String) {
        viewModelScope.launch {
            service.signup(username, email, password) { response ->
                _signupState.value = FetchState.Loading
                if (response == null)
                    _signupState.value = FetchState.Error("Could not sign user up")
                else
                    _signupState.value = FetchState.Success()
                _signupResult.postValue(response)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            with(sharedPreferences.edit()) {
                remove("user_token")
                apply()
            }
            _loginState.value = FetchState.Idle
            _signupState.value = FetchState.Idle
        }
    }

    fun getUserDetails(token: String) {
        viewModelScope.launch {
            _fetchProfileState.value = FetchState.Loading
            _fetchImageState.value = FetchState.Loading
            _fetchObrasState.value = FetchState.Loading
            service.getUserDetails(token) { response ->
                Log.d("OKHTTP", response.toString())
                if (response == null) {
                    _fetchProfileState.value = FetchState.Error("Could not fetch user details")
                    _fetchProfileResult.postValue(response)
                    if (sharedPreferences.getString("user_token", null) != null) {
                        /*with(sharedPreferences.edit()) {
                            remove("user_token")
                            apply()
                        }*/
                    }
                    //return@getUserDetails
                } else
                    _fetchProfileState.value = FetchState.Success()
                _fetchProfileResult.postValue(response)
            }

            service.getUserImage(token) { response ->
                if (response == null) {
                    _fetchImageState.value = FetchState.Error("Could not fetch user image")
                } else {
                    _fetchImageState.value = FetchState.Success()
                }
                _fetchImageResult.postValue(response)
            }

            service.getUserConstructions(token) { response ->
                if (response == null)
                    _fetchObrasState.value = FetchState.Error("Could not fetch construction details")
                else {
                    Log.d("RESPONSE", response.obras.toString())
                    _fetchObrasState.value = FetchState.Success()
                }
                _fetchObraResult.postValue(response)
            }
        }
    }
}