import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.InputStream

object URIl {
    private const val MAX_IMAGE_SIZE = 400 * 1024 // 400 KB

    fun compressImage(context: Context, imageUri: Uri): Uri? {
        var inputStream: InputStream = context.contentResolver.openInputStream(imageUri) ?: return null

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(inputStream, null, options)
        inputStream.close()

        val outputStream = ByteArrayOutputStream()
        var bitmap: Bitmap? = null
        var compression = 90

        inputStream = context.contentResolver.openInputStream(imageUri) ?: return null

        do {
            options.inSampleSize = calculateInSampleSize(options, 1024, 1024)
            options.inJustDecodeBounds = false
            bitmap = BitmapFactory.decodeStream(inputStream, null, options)
            outputStream.reset()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, compression, outputStream)
            compression -= 10
        } while (outputStream.toByteArray().size > MAX_IMAGE_SIZE && compression > 0)

        bitmap?.recycle()
        inputStream.close()
        outputStream.close()

        val compressedImageBytes = outputStream.toByteArray()
        val compressedImageUri = saveCompressedImage(context, compressedImageBytes)
        return compressedImageUri
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun saveCompressedImage(context: Context, compressedImageBytes: ByteArray): Uri {
        val outputStream = context.contentResolver.openOutputStream(createTempImageUri(context))
        outputStream?.write(compressedImageBytes)
        outputStream?.close()
        return createTempImageUri(context)
    }

    private fun createTempImageUri(context: Context): Uri {
        val tempFileName = "compressed_image_${System.currentTimeMillis()}.jpg"
        return Uri.fromFile(context.cacheDir.resolve(tempFileName))
    }
}
