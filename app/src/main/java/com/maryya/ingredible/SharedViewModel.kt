import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class SharedViewModel : ViewModel() {
    var itemList by mutableStateOf(listOf("mela", "frutta di guscio", "pera"))
        private set

    fun updateList(newList: List<String>) {
        itemList = newList
    }
}