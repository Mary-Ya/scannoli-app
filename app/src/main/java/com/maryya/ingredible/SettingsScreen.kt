import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(viewModel: SharedViewModel) {
    var textInput by remember { mutableStateOf("") }
    var itemList by remember { mutableStateOf(viewModel.itemList) } // Mock list

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
                    itemList = itemList + textInput
                    textInput = ""
                    viewModel.updateList(itemList)
                }
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add")
        }

        LazyColumn {
            items(itemList) { item ->
                Text(text = item, modifier = Modifier.padding(16.dp))
            }
        }

        Button(
            onClick = { /* TODO: Handle Save Action */ },
            modifier = Modifier.align(Alignment.End).padding(16.dp)
        ) {
            Text("Save")
        }
    }
}