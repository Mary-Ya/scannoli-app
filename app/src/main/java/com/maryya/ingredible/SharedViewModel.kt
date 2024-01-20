import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

class SharedViewModel : ViewModel() {
    val colorList = mutableStateListOf(Color.Red, Color.Green, Color.Blue, Color.Yellow) // Add more colors as needed

    var itemList = mutableStateListOf("mela", "frutta a guscio", "pera")
        private set

    fun updateList(newList: List<String>) {
        itemList.clear()
        itemList.addAll(newList)
    }
}