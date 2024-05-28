package com.maryya.ingredible.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maryya.ingredible.viewmodel.shared.SharedViewModel
import com.maryya.ingredible.data.entity.IngredientList
import kotlinx.coroutines.launch

@Composable
fun ScreenWithListsDrawer (
    navController: NavController,
    viewModel: SharedViewModel,
    mainContent: @Composable () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    ModalDrawer(
    drawerState = drawerState,
    drawerContent = { SheetContent(viewModel = viewModel) },
    content = {
        Column {
            TopBar(navController = navController, onSheetToggle = {
                coroutineScope.launch {
                    if (drawerState.isClosed) drawerState.open() else drawerState.close()
                }
            })
            mainContent()
        }
    }
    )
}

@Composable
fun SheetContent(viewModel: SharedViewModel) {
    val lists by viewModel.listsLiveData.observeAsState(initial = emptyList())
    var isAddingList by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }


    Column(modifier = Modifier.padding(16.dp)) {
        if (isAddingList) {
            TextField(
                value = newListName,
                onValueChange = { newListName = it },
                label = { Text("List Name") }
            )
            Row {
                Button(onClick = {
                    viewModel.addNewList(listName = newListName)
                    isAddingList = false
                    newListName = "" // Reset input field
                }) {
                    Text("Save")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    // Cancel adding
                    isAddingList = false
                    newListName = "" // Reset input field
                }) {
                    Text("Cancel")
                }
            }
        } else {
            Button(onClick = { isAddingList = true }) {
                Text("Add List")
            }
        }
        LazyColumn {
            itemsIndexed(lists) { _, itemList ->
                ListItemView(itemList = itemList) { updatedList, isActive ->
                    viewModel.updateItemListActiveState(updatedList, isActive)
                }
            }
        }
    }
}

@Composable
fun ListItemView(itemList: IngredientList, onActiveChanged: (IngredientList, Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = itemList.isActive,
            onCheckedChange = { isChecked: Boolean ->
                onActiveChanged(itemList, isChecked)
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = itemList.name,
            style = MaterialTheme.typography.h6
        )
    }
}