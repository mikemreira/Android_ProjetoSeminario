package pt.isel.projetoeseminario.ui.useroperations.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.intellij.lang.annotations.RegExp
import pt.isel.projetoeseminario.R
import pt.isel.projetoeseminario.model.Obra
import pt.isel.projetoeseminario.model.ObrasOutputModel
import pt.isel.projetoeseminario.ui.error.NotFoundErrorScreen
import pt.isel.projetoeseminario.viewModels.FetchState
import pt.isel.projetoeseminario.viewModels.UserViewModel
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Composable
fun PerfilScreen(viewModel: UserViewModel, token: String) {
    val fetchProfileState = viewModel.fetchProfileState.collectAsState()
    val fetchImageState = viewModel.fetchImageState.collectAsState()
    val fetchResult = viewModel.fetchProfileResult.collectAsState()
    val fetchObraResult = viewModel.fetchObraResult.collectAsState()
    val fetchImageResult = viewModel.fetchImageResult.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getUserDetails(token)
        viewModel.getUserObras(token)
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (fetchProfileState.value) {
                is FetchState.Idle -> CircularProgressIndicator()
                is FetchState.Loading -> CircularProgressIndicator()
                is FetchState.Error -> NotFoundErrorScreen()
                is FetchState.Success ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (fetchImageState.value is FetchState.Loading) CircularProgressIndicator() else ProfileImage(
                            bitmap = fetchImageResult.value?.foto
                        )
                        ProfileInfo(title = "Username", value = fetchResult.value?.nome ?: "Default")
                        ProfileInfo(title = "E-mail", value = fetchResult.value?.email ?: "Default")
                        if (fetchObraResult.value != null)
                            HorizontalCardSlider(cardItems = fetchObraResult.value!!)

                    }
            }
        }
    }
}

@Composable
fun ProfileInfo(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.Black
        )
    }
}

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun ProfileImage(bitmap: String?) {
    if (bitmap != null) {
        val imageBytes = Base64.decode(bitmap.substringAfter("base64,", ""))
        val bmp: Bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        Image(
            bitmap = bmp.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .background(Color.LightGray, shape = CircleShape)
                .clip(CircleShape)
                .border(2.dp, Color.DarkGray, CircleShape)
                .padding(4.dp),
            contentScale = ContentScale.Crop
        )
    } else
        Image(
            painterResource(id = R.drawable.builder),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp)
                .background(Color.LightGray, shape = CircleShape)
                .clip(CircleShape)
                .border(2.dp, Color.DarkGray, CircleShape)
                .padding(4.dp),
            contentScale = ContentScale.Crop
        )
}

@Composable
fun HorizontalCardSlider(cardItems: ObrasOutputModel) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(cardItems.obras) { cardItem ->
            CardItemView(cardItem)
        }
    }
}

@Composable
fun CardItemView(cardItem: Obra) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(250.dp)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = cardItem.name,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = cardItem.description,
                fontSize = 16.sp
            )
        }
    }
}
