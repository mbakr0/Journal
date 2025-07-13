package online.mohmedbakr.newsfeed.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 *
 * @param title             title of the article
 * @param newspaperName     the name of the newspaperName
 * @param url               Article Url
 * @param publicationDate   the date When the article published
 * @param imageLink         the link of an image associated with the article
 */

@Entity(tableName = "article")
data class Article(
    @ColumnInfo(name = "newspaper_name") val newspaperName: String,
    @ColumnInfo(name = "title") val title: String,
    @PrimaryKey @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "publicationDate") val publicationDate: String,
    @ColumnInfo(name = "imageLink") val imageLink: String,
)
