package co.winds.smsotp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Spannable
import android.text.SpannableString
import android.text.TextUtils
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import java.util.concurrent.TimeUnit


/*New Activity Start*/
fun AppCompatActivity.startNewActivity(cls: Class<*>) {
    this.startActivity(Intent(this, cls))
    this.finish()
}

fun AppCompatActivity.startNewActivityNoFinish(cls: Class<*>) {
    this.startActivity(Intent(this, cls))
}

fun AppCompatActivity.startNewActivityFlag(cls: Class<*>) {
    val intent = Intent(this, cls)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    this.startActivity(intent)
}


fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

//Custom Snackbar
fun Context.showSnackBar(message: String): Snackbar {
    val sb = Snackbar.make((this as Activity).findViewById<View>(android.R.id.content), message, Snackbar.LENGTH_LONG)
    sb.view.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
    val textView = sb.view.findViewById<TextView>(android.support.design.R.id.snackbar_text)
    textView.setTextColor(Color.WHITE)
    sb.show()
    return sb
}


fun Context.log(message: String) {
    Log.d("TAGS", message)
}


fun setLanguageCode(lgCode: String): String? {
    return when (lgCode) {
        "en" -> "English"
        "hi" -> "हिंदी"
        "te" -> "తెలుగు"
        "ta" -> "தமிழ்"
        "ml" -> "മലയാളം"
        "gu" -> "ગુજરાતી"
        "as" -> "অসমীয়া"
        "bn" -> "বাঙালি"
        "kn" -> "ಕನ್ನಡ"
        "mr" -> "मराठी"
        "or" -> "ଓଡ଼ିଆ"
        else -> "English"
    }
}


fun Context.onCall(n: String) {
    if (!TextUtils.isEmpty(n)) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", n, null))
        startActivity(intent)
    }
}


fun Context.hideSoftKeyboard() {
    try {
        val inputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow((this as Activity).currentFocus!!.windowToken, 0)
    } catch (e: Exception) {
    }

}






fun minSecond(sec:Int):String{
    val millisUntilFinished =sec*1000.toLong()
    return String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) //On finish change timer text
}





fun Context.drawTextToBitmap(gResId: Int, gText: String): Bitmap {
    val resources = resources
    val scale = resources.displayMetrics.density
    var bitmap = BitmapFactory.decodeResource(resources, gResId)
    var bitmapConfig: android.graphics.Bitmap.Config? = bitmap.config

    if (bitmapConfig == null) {
        bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888
    }
    bitmap = bitmap.copy(bitmapConfig, true)
    val canvas = Canvas(bitmap)

    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    /* SET FONT COLOR (e.g. WHITE -> rgb(255,255,255)) */
    paint.color = Color.rgb(255, 255, 255)
    /* SET FONT SIZE (e.g. 15) */
    paint.textSize = (10 * scale).toInt().toFloat()
    /* SET SHADOW WIDTH, POSITION AND COLOR (e.g. BLACK) */
    paint.setShadowLayer(0f, 0f, 4f, Color.BLACK)

    val bounds = Rect()
    paint.getTextBounds(gText, 0, gText.length, bounds)
    val x = (bitmap.width - bounds.width()) / 2
    val y = (bitmap.height + bounds.height()) / 2.5
    canvas.drawText(gText, x.toFloat(), y.toFloat(), paint)


    return getResizedBitmap(bitmap, 110, 140)
}

fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
    val width = bm.width
    val height = bm.height
    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height
    // CREATE A MATRIX FOR THE MANIPULATION
    val matrix = Matrix()
    // RESIZE THE BIT MAP
    matrix.postScale(scaleWidth, scaleHeight)

    // "RECREATE" THE NEW BITMAP
    val resizedBitmap = Bitmap.createBitmap(
            bm, 0, 0, width, height, matrix, false
    )
    bm.recycle()
    return resizedBitmap
}


/*MyPartners */
fun textRating(data_rating: String): SpannableString {
    val spannableString = SpannableString(data_rating)
    spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#027418")), 8, 11, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    return spannableString
}

fun textRs(data_rs: String): SpannableString {
    val spannableString = SpannableString(data_rs)
    spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#101a4d")), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE).toString()
    return spannableString
}


fun textTnx(data_rs: String): SpannableString {
    val spannableString = SpannableString(data_rs)
    spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#101a4d")), 0, 12, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE).toString()
    return spannableString
}


fun Context.openPermissionSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.parse("package:" + this.packageName));
    intent.addCategory(Intent.CATEGORY_DEFAULT);
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
    this.startActivity(intent);
}
