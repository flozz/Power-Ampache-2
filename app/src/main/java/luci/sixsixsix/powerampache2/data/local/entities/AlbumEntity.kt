package luci.sixsixsix.powerampache2.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import luci.sixsixsix.powerampache2.domain.models.Album
import luci.sixsixsix.powerampache2.domain.models.MusicAttribute

@Entity
data class AlbumEntity(
    @PrimaryKey val id: String,
    val name: String = "",
    val basename: String = "",
    val artistId: String = "",
    val artistName: String = "",
    val artists: List<MusicAttribute> = listOf(),
    val time: Int = 0,
    val year: Int = 0,
    val songCount: Int = 0,
    val diskCount: Int = 0,
    val genre: List<MusicAttribute>,
    val artUrl: String = "",
    val flag: Int = 0,
    val rating: Int = 0,
    val averageRating: Int = 0,
)

fun AlbumEntity.toAlbum() = Album(
    id = id,
    name = name ?: "",
    basename = basename ?: "",
    artists = artists,
    artist = MusicAttribute(id = artistId, name = artistName),
    artUrl = artUrl ?: "",
    songCount = songCount ?: 0,
    flag = flag ?: 0,
    time = time ?: 0,
    year = year ?: 0,
    genre = genre
)

fun Album.toAlbumEntity() = AlbumEntity(
    id = id,
    name = name ?: "",
    basename = basename ?: "",
    artists = artists, //Gson().toJson(MusicAttributesContainer(artists)) ?: "{}",
    artistId = artist.id,
    artistName = artist.name,
    artUrl = artUrl ?: "",
    songCount = songCount ?: 0,
    flag = flag ?: 0,
    time = time ?: 0,
    year = year ?: 0,
    genre = genre //Gson().toJson(MusicAttributesContainer(genre)) ?: "{}",
)
