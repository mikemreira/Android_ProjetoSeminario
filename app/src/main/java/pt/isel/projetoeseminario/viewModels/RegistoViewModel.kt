package pt.isel.projetoeseminario.viewModels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pt.isel.projetoeseminario.model.RegistoOutputModel
import pt.isel.projetoeseminario.model.RegistoPostOutputModel
import pt.isel.projetoeseminario.model.UserRegisterOutputModel
import pt.isel.projetoeseminario.services.RegistoService
import java.time.LocalDateTime

class RegistoViewModel: ViewModel() {
    private val registosService = RegistoService()

    private val _fetchDataState = MutableStateFlow<FetchState>(FetchState.Idle)
    private val _postDataState = MutableStateFlow<FetchState>(FetchState.Idle)
    private val _fetchRegistersResult = MutableStateFlow<UserRegisterOutputModel?>(null)
    private val _postDataResult = MutableLiveData<RegistoPostOutputModel?>()
    val fetchDataState: StateFlow<FetchState> = _fetchDataState
    val postDataState: StateFlow<FetchState> = _postDataState
    val fetchRegistersResult: StateFlow<UserRegisterOutputModel?> = _fetchRegistersResult
    val postDataResult: LiveData<RegistoPostOutputModel?> = _postDataResult

    fun getUserRegisters(token: String) {
        viewModelScope.launch {
            _fetchDataState.value = FetchState.Loading
            registosService.getUserRegisters(token) { response ->
                if (response == null) {
                    _fetchDataState.value = FetchState.Error("Could not fetch registers")
                } else {
                    _fetchDataState.value = FetchState.Success()
                }
                _fetchRegistersResult.value = response
            }
        }
    }

    fun addUserRegisterEntrada(token: String, time: LocalDateTime, obraId: Int) {
        viewModelScope.launch {
            _postDataState.value = FetchState.Loading
            registosService.addUserRegisterEntrada(token, time, obraId) { response ->
                if (response == null) {
                    _postDataState.value = FetchState.Error("Could not add register")
                } else {
                    _postDataState.value = FetchState.Success()
                }
                _postDataResult.postValue(response)
            }
        }
    }

    fun addUserRegisterSaida(token: String, time: LocalDateTime, obraId: Int) {
        viewModelScope.launch {
            _postDataState.value = FetchState.Loading
            registosService.addUserRegisterSaida(token, time, obraId) { response ->
                if (response == null) {
                    _postDataState.value = FetchState.Error("Could not add register")
                } else {
                    _postDataState.value = FetchState.Success()
                }
                _postDataResult.postValue(response)
            }
        }
    }

    fun addRegisterNFC(token: String, time: LocalDateTime, nfcId: String) {
        viewModelScope.launch {
            _postDataState.value = FetchState.Loading
            registosService.addRegisterNFC(token, time, nfcId) { response ->
                if (response == null) {
                    _postDataState.value = FetchState.Error("Could not add register")
                } else {
                    _postDataState.value = FetchState.Success()
                }
                _postDataResult.postValue(response)
            }
        }
    }

    fun resetState() {
        _postDataState.value = FetchState.Idle
        _fetchDataState.value = FetchState.Idle
    }
}