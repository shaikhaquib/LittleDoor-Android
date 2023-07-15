import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.devative.littledoor.BuildConfig

object FilePickerUtils {
    const val PICK_FILE_REQUEST_CODE = 123

    // Utility function to check if the required READ_EXTERNAL_STORAGE permission is granted
    private fun isReadStoragePermissionGranted(activity: Activity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    // Utility function to request the READ_EXTERNAL_STORAGE permission
    private fun requestReadStoragePermission(activity: Activity) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            PICK_FILE_REQUEST_CODE
        )
    }

    // Utility function to pick a file using the file manager
    fun pickFileFromFileManager(activity: Activity) {
        // Check if the READ_EXTERNAL_STORAGE permission is granted
        if (isReadStoragePermissionGranted(activity) || Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Create an intent to pick a file
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
            // Start the activity to pick the file
            activity.startActivityForResult(intent, PICK_FILE_REQUEST_CODE)
        } else {
            // Request the READ_EXTERNAL_STORAGE permission
            requestReadStoragePermission(activity)
        }
    }

    // Handle the permission request result
    fun handlePermissionRequestResult(
        requestCode: Int,
        grantResults: IntArray,
        activity: Activity
    ) {
        if (requestCode == PICK_FILE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, pick the file
                pickFileFromFileManager(activity)
            } else {
                // Permission denied, show a message or handle accordingly
                // You can display a Toast message indicating that the permission is required
            }
        }
    }

    // Utility function to get the file name from the URI
    fun getFileNameFromUri(uri: Uri, activity: Activity): String? {
        var fileName: String? = null
        val cursor = activity.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    val displayName = it.getString(displayNameIndex)
                    fileName = displayName
                }
            }
        }
        return fileName
    }

    fun getFullPathFromUri(uri: Uri, activity: Activity): String? {
        var fullPath: String? = null
        val projection = arrayOf(MediaStore.MediaColumns.DATA)
        val cursor = activity.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(MediaStore.MediaColumns.DATA)
                if (columnIndex != -1) {
                    fullPath = it.getString(columnIndex)
                }
            }
        }
        return fullPath
    }


}
