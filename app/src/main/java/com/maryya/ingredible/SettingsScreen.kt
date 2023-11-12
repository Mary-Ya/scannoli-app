import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(viewModel: SharedViewModel) {
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
                    viewModel.updateList(viewModel.itemList + textInput)
                    textInput = ""
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add")
        }

        LazyColumn {
            itemsIndexed(viewModel.itemList) { index, item ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = item, modifier = Modifier.weight(1f).padding(8.dp))

                    IconButton(onClick = {
                        // Remove item from the list
                        viewModel.updateList(viewModel.itemList.toMutableList().apply {
                            removeAt(index)
                        })
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Remove")
                    }
                }
            }
        }
    }
}