package com.eati.pexels.presentation

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.eati.pexels.R
import com.eati.pexels.domain.Photo

@Composable
fun PhotosScreen(viewModel: PhotosViewModel) {
    val result by viewModel.photosFlow.collectAsState()

    Photos(result, viewModel::updateResults)
}

@Composable
fun Photos(results: List<Photo>, updateResults: (String) -> Unit) {

    var search by remember { mutableStateOf("architecture") }
    val focusManager =  LocalFocusManager.current

    LaunchedEffect(Unit) {
        updateResults(search)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {

        Text(
            text = "Buscador de imágenes",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = search,
            onValueChange = { search = it },
            placeholder = {
                Text(text = "Buscar...")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            singleLine = true,
            keyboardActions = KeyboardActions(onDone = {
                updateResults(search)
                focusManager.clearFocus()
            })
        )

        LazyColumn(){
            items(results){item ->

                Card(item)

            }
        }
    }
}

@Composable
fun Card(photo: Photo) {

    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize()
            .background(color = Color.Gray)
    ) {

        AsyncImage(
            model = photo.url,
            contentDescription = photo.alt,
            placeholder = painterResource(R.drawable.loading_img),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = { expanded = !expanded }
                )
        )

        if (expanded) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {

                Text(text = "Fotógrafo: " + photo.photographer)
                Hyperlink(url = photo.photographerUrl)

            }
        }
    }
}

@Composable
fun Hyperlink(url : String) {

    val annotatedString = buildAnnotatedString {
        append("Web del fotógrafo")

        addStyle(
            style = SpanStyle(
                color = Color.Blue,
                textDecoration = TextDecoration.Underline
            ),
            start = 0,
            end = length
        )

        addStringAnnotation(
            "URL",
            url,
            start = 0,
            end = length
        )
    }

    val uriHandler = LocalUriHandler.current

    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations("URL", offset, offset)
                .firstOrNull()?.let { annotation ->
                    uriHandler.openUri(annotation.item)
                }
        }
    )
}
