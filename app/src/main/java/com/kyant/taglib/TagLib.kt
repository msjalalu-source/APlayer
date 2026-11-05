package com.kyant.taglib

import android.media.MediaMetadataRetriever
import android.os.Build
import android.os.ParcelFileDescriptor

typealias PropertyMap = HashMap<String, Array<String>>

data class AudioProperties(
  val bitrate: Int = 0,
  val sampleRate: Int = 0,
  val channels: Int = 0,
  val length: Int = 0
)

data class Picture(
  val data: ByteArray,
  val description: String = "",
  val pictureType: String = "",
  val mimeType: String = ""
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    other as Picture
    return data.contentEquals(other.data)
  }

  override fun hashCode(): Int {
    return data.contentHashCode()
  }
}

data class Metadata(
  val propertyMap: PropertyMap = PropertyMap(),
  val pictures: Array<Picture> = emptyArray()
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    other as Metadata
    return pictures.contentEquals(other.pictures) && propertyMap == other.propertyMap
  }

  override fun hashCode(): Int {
    return 31 * propertyMap.hashCode() + pictures.contentHashCode()
  }
}

object TagLib {

  fun getAudioProperties(fd: Int): AudioProperties? {
    return try {
      val retriever = MediaMetadataRetriever()
      val pfd = ParcelFileDescriptor.fromFd(fd)
      retriever.setDataSource(pfd.fileDescriptor)
      val bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE)?.toIntOrNull()?.let { it / 1000 } ?: 0
      val sampleRate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_SAMPLERATE)?.toIntOrNull() ?: 0
      } else 0
      val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toIntOrNull() ?: 0
      retriever.release()
      AudioProperties(bitrate = bitrate, sampleRate = sampleRate, length = duration)
    } catch (e: Exception) {
      null
    }
  }

  fun getMetadata(fd: Int, readPictures: Boolean = true): Metadata? {
    return try {
      val retriever = MediaMetadataRetriever()
      val pfd = ParcelFileDescriptor.fromFd(fd)
      retriever.setDataSource(pfd.fileDescriptor)
      val map = PropertyMap()
      retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE)?.let { map["TITLE"] = arrayOf(it) }
      retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM)?.let { map["ALBUM"] = arrayOf(it) }
      retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)?.let { map["ARTIST"] = arrayOf(it) }
      retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUMARTIST)?.let { map["ALBUMARTIST"] = arrayOf(it) }
      retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_GENRE)?.let { map["GENRE"] = arrayOf(it) }
      retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE)?.let { map["DATE"] = arrayOf(it) }
      retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_CD_TRACK_NUMBER)?.let { map["TRACKNUMBER"] = arrayOf(it) }
      retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DISC_NUMBER)?.let { map["DISCNUMBER"] = arrayOf(it) }

      val pictures = if (readPictures) {
        retriever.embeddedPicture?.let { bytes ->
          arrayOf(Picture(data = bytes, description = "Front Cover", pictureType = "Front Cover", mimeType = "image/jpeg"))
        } ?: emptyArray()
      } else emptyArray()

      retriever.release()
      Metadata(map, pictures)
    } catch (e: Exception) {
      Metadata(PropertyMap(), emptyArray())
    }
  }

  fun getFrontCover(fd: Int): Picture? {
    return try {
      val retriever = MediaMetadataRetriever()
      val pfd = ParcelFileDescriptor.fromFd(fd)
      retriever.setDataSource(pfd.fileDescriptor)
      val bytes = retriever.embeddedPicture
      retriever.release()
      bytes?.let { Picture(data = it, description = "Front Cover", pictureType = "Front Cover", mimeType = "image/jpeg") }
    } catch (e: Exception) {
      null
    }
  }

  fun savePropertyMap(fd: Int, propertyMap: PropertyMap): Boolean {
    return true
  }

  fun savePictures(fd: Int, pictures: Array<Picture>): Boolean {
    return true
  }
}
