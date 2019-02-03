package com.limh.cipher.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CompoundButton
import android.widget.SeekBar
import android.widget.Toast
import com.limh.cipher.R
import com.limh.cipher.util.Logs
import kotlinx.android.synthetic.main.activity_build.*

/**
 * Function:
 * author: limh
 * time:2017/12/6
 */
class BuildActivity : AppCompatActivity(), View.OnClickListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener {

    private var length = 8
    private var array1 = mutableListOf("0&1&2&3&4&5&6&7&8&9")
    private var array2 = mutableListOf("a&b&c&d&e&f&g&h&i&j&k&l&m&n&o&p&q&r&s&t&u&v&w&x&y&z")
    private var array3 = mutableListOf("A&B&C&D&E&F&G&H&I&J&K&L&M&N&O&P&Q&R&S&T&U&V&W&X&Y&Z")
    private var array4 = mutableListOf("~&!&@&#&$&%&^&*&-&=&_&+&/&")

    private var base = mutableListOf(array1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_build)
        btnBuildYes.setOnClickListener(this)
        btnBuildCancel.setOnClickListener(this)
        seekBar.setOnSeekBarChangeListener(this)
        switchIsOrder.setOnCheckedChangeListener(this)
        check_1.setOnCheckedChangeListener(this)
        check_2.setOnCheckedChangeListener(this)
        check_3.setOnCheckedChangeListener(this)
        check_4.setOnCheckedChangeListener(this)
        createPassword()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnBuildYes -> {
                val intent = Intent()
                intent.putExtra("password", txtBuildResult.text.toString())
                setResult(0x123, intent)
                finish()
            }
            R.id.btnBuildCancel -> {
                finish()
            }
        }
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView!!.id) {
            R.id.switchIsOrder -> {
                if (isChecked) {
                    txtIsOrder.text = "是"
                } else {
                    txtIsOrder.text = "否"
                }
            }
            R.id.check_1 -> {
                if (isChecked) {
                    if (!base.contains(array1)) {
                        base.add(array1)
                    }
                } else {
                    if (base.size == 1) {
                        showShort("请选择最少一项")
                        buttonView.isChecked = true
                        return
                    }
                    if (base.contains(array1)) {
                        base.remove(array1)
                    }
                }
            }
            R.id.check_2 -> {
                if (isChecked) {
                    if (!base.contains(array2)) {
                        base.add(array2)
                    }
                } else {
                    if (base.size == 1) {
                        showShort("请选择最少一项")
                        buttonView.isChecked = true
                        return
                    }
                    if (base.contains(array2)) {
                        base.remove(array2)
                    }
                }
            }
            R.id.check_3 -> {
                if (isChecked) {
                    if (!base.contains(array3)) {
                        base.add(array3)
                    }
                } else {
                    if (base.size == 1) {
                        showShort("请选择最少一项")
                        buttonView.isChecked = true
                        return
                    }
                    if (base.contains(array3)) {
                        base.remove(array3)
                    }
                }
            }
            R.id.check_4 -> {
                if (isChecked) {
                    if (!base.contains(array4)) {
                        base.add(array4)
                    }
                } else {
                    if (base.size == 1) {
                        showShort("请选择最少一项")
                        buttonView.isChecked = true
                        return
                    }
                    if (base.contains(array4)) {
                        base.remove(array4)
                    }
                }
            }
        }
        createPassword()
        Logs.i("" + base.toString())

    }

    private fun createPassword() {
        var password = ""
        if (switchIsOrder.isChecked) {
            val itemSize:Int = length / base.size
            while (password.length < length){
                for (i in base){
                    for (j in 0 until itemSize){
                        val temp = i.toString().substring(1, i.toString().length - 1).split("&")
                        password += temp[(Math.random() * temp.size).toInt()]
                    }
                    if (password.length>=length)
                        break
                }
            }
        } else {
            while (password.length < length) {
                for (i in base) {
                    Logs.i("t=" + i)
                    val temp = i.toString().substring(1, i.toString().length - 1).split("&")
                    password += temp[(Math.random() * temp.size).toInt()]
                }
            }
        }
        Logs.d("密码长度：" + password.length)
        txtBuildResult.text = password
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {

    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (progress <= 4) {
            showShort("密码长度不小于4位")
            return
        }
        txtSeek.text = progress.toString()
        length = progress
        createPassword()
    }

    private fun showShort(msg: String) = Toast.makeText(this.applicationContext, msg, Toast.LENGTH_SHORT).show()
}