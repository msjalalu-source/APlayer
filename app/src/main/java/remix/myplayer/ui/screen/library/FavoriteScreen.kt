package remix.myplayer.ui.screen.library

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import remix.myplayer.service.Command
import remix.myplayer.service.MusicService
import remix.myplayer.service.MusicServiceRemote.setPlayQueue
import remix.myplayer.ui.widget.library.SongListHeader
import remix.myplayer.ui.widget.library.list.ListSong
import remix.myplayer.util.MusicUtil
import remix.myplayer.util.ext.verticalScrollbar
import remix.myplayer.viewmodel.MultiSelectState
import remix.myplayer.viewmodel.libraryViewModel
import remix.myplayer.viewmodel.mainViewModel
import remix.myplayer.viewmodel.playbackViewModel

/**
 * Favorite tab: shows the songs of the favorite list, reusing the Songs tab layout
 */
@Composable
fun FavoriteScreen() {
  val libraryVM = libraryViewModel
  val mainVM = mainViewModel

  val playbackState by playbackViewModel.playbackUiState.collectAsStateWithLifecycle()
  val multiSelectState by mainVM.multiSelectState.collectAsStateWithLifecycle()
  val listState = rememberLazyListState()
  val allSongs by libraryVM.songs.collectAsStateWithLifecycle()
  val playLists by libraryVM.playLists.collectAsStateWithLifecycle()
  val context = LocalContext.current
  val popupEnabled = !multiSelectState.isShowInLibrary()

  val songs = remember(allSongs, playLists) {
    val ids = playLists.firstOrNull { it.isFavorite() }?.audioIds ?: emptyList()
    val byId = allSongs.associateBy { it.id }
    ids.mapNotNull { byId[it] }
  }

  Column {
    if (songs.isNotEmpty()) {
      SongListHeader(songs)
    }

    val selectedIds by remember {
      derivedStateOf {
        multiSelectState.selectedModels(MultiSelectState.Where.Song)
      }
    }

    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .verticalScrollbar(listState)
    ) {
      itemsIndexed(songs, key = { _, song ->
        song.id
      }) { pos, song ->
        val selected = selectedIds.contains(song.getKey())
        val isPlayingSong = playbackState.song.id == song.id

        ListSong(
          modifier = Modifier.height(64.dp),
          song = song,
          modelParent = song,
          selected = selected,
          playing = isPlayingSong,
          popupEnabled = popupEnabled,
          onClickSong = {
            if (songs.isEmpty()) {
              return@ListSong
            }

            if (multiSelectState.where == MultiSelectState.Where.Song) {
              mainVM.updateMultiSelectModel(song)
              return@ListSong
            }

            setPlayQueue(
              songs, MusicUtil.makeCmdIntent(Command.PLAY_AT)
                .putExtra(MusicService.EXTRA_POSITION, pos)
            )
          },
          onLongClickSong = {
            mainVM.showMultiSelect(context, MultiSelectState.Where.Song, song)
          })
      }
    }
  }
}
