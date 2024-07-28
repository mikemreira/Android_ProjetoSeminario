package pt.isel.projetoeseminario.ui.home

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Nfc
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import pt.isel.projetoeseminario.model.Obra
import pt.isel.projetoeseminario.model.ObrasOutputModel
import pt.isel.projetoeseminario.ui.error.NotFoundErrorScreen
import pt.isel.projetoeseminario.viewModels.FetchState
import pt.isel.projetoeseminario.viewModels.RegistoViewModel
import pt.isel.projetoeseminario.viewModels.UserViewModel
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(userViewModel: UserViewModel, registerViewModel: RegistoViewModel, token: String, onHomeScreen: () -> Unit) {
    val fetchObrasState = userViewModel.fetchObrasState.collectAsState()
    val fetchObrasResult = userViewModel.fetchObraResult.collectAsState()
    val postDataState = registerViewModel.postDataState.collectAsState()
    var nfcToggled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        userViewModel.getUserObras(token)
    }

    var expanded by remember { mutableStateOf(false) }
    var textfieldSize by remember { mutableStateOf(Size.Zero)}

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        if (fetchObrasState.value is FetchState.Loading) CircularProgressIndicator()
        else {
            Log.d("RESPONSESTATES", "fetchobrasstate = ${fetchObrasState.value} fetchobrasresult = ${fetchObrasResult.value?.obras.toString()}")
            if (postDataState.value is FetchState.Success) Toast.makeText(LocalContext.current, "O seu registo foi efetuado com sucesso.", Toast.LENGTH_SHORT).show().also { registerViewModel.resetState() }
            else if (postDataState.value is FetchState.Error) Toast.makeText(LocalContext.current, "Nao foi possível efetuar o seu registo.", Toast.LENGTH_SHORT).show().also { registerViewModel.resetState() }
            Box {
                val obras = fetchObrasResult.value
                if (obras != null && fetchObrasState.value is FetchState.Success) {
                    var selected by remember { mutableStateOf(obras.obras[0]) }
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(vertical = 10.dp)
                        ) {
                            Button(
                                onClick = {
                                    registerViewModel.addUserRegisterEntrada("Bearer $token", LocalDateTime.now(), selected.oid)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF7FCB78
                                    )
                                ),
                                enabled = postDataState.value !is FetchState.Loading,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Entrada")
                            }
                            Spacer(modifier = Modifier.width(8.dp)) // Adjust the space between buttons
                            Button(
                                onClick = {
                                    registerViewModel.addUserRegisterSaida("Bearer $token", LocalDateTime.now(), selected.oid)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFFF5DF68
                                    )
                                ),
                                enabled = postDataState.value !is FetchState.Loading,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(text = "Saída")
                            }
                        }
                        OutlinedTextField(
                            value = selected.name,
                            onValueChange = { },
                            readOnly = true,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .onGloballyPositioned { coordinates ->
                                    textfieldSize = coordinates.size.toSize()
                                },
                            label = { Text("Select construction") },
                            trailingIcon = {
                                Icon(Icons.Default.ArrowDropDown, "contentDescription",
                                    Modifier.clickable { expanded = !expanded })
                            }
                        )
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                        ) {
                            obras.obras.forEach { label ->
                                DropdownMenuItem(text = { Text(text = label.name) }, onClick = {
                                    selected = label
                                })
                            }
                        }
                    }
                } else if (fetchObrasResult.value == null && fetchObrasState.value is FetchState.Success) {
                    NotFoundErrorScreen()
                } else if (fetchObrasState.value !is FetchState.Success) {
                    CircularProgressIndicator()
                }
            }
            if (nfcToggled)
                AlertDialog(
                    onDismissRequest = { nfcToggled = false },
                    title = { Text(text = "NFC Scan") },
                    text = { Text("Passe o seu telemóvel na tag NFC para efetuar o seu registo.", textAlign = TextAlign.Center) },
                    icon = { Icon(imageVector = Icons.Default.Nfc, contentDescription = null) },
                    dismissButton = {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.BottomCenter) {
                            Button(
                                onClick = { nfcToggled = false },
                                modifier = Modifier.align(Alignment.Center)
                            ) {
                                Text("Cancel")
                            }
                        }
                    },
                    confirmButton = { }
                )
            if (fetchObrasResult.value != null)
                FloatingActionButton(
                    onClick = {
                        nfcToggled = true
                    },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Nfc, // Replace with the actual NFC icon
                        contentDescription = "NFC Scan"
                    )
                }
        }
    }
}