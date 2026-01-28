import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class HistoryEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val diseaseName: String,
    val confidence: String,
    val date: String
)
