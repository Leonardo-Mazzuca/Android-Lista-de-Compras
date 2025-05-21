🛒 Android Lista de Compras
Este é um projeto Android desenvolvido em Kotlin com o objetivo de criar uma lista de compras dinâmica, permitindo que o usuário adicione e remova itens com facilidade.

![Screenshot (4)](https://github.com/user-attachments/assets/c8faa1e3-2b76-4f60-952a-846a6e649ec0)

Adicionando items na lista:

![Screenshot (5)](https://github.com/user-attachments/assets/a95495fa-39df-414b-8067-14186f1b6f40)

✨ Funcionalidades
Adicionar itens à lista de compras

Remover itens ao tocar sobre eles

Armazenamento local utilizando SQLite nativo, garantindo que os dados persistam mesmo após fechar o app

🧠 Estrutura do Código

- A MainActivity é a classe principal responsável por:
- Carregar o layout principal da aplicação (activity_main.xml)
- Inicializar a Toolbar com o título "Lista de Compras"
- Gerenciar os componentes de interface como EditText, Button e RecyclerView
- Conectar o RecyclerView ao ItemsAdapter, que exibe a lista de itens

Utilizar o ViewModel (ItemsViewModel) para manter a lógica de negócios separada da UI, seguindo os princípios de arquitetura recomendados

Observar as alterações na lista de itens via LiveData, atualizando a interface automaticamente sempre que os dados forem modificados

📦 Model – Explicação Completa
🔹 1. ItemModel – A Entidade do Banco de Dados
```kotlin

@Entity
data class ItemModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)

```

@Entity: Indica que essa classe representa uma tabela no banco de dados SQLite.

id: É a chave primária (@PrimaryKey), com geração automática (autoGenerate = true), usada para identificar cada item de forma única.

name: É o nome do item inserido na lista de compras.

Essa classe define a estrutura da tabela no SQLite usando a biblioteca Room.

🔹 2. ItemDAO – Data Access Object

```kotlin
@Dao
interface ItemDAO {
    @Query("SELECT * FROM ItemModel")
    fun getAll(): LiveData<List<ItemModel>>

    @Insert
    fun insert(item: ItemModel)

    @Delete
    fun delete(item: ItemModel)
}
```
@Dao: Define a interface de acesso ao banco.

getAll(): Retorna todos os itens da tabela como um LiveData, para que a interface seja atualizada automaticamente sempre que os dados mudarem.

insert(): Insere um novo item no banco.

delete(): Remove um item do banco.

🔹 3. ItemDatabase – Instância do Banco de Dados

```kotlin
@Database(entities = [ItemModel::class], version = 1)
abstract class ItemDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDAO
}
```

@Database: Define a classe que representa o banco de dados.

entities: Define as tabelas existentes (neste caso, apenas ItemModel).

version: Versão do banco de dados (importante para migrações futuras).

itemDao(): Exposição do DAO para acessar métodos como insert, delete, e getAll.

🔹 4. ItemsViewModel – Conexão entre UI e Dados

```kotlin
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

```

ItemsViewModel: Classe da arquitetura MVVM que fornece os dados para a interface (MainActivity) e reage às ações do usuário.

LiveData: Permite observar dados e atualizá-los automaticamente na UI.

viewModelScope: Usado para lançar corrotinas seguras no ciclo de vida do ViewModel.

Dispatchers.IO: Garante que as operações de banco (como insert e delete) sejam feitas em uma thread separada.


🔹 5. ItemsViewModelFactory – Factory para criar o ViewModel

```kotlin
class ItemsViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemsViewModel::class.java)) {
            return ItemsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

```

Necessário quando seu ViewModel recebe parâmetros no construtor (como Application).

O Android exige o uso de uma Factory para criar o ViewModel corretamente nesses casos.
