import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import leonardomazzuca.com.github.ItemDAO
import leonardomazzuca.com.github.ItemDatabase
import leonardomazzuca.com.github.ItemModel


class ItemsViewModel(application: Application) : AndroidViewModel(application) {




    private val itemDao: ItemDAO
    val itemsLiveData: LiveData<List<ItemModel>>



    init {
        val database = Room.databaseBuilder(
            getApplication(),
            ItemDatabase::class.java,
            "items_database"
        ).build()


        itemDao = database.itemDao()
        itemsLiveData = itemDao.getAll()
    }







    fun addItem(item: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newItem = ItemModel(name = item)
            itemDao.insert(newItem)



        }
    }

    fun removeItem(item: ItemModel) {
        viewModelScope.launch(Dispatchers.IO) {
            itemDao.delete(item)
        }





    }
}