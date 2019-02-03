package com.limh.cipher.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


/**
 * Function:二维码工具类
 * author: limh
 * time:2017/12/6
 */
object QRCodeUtils {

    fun generateBitmap(content: String, width: Int, height: Int): Bitmap? {
        val qrcodeWriter = QRCodeWriter()
        val hints: MutableMap<EncodeHintType, String> = HashMap()
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8")
        try {
            val encode = qrcodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints)
            val pixels = IntArray(width * height)
            for (i in 0 until height) {
                for (j in 0 until width) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000
                    } else {
                        pixels[i * width + j] = -0x1
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
        return null
    }

    fun addLogo(qrBitmap: Bitmap,logoBitmap: Bitmap):Bitmap?{
        val qrBitmapWidth=qrBitmap.width
        val qrBitmapHeigth=qrBitmap.height
        val logoBitmapWidth=logoBitmap.width
        val logoBitmapHeigth=logoBitmap.height
        val blankBitmap=Bitmap.createBitmap(qrBitmapWidth,qrBitmapHeigth,Bitmap.Config.ARGB_8888)
        val canvas=Canvas(blankBitmap)
        canvas.drawBitmap(qrBitmap,0f,0f,null)
        canvas.save(Canvas.ALL_SAVE_FLAG)
        var scaleSize=1.0f
        while ((logoBitmapWidth/scaleSize)>(qrBitmapWidth/5)||(logoBitmapHeigth/scaleSize)>(qrBitmapHeigth/5)){
            scaleSize*=2
        }
        val sx=1.0f/scaleSize
        canvas.scale(sx,sx,qrBitmapWidth/2f,qrBitmapHeigth/2f)
        canvas.drawBitmap(logoBitmap,(qrBitmapWidth-logoBitmapWidth)/2f,(qrBitmapHeigth-logoBitmapHeigth)/2f,null)
        canvas.restore()
        return blankBitmap
    }

    /**
     * 保存图片
     */
    fun savePicture(bm: Bitmap?, fileName: String) {
        if (null == bm) {
            return
        }
        val foder = File(Environment.getExternalStorageDirectory().absolutePath + "/Download")
        if (!foder.exists()) {
            foder.mkdirs()
        }
        val myCaptureFile = File(foder, fileName)
        try {
            if (!myCaptureFile.exists()) {
                myCaptureFile.createNewFile()
            }
            val bos = BufferedOutputStream(FileOutputStream(myCaptureFile))
            //压缩保存到本地
            bm.compress(Bitmap.CompressFormat.JPEG, 90, bos)
            bos.flush()
            bos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}