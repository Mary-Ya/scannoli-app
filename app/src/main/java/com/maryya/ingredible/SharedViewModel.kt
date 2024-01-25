import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

class SharedViewModel : ViewModel() {
    var initialList = listOf(
        "mela", "mele", // Apple, Apples
        "pesca", "pesche", // Peach, Peaches
        "pera", "pere", // Pear, Pears
        "pomodoro", "pomodori", // Tomato, Tomatoes
        "arachide", "arachidi", // Peanut, Peanuts
        "mandorla", "mandorle", // Almond, Almonds
        "noce", "noci", // Walnut, Walnuts
        "nocciola", "nocciole", // Hazelnut, Hazelnuts
        "frutto a guscio", "frutta a guscio", // Tree nut, Tree nuts
        "olio di arachide", // Peanut oil
        "burro di arachidi", // Peanut butter
        "farina di mandorle", // Almond flour
        "estratto di vaniglia", // Vanilla extract
        "marzapane", // Marzipan
        "nutella" // Nutella
    )

    var itemList = mutableStateListOf<String>()
        private set

    fun resetList() {
        itemList.clear()
        itemList.addAll(initialList)
    }

    fun updateList(newItem: String) {
        itemList.add(newItem)
    }

    fun removeItemFromList(index: Int) {
        itemList.toMutableList().apply {
            removeAt(index)
        }
    }

    private var colorList = mutableStateListOf(
        Color(android.graphics.Color.parseColor("#645AD4")), // Violet
        Color(android.graphics.Color.parseColor("#7F00CC")), // Purple
        Color(android.graphics.Color.parseColor("#39B846")), // Green
        Color(android.graphics.Color.parseColor("#FFDB1F")), // Yellow
        Color(android.graphics.Color.parseColor("#1F94FF")), // Blue
    )

    // Map to store the color for each item
    private var itemColorMap = mutableStateMapOf<String, Color>()

    private fun initializeColorMap() {
        itemList.forEachIndexed { index, item ->
            itemColorMap[item] = colorList[index % colorList.size]
        }
    }

    fun getColorForItem(item: String): Color {
        return itemColorMap[item] ?: Color.Gray // Default color if not found
    }

    init {
        resetList()
        initializeColorMap()
    }
}