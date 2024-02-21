package luci.sixsixsix.powerampache2.presentation.main.screens.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import kotlinx.coroutines.launch
import luci.sixsixsix.powerampache2.R
import luci.sixsixsix.powerampache2.presentation.main.viewmodel.MainViewModel
import luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail.components.SongDetailTopBar
import luci.sixsixsix.powerampache2.presentation.screens_detail.song_detail.components.MiniPlayer

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun SheetDragHandle(
    scaffoldState: BottomSheetScaffoldState,
    mainViewModel: MainViewModel
) {
    val scope = rememberCoroutineScope()
    val barHeight = dimensionResource(id = R.dimen.miniPlayer_height)

    Box(modifier = Modifier
        .height(dimensionResource(id = R.dimen.miniPlayer_height))
        .fillMaxWidth()
    ) {
        // show mini-player
        Box(modifier = Modifier
            .height(barHeight)
            .fillMaxWidth()
            .clickable {
                scope.launch {
                    if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
                        scaffoldState.bottomSheetState.partialExpand() //only peek
                    } else {
                        scaffoldState.bottomSheetState.expand()
                    }
                }
            }
        ) {
            AnimatedVisibility(visible = scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded,
                enter = slideInVertically(),
                exit = fadeOut(spring(stiffness = Spring.StiffnessHigh))
            ) {
                SongDetailTopBar(mainViewModel = mainViewModel)
            }
            AnimatedVisibility(
                visible = scaffoldState.bottomSheetState.currentValue != SheetValue.Expanded,
                enter = slideInVertically(initialOffsetY = { it / 2 }),
                exit = slideOutVertically()
            ) {
                MiniPlayer(mainViewModel = mainViewModel)
            }

//            if (scaffoldState.bottomSheetState.currentValue == SheetValue.Expanded) {
//                SongDetailTopBar(mainViewModel = mainViewModel)
//            } else {
//                MiniPlayer(mainViewModel = mainViewModel)
//            }
        }
    }
}
