import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue

class SharedViewModel : ViewModel() {
    var itemList = mutableStateListOf("mela", "frutta a guscio", "pera")
        private set

    fun updateList(newList: List<String>) {
        itemList.clear()
        itemList.addAll(newList)
    }
}