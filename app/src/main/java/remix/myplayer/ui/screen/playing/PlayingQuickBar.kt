package remix.myplayer.ui.screen.playing

import android.content.Context
import android.media.AudioManager
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import remix.myplayer.R
import remix.myplayer.service.Command
import remix.myplayer.util.Util
import remix.myplayer.util.ext.clickWithRipple
import remix.myplayer.viewmodel.timerViewModel

/**
 * Shortcut row above the seekbar: volume, favorite, timer
 */
@Composable
internal fun PlayingQuickBar(isFavorite: Boolean, swatch: Palette.Swatch) {
  val context = LocalContext.current
  val timerVM = timerViewModel
  val swatchColor = Color(swatch.rgb)

  Row(
    modifier = Modifier
      .padding(horizontal = 36.dp)
      .fillMaxWidth()
      .background(swatchColor.copy(0.1f), shape = RoundedCornerShape(24.dp))
      .padding(vertical = 6.dp),
    horizontalArrangement = Arrangement.SpaceEvenly,
    verticalAlignment = Alignment.CenterVertically
  ) {
    // 1) Volume: opens the phone's own volume panel
    Image(
      painter = painterResource(R.drawable.ic_volume_up_black_24dp),
      contentDescription = "QuickBarVolume",
      colorFilter = ColorFilter.tint(swatchColor),
      modifier = Modifier
        .clickWithRipple {
          val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
          audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_SAME,
            AudioManager.FLAG_SHOW_UI
          )
        }
        .padding(8.dp)
        .size(24.dp)
    )

    // 2) Favorite: add or remove the current song
    if (isFavorite) {
      Image(
        imageVector = Icons.Default.Favorite,
        contentDescription = "QuickBarFavorite",
        colorFilter = ColorFilter.tint(swatchColor),
        modifier = Modifier
          .clickWithRipple { Util.sendCMDLocalBroadcast(Command.LOVE) }
          .padding(8.dp)
          .size(24.dp)
      )
    } else {
      Image(
        painter = painterResource(R.drawable.ic_favorite_24dp),
        contentDescription = "QuickBarFavorite",
        colorFilter = ColorFilter.tint(swatchColor),
        modifier = Modifier
          .clickWithRipple { Util.sendCMDLocalBroadcast(Command.LOVE) }
          .padding(8.dp)
          .size(24.dp)
      )
    }

    // 3) Timer: opens the existing sleep timer
    Image(
      painter = painterResource(R.drawable.ic_timer_white_24dp),
      contentDescription = "QuickBarTimer",
      colorFilter = ColorFilter.tint(swatchColor),
      modifier = Modifier
        .clickWithRipple { timerVM.showTimerDialog() }
        .padding(8.dp)
        .size(24.dp)
    )
  }
}
