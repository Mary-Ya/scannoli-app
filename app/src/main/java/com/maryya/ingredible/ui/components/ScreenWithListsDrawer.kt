package com.maryya.ingredible.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Text
import androidx.compose.material.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.maryya.ingredible.viewmodel.shared.SharedViewModel
import com.maryya.ingredible.data.entity.ItemList
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

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        itemsIndexed(lists) { index, list ->
            ListItemView(itemList = list)
        }
    }
}

@Composable
fun ListItemView(itemList: ItemList) {
    Text(
        text = itemList.name,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        style = MaterialTheme.typography.h6
    )
}