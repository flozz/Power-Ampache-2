package luci.sixsixsix.powerampache2.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import luci.sixsixsix.powerampache2.common.Constants
import luci.sixsixsix.powerampache2.common.Resource
import luci.sixsixsix.powerampache2.data.local.MusicDatabase
import luci.sixsixsix.powerampache2.data.local.entities.toPlaylist
import luci.sixsixsix.powerampache2.data.local.entities.toPlaylistEntity
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.data.remote.dto.toError
import luci.sixsixsix.powerampache2.data.remote.dto.toPlaylist
import luci.sixsixsix.powerampache2.domain.MusicRepository
import luci.sixsixsix.powerampache2.domain.PlaylistsRepository
import luci.sixsixsix.powerampache2.domain.errors.ErrorHandler
import luci.sixsixsix.powerampache2.domain.errors.MusicException
import luci.sixsixsix.powerampache2.domain.models.Playlist
import luci.sixsixsix.powerampache2.domain.models.Session
import javax.inject.Inject
import javax.inject.Singleton

/**
 * the source of truth is the database, stick to the single source of truth pattern, only return
 * data from database, when making a network call first insert data into db then read from db and
 * return/emit data.
 * When breaking a rule please add a comment with a TODO: BREAKING_RULE
 */
@Singleton
class PlaylistsRepositoryImpl @Inject constructor(
    private val api: MainNetwork,
    private val db: MusicDatabase,
    private val musicRepository: MusicRepository,
    private val errorHandler: ErrorHandler
): PlaylistsRepository {
    private val dao = db.dao
    private fun getSession(): Session? = musicRepository.sessionLiveData.value

    override suspend fun getPlaylists(
        fetchRemote: Boolean,
        query: String,
        offset: Int
    ): Flow<Resource<List<Playlist>>> = flow {
        emit(Resource.Loading(true))

        if (offset == 0) {
            val localPlaylists = dao.searchPlaylists(query)
            val isDbEmpty = localPlaylists.isEmpty() && query.isEmpty()
            if (!isDbEmpty) {
                emit(Resource.Success(data = localPlaylists.map { it.toPlaylist() }))
            }
            val shouldLoadCacheOnly = !isDbEmpty && !fetchRemote
            if(shouldLoadCacheOnly) {
                emit(Resource.Loading(false))
                return@flow
            }
        }

        val auth = getSession()!!
        val response = api.getPlaylists(auth.auth, filter = query, offset = offset)
        response.error?.let { throw(MusicException(it.toError())) }
        val playlists = response.playlist!!.map { it.toPlaylist() } // will throw exception if playlist null

        if (query.isNullOrBlank() && offset == 0 && Constants.CLEAR_TABLE_AFTER_FETCH) {
            // if it's just a search do not clear cache
            dao.clearPlaylists()
        }
        dao.insertPlaylists(playlists.map { it.toPlaylistEntity() })
        // stick to the single source of truth pattern despite performance deterioration
        emit(Resource.Success(data = dao.searchPlaylists(query).map { it.toPlaylist() }, networkData = playlists))
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("getPlaylists()", e, this) }

    private suspend fun like(id: String, like: Boolean, type: MainNetwork.Type): Flow<Resource<Any>> = flow {
        emit(Resource.Loading(true))
        val auth = getSession()!!
        api.flag(
            authKey = auth.auth,
            id = id,
            flag = if (like) { 1 } else { 0 },
            type = type).apply {

            if (error != null) {
                // TODO rewrite using MusicException
                throw Exception(error.toString())
            } else if (success != null) {
                emit(Resource.Success(data = Any(), networkData = Any()))
            }else {
                throw Exception("error getting a response from FLAG/LIKE call")
            }
        }
        emit(Resource.Loading(false))
    }.catch { e -> errorHandler("likeSong()", e, this) }

    override suspend fun likeSong(id: String, like: Boolean): Flow<Resource<Any>> = like(id, like, MainNetwork.Type.song)

    override suspend fun likeAlbum(id: String, like: Boolean): Flow<Resource<Any>> = like(id, like, MainNetwork.Type.album)

    override suspend fun likeArtist(id: String, like: Boolean): Flow<Resource<Any>> = like(id, like, MainNetwork.Type.artist)

    override suspend fun likePlaylist(id: String, like: Boolean): Flow<Resource<Any>> = like(id, like, MainNetwork.Type.playlist)
}
