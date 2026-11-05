package remix.myplayer.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.hjq.permissions.Permission
import remix.myplayer.App
import remix.myplayer.BuildConfig

object PermissionUtil {
  private fun has(context: Context?, vararg permissions: String): Boolean {
    val ctx = context ?: runCatching { App.context }.getOrNull() ?: return false
    return permissions.all {
      ContextCompat.checkSelfPermission(
        ctx,
        it
      ) == PackageManager.PERMISSION_GRANTED
    }
  }

  fun hasNecessaryPermission(context: Context? = null): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      has(context, Permission.READ_MEDIA_AUDIO)
    } else {
      has(context, Permission.READ_EXTERNAL_STORAGE)
    }
  }

  @RequiresApi(Build.VERSION_CODES.R)
  fun hasManageExternalStorage(): Boolean {
    return Environment.isExternalStorageManager()
  }

  @RequiresApi(Build.VERSION_CODES.R)
  fun requestManageExternalStorage(context: Context) {
    context.startActivity(
      Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).setData(
        Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
      )
    )
    // TODO: show toast when "a matching Activity not exists"
  }
}