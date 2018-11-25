package com.example.essam.instgramfillter.Utilits

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream
import java.lang.Exception

object BitmapUtils {

    fun getBitmapFromAssets(context: Context, fileName: String, width: Int, heigth: Int): Bitmap? {
        val assetManager = context.assets
        val inputStream: InputStream
        val bitmap: Bitmap? = null
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            inputStream = assetManager.open(fileName)
            options.inSampleSize = calculateInSampleSize(options, width, heigth)

            options.inJustDecodeBounds = false
            return BitmapFactory.decodeStream(inputStream, null, options)


        } catch (e: IOException) {
            e.printStackTrace()

        }
        return null
    }

    fun getBitmapFromGallery(context: Context, path: Uri, width: Int, heigth: Int): Bitmap {
        val filePathColumns = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(path, filePathColumns, null, null, null)
        cursor.moveToFirst()
        val columeIndex = cursor.getColumnIndex(filePathColumns[0])
        val picturePath = cursor.getString(columeIndex)
        cursor.close()
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(picturePath, options)
        options.inSampleSize = calculateInSampleSize(options, width, heigth)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(picturePath, options)

    }

    fun insertImage(contentResolver: ContentResolver, source: Bitmap?, title: String, description: String): String? {
        val value = ContentValues()
        value.put(MediaStore.Images.Media.TITLE, title)
        value.put(MediaStore.Images.Media.DISPLAY_NAME, title)
        value.put(MediaStore.Images.Media.DESCRIPTION, description)
        value.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        value.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
        value.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())

        var url: Uri? = null
        var stringUrl: String? = null
        try {
            url = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, value)
            if (source != null) {
                val imageOut = contentResolver.openOutputStream(url!!)
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut)

                } finally {
                    imageOut!!.close()
                }
                val id = ContentUris.parseId(url)
                val miniThumb = MediaStore.Images.Thumbnails.getThumbnail(
                    contentResolver, id,
                    MediaStore.Images.Thumbnails.MINI_KIND, null
                )
                storeMiniThumb(contentResolver, miniThumb, id, 50f, 50f, MediaStore.Images.Thumbnails.MICRO_KIND)

            } else {
                contentResolver.delete(url!!, null, null)
                url = null
            }

        } catch (e: Exception) {
            if (url != null) {
                contentResolver.delete(url!!, null, null)
                url = null
            }
            e.printStackTrace()
        }
        if (url != null) {
            stringUrl = url.toString()
        }
        return stringUrl
    }

    private fun storeMiniThumb(
        contentResolver: ContentResolver, source: Bitmap?, id: Long, width: Float, heigth: Float, micrO_KIND: Int
    ): Bitmap? {
        val matrix = Matrix()
        val scaleX = width / source!!.width
        val scaleY = heigth / source!!.height
        matrix.setScale(scaleX, scaleY)
        val thumb = Bitmap.createBitmap(source, 0, 0, source!!.width, source!!.height, matrix, true)

        val value = ContentValues(4)
        value.put(MediaStore.Images.Thumbnails.KIND, micrO_KIND)
        value.put(MediaStore.Images.Thumbnails.IMAGE_ID, id.toInt())
        value.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.height)
        value.put(MediaStore.Images.Thumbnails.WIDTH, thumb.width)

        val url = contentResolver.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, value)
        try {
            val thumbOut = contentResolver.openOutputStream(url!!)
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut)
            thumbOut!!.close()
            return thumb
        } catch (f: FileNotFoundException) {
            return null
            f.printStackTrace()
        } catch (e: IOException) {
            return null
            e.printStackTrace()
        }
    }


    private fun calculateInSampleSize(options: BitmapFactory.Options, width: Int, height: Int): Int {
        val outheight = options.outHeight
        val outwidht = options.outWidth

        var inSampleSize = 1
        if (outheight > height || outwidht > width) {
            val halfHeight = outheight / 2
            val halfwidth = outwidht / 2
            while (halfHeight / inSampleSize >= height && halfwidth / inSampleSize >= height) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

}