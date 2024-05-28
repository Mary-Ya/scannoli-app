package com.maryya.ingredible.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maryya.ingredible.viewmodel.shared.SharedViewModel
import androidx.compose.runtime.livedata.observeAsState

@Composable
fun SettingsScreen(viewModel: SharedViewModel = viewModel()) {
    // Use observeAsState() for LiveData
    val itemList by viewModel.itemsLiveData.observeAsState(initial = emptyList())

    var textInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = textInput,
            onValueChange = { textInput = it },
            label = { Text("Enter item") },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )

        FloatingActionButton(
            onClick = {
                if (textInput.isNotBlank()) {
                    viewModel.updateList(textInput)
                    textInput = ""
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add")
        }

        if(itemList.isEmpty()) {
            Text("List is empty")
        }

        LazyColumn {
            itemsIndexed(itemList) { index, item ->
                // Adjust the UI representation as per your data class structure
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = item.name, modifier = Modifier.weight(1f).padding(8.dp)) // Assuming item.toString() returns the displayable text

                    IconButton(onClick = {
                        // Update your logic to remove from the database
                        viewModel.removeItemFromList(item) // Adjust this method according to your ViewModel
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Remove")
                    }
                }
            }
        }
    }
}