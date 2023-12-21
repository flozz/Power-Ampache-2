package luci.sixsixsix.powerampache2.presentation.album_detail

import luci.sixsixsix.powerampache2.domain.models.Song

data class AlbumDetailState (
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val searchQuery: String = "",
    val isFetchingMore: Boolean = false
)
