import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maryya.ingredible.entity.ItemList
import kotlinx.coroutines.launch

class ItemListViewModel(private val repository: ItemRepository) : ViewModel() {

    private val _allItemLists = MutableLiveData<List<ItemList>>()
    val allItemLists: LiveData<List<ItemList>> = _allItemLists

    init {
        loadItemLists()
    }

    //CHANGE HERE
    private fun loadItemLists() = viewModelScope.launch {
        val lists = repository.getAllItemLists() // Fetch the list asynchronously
        if (lists.isEmpty()) {
            // If the list is empty, perform necessary actions, e.g., initializing with default values
            Log.d("LoadItemList", "isempty")
        } else {
            _allItemLists.postValue(allItemLists.value) // Update LiveData with the fetched list
        }
    }
    // More functions for update, delete as needed...
}