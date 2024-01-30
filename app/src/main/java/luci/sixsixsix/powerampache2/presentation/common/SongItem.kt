package luci.sixsixsix.powerampache2.presentation.common

import android.os.Parcelable
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.DownloadDone
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlinx.parcelize.Parcelize
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.common.fontDimensionResource
import luci.sixsixsix.powerampache2.domain.models.Song
import luci.sixsixsix.powerampache2.domain.models.totalTime

enum class SongInfoThirdRow { AlbumTitle, Time }

enum class SubtitleString { NOTHING, ARTIST, ALBUM }

enum class SongItemEvent {
    PLAY_NEXT,
    SHARE_SONG,
    DOWNLOAD_SONG,
    GO_TO_ALBUM,
    GO_TO_ARTIST,
    ADD_SONG_TO_QUEUE,
    ADD_SONG_TO_PLAYLIST,
}

@Parcelize
data class SongWrapper(
    val song: Song,
    val isOffline: Boolean
): Parcelable

@Composable
fun SongItem(
    song: Song,
    songItemEventListener: (songItemEvent: SongItemEvent) -> Unit,
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false,
    isSongDownloaded: Boolean = false,
    subtitleString: SubtitleString = SubtitleString.ARTIST,
    songInfoThirdRow: SongInfoThirdRow = SongInfoThirdRow.AlbumTitle,
    enableSwipeToRemove: Boolean = false,
    onRemove: (Song) -> Unit = {},
    onRightToLeftSwipe: (Song) -> Unit = {}
) {
    SwipeToDismissItem(
        item = song,
        foregroundView = {
            SongItemMain(song, songItemEventListener, modifier, isLandscape, isSongDownloaded, subtitleString, songInfoThirdRow)
        },
        enableSwipeToRemove = enableSwipeToRemove,
        onRemove = onRemove,
        onRightToLeftSwipe = onRightToLeftSwipe
    )
}

@Composable
fun SongItemMain(
    song: Song,
    songItemEventListener: (songItemEvent: SongItemEvent) -> Unit,
    modifier: Modifier = Modifier,
    isLandscape: Boolean = false,
    isSongDownloaded: Boolean = false,
    subtitleString: SubtitleString = SubtitleString.ARTIST,
    songInfoThirdRow: SongInfoThirdRow = SongInfoThirdRow.AlbumTitle
) {
    var isContextMenuVisible by rememberSaveable { mutableStateOf(false) }
    var pressOffset by remember { mutableStateOf(DpOffset.Zero) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimensionResource(id = R.dimen.songItem_row_paddingHorizontal),
                vertical = dimensionResource(id = R.dimen.songItem_row_paddingVertical)
            )
    ) {
        if(!isLandscape) {
            Card(
                border = BorderStroke(
                    width = dimensionResource(id = R.dimen.songItem_card_borderStroke),
                    color = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier
                    .weight(if (subtitleString == SubtitleString.NOTHING) 0.7f else 1f)
                    .background(Color.Transparent)
                    .align(Alignment.CenterVertically),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                ),
                elevation = CardDefaults.cardElevation(1.dp),
                shape = RoundedCornerShape(dimensionResource(id = R.dimen.songItem_card_cornerRadius))
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    AsyncImage(
                        model = song.imageUrl,
                        contentScale = ContentScale.FillWidth,
                        placeholder = painterResource(id = R.drawable.placeholder_album),
                        error = painterResource(id = R.drawable.ic_playlist),
                        contentDescription = song.title,
                    )
                if(isSongDownloaded) {
                        Card(
                            //colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.),
                            modifier = Modifier.size(20.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .wrapContentSize()
                                    .padding(2.dp)
                                    .background(Color.Transparent),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.DownloadDone,
                                    contentDescription = "download done",
                                    //tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                }
}
            }
        }
        Spacer(modifier = Modifier
                .width(dimensionResource(R.dimen.songItem_infoTextSection_spacer)))

        InfoTextSection(
            modifier = Modifier
                .weight(5f)
                .padding(
                    horizontal = dimensionResource(R.dimen.songItem_infoTextSection_paddingHorizontal),
                    vertical = dimensionResource(R.dimen.songItem_infoTextSection_paddingVertical)
                )
                .align(Alignment.CenterVertically),
            song = song,
            subtitleString = subtitleString,
            songInfoThirdRow = songInfoThirdRow
        )

        Image(
            alignment = Alignment.Center,
            imageVector = Icons.Outlined.MoreVert,
            contentDescription = stringResource(id = R.string.menu_content_description),
            modifier = Modifier
                .background(Color.Transparent)
                .weight(0.5f)
                .pointerInput(true) {
                    detectTapGestures(
                        onPress = { offset ->
                            pressOffset = DpOffset(offset.x.toDp(), offset.y.toDp())
                            isContextMenuVisible = true
                        })
                },
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
        )

        Spacer(modifier = Modifier
            .width(15.dp))
    }

    Spacer(modifier = Modifier
            .width(dimensionResource(R.dimen.songItem_infoTextSection_spacer) * 2))

    SongDropDownMenu(
        isContextMenuVisible = isContextMenuVisible,
        pressOffset = pressOffset,
        songItemEventListener = {
            isContextMenuVisible = false
            songItemEventListener(it)
        },
        onDismissRequest = {
            isContextMenuVisible = false
        })
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InfoTextSection(
    modifier: Modifier,
    song: Song,
    subtitleString: SubtitleString,
    songInfoThirdRow: SongInfoThirdRow = SongInfoThirdRow.AlbumTitle
) {
    val songInfoThirdRowText = when(songInfoThirdRow) {
        SongInfoThirdRow.AlbumTitle -> song.album.name
        SongInfoThirdRow.Time -> song.totalTime()
    }

    Column(
        modifier = modifier
    ) {
        Text(
            modifier = Modifier.basicMarquee(),
            text = song.title,
            fontWeight = FontWeight.Normal,
            fontSize = fontDimensionResource(R.dimen.songItem_infoTextSection_textSize_title),
            maxLines = 1,
        )
        Spacer(modifier = Modifier
                .width(dimensionResource(R.dimen.songItem_infoTextSection_spacer)))

        when(subtitleString) {
            SubtitleString.NOTHING -> {

            }
            SubtitleString.ARTIST -> Text(
                modifier = Modifier.basicMarquee(),
                text = song.artist.name,
                fontWeight = FontWeight.Light,
                fontSize = fontDimensionResource(R.dimen.songItem_infoTextSection_textSize_artist),
                maxLines = 1,
                textAlign = TextAlign.Start
            )
            SubtitleString.ALBUM -> Text(
                modifier = Modifier.basicMarquee(),
                text = song.album.name,
                fontWeight = FontWeight.Light,
                fontSize = fontDimensionResource(R.dimen.songItem_infoTextSection_textSize_artist),
                maxLines = 1,
                textAlign = TextAlign.Start
            )
        }

        Spacer(modifier = Modifier
                .width(dimensionResource(R.dimen.songItem_infoTextSection_spacer)))
        Text(
            modifier = Modifier.basicMarquee(),
            text = songInfoThirdRowText,
            fontWeight = FontWeight.Light,
            fontSize = fontDimensionResource(R.dimen.songItem_infoTextSection_textSize_album),
            maxLines = 1,
            textAlign = TextAlign.Start
        )
    }
}

@Preview
@Composable
fun SongItemPreview() {
    SongItem(
        song = Song.mockSong,
        songItemEventListener = {},
        subtitleString = SubtitleString.NOTHING,
        isSongDownloaded = true
    ) {

    }
}
