package luci.sixsixsix.powerampache2.presentation.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import luci.sixsixsix.powerampache2.data.remote.MainNetwork
import luci.sixsixsix.powerampache2.presentation.common.RoundedCornerButton

@Composable
fun NewPlaylistDialog(
    onConfirm: (playlistName: String, playlistType: MainNetwork.PlaylistType) -> Unit,
    onCancel: () -> Unit
) {
    var playlistName by remember { mutableStateOf("") }
    val playlistType by remember { mutableStateOf(MainNetwork.PlaylistType.private) }
    val radioOptions = listOf(MainNetwork.PlaylistType.private, MainNetwork.PlaylistType.public)
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0] ) }

    Dialog(onDismissRequest = { onCancel() }) {
        Card(
            modifier = Modifier
                .padding(16.dp),
            shape = RoundedCornerShape(8.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(top = 30.dp, bottom = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                OutlinedTextField(
                    value = playlistName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    onValueChange = {
                        playlistName = it
                    },
                    singleLine = true,
                    placeholder = {
                        Text(
                            text = "Playlist Name",
                            modifier = Modifier
                            //    .wrapContentSize(Alignment.Center)
                                .padding(vertical = 0.dp),
                            //textAlign = TextAlign.Center,
                            //fontWeight = FontWeight.Bold,
                            //fontSize = 15.sp
                        )
                    }
                )

                Spacer(modifier = Modifier.padding(top = 20.dp))

                PlaylistTypeRadioButtons(
                    radioOptions = radioOptions,
                    selectedOption = selectedOption,
                    onOptionSelected = onOptionSelected
                )

                Spacer(modifier = Modifier.padding(top = 20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    RoundedCornerButton("CREATE") {
                        onConfirm(playlistName,playlistType)
                    }

                    RoundedCornerButton("CANCEL") {
                        onCancel()
                    }
                }
            }
        }
    }
}

@Composable
fun PlaylistTypeRadioButtons(
    radioOptions: List<MainNetwork.PlaylistType>,
    selectedOption: MainNetwork.PlaylistType,
    onOptionSelected: (MainNetwork.PlaylistType) -> Unit
) {

    Row(
        Modifier
            .fillMaxWidth()) {
        radioOptions.forEach { type ->
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1.0f)
                    .selectable(
                        selected = (type == selectedOption),
                        onClick = {
                            onOptionSelected(type)
                        }
                    )
                    .padding(horizontal = 16.dp)
            ) {
                RadioButton(
                    modifier = Modifier
                        .wrapContentSize(Alignment.Center),
                    selected = (type == selectedOption),
                    onClick = { onOptionSelected(type) }
                )
                Text(
                    text = type.name.capitalize(),
                   // modifier = Modifier
                        //.wrapContentSize(Alignment.Center)
                        //.padding(vertical = 5.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 17.sp
                )
            }
        }
    }
}

@Composable @Preview
fun PreviewNewPlaylistDialog() {
    NewPlaylistDialog(
        onCancel = {},
        onConfirm = { name, type ->

        }
    )
}
