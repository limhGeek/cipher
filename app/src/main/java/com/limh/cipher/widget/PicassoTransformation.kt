package com.limh.cipher.widget

import android.graphics.*
import com.squareup.picasso.Transformation

/**
 * Function:
 * author: limh
 * time:2017/12/5
 */
class PicassoTransformation : Transformation {

    private var radius = 0

    constructor(radius: Int) {
        this.radius = radius
    }


    override fun key(): String {
        return "roundcorner"
    }

    override fun transform(source: Bitmap): Bitmap {
        val widthLight = source.getWidth()
        val heightLight = source.getHeight()

        val output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888)

        val canvas = Canvas(output)
        val paintColor = Paint()
        paintColor.setFlags(Paint.ANTI_ALIAS_FLAG)

        val rectF = RectF(Rect(0, 0, widthLight, heightLight))
        if (radius == 0) {
            canvas.drawRoundRect(rectF, (widthLight / 2).toFloat(), (heightLight / 2).toFloat(), paintColor)
        } else {
            canvas.drawRoundRect(rectF, radius.toFloat(), radius.toFloat(), paintColor)
        }

        val paintImage = Paint()
        paintImage.setXfermode(PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP))
        canvas.drawBitmap(source, 0f, 0f, paintImage)
        source.recycle()
        return output
    }
}
