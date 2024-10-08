package pt.isel.projetoeseminario.ui.registos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowCircleLeft
import androidx.compose.material.icons.rounded.ArrowCircleRight
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import pt.isel.projetoeseminario.model.RegistoOutputModel
import pt.isel.projetoeseminario.ui.error.NotFoundErrorScreen
import pt.isel.projetoeseminario.viewModels.FetchState
import pt.isel.projetoeseminario.viewModels.RegistoViewModel


@Composable
fun RegistosScreen(viewModel: RegistoViewModel, token: String) {
    val fetchRegistersState = viewModel.fetchDataState.collectAsState()
    val fetchRegistersResult = viewModel.fetchRegistersResult.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getUserRegisters("Bearer $token")
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (fetchRegistersState.value is FetchState.Loading) CircularProgressIndicator()
            else if (fetchRegistersResult.value == null && fetchRegistersState.value !is FetchState.Loading) NotFoundErrorScreen("No registers at the moment.")
            else if (fetchRegistersResult.value != null && fetchRegistersState.value is FetchState.Success) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    LazyColumn {
                        items(fetchRegistersResult.value!!.registers) { entry ->
                            TimeEntryItem(entry = entry)
                            Spacer(modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimeEntryItem(entry: RegistoOutputModel) {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
        .sizeIn(maxWidth = 128.dp), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier.fillMaxSize(),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowCircleRight,
                            contentDescription = null,
                            tint = Color(0xFF7ABF62)
                        )
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            val time = entry.entrada
                            Text(
                                text = "${time.dayOfMonth}-${if(time.monthValue < 10) "0${time.monthValue}" else time.monthValue}-${time.year} ${time.hour}:${time.minute}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    if (entry.saida != null) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowCircleLeft,
                                contentDescription = null,
                                tint = Color(0xFFEC5454)
                            )
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                val time = entry.saida
                                Text(
                                    text = "${time.dayOfMonth}-${if(time.monthValue < 10) "0${time.monthValue}" else time.monthValue}-${time.year} ${time.hour}:${time.minute}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
                Text(
                    text = entry.nome_obra,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}
