package com.vaca.mynfc

import android.content.Intent
import android.nfc.*
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.nio.charset.Charset
import java.util.*


class MActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_m)


    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val tag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        val ndefRecord = NdefRecord.createExternal("domain", "service", "content".toByteArray())
        val ndefMessage = NdefMessage(arrayOf(ndefRecord))
        val ndef = Ndef.get(tag) //获取Ndef tech的对象
        if (ndef != null) { //非NDEF数据
            try {
                ndef.connect()
                ndef.writeNdefMessage(ndefMessage)
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: FormatException) {
                e.printStackTrace()
            }
        } else { //可格式化为NDEF数据
            val ndefFormatable = NdefFormatable.get(tag)
            if (ndefFormatable != null) {
                try {
                    ndefFormatable.connect()
                    ndefFormatable.format(ndefMessage)
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: FormatException) {
                    e.printStackTrace()
                }
            }
        }
    }


//------------------------------------------------------tnf_well_known with rtd_text类型的数据
    fun createTextRecord(
        payload: String,
        locale: Locale,
        encodeInUtf8: Boolean
    ): NdefRecord {
        val langBytes: ByteArray = locale.language.toByteArray(Charset.forName("US-ASCII"))
        val utfEncoding: Charset =
            if (encodeInUtf8) Charset.forName("UTF-8") else Charset.forName("UTF-16")
        val textBytes: ByteArray = payload.toByteArray(utfEncoding)
        val utfBit = if (encodeInUtf8) 0 else 1 shl 7
        val status = (utfBit + langBytes.size).toChar()
        val data = ByteArray(1 + langBytes.size + textBytes.size)
        data[0] = status.toByte()
        System.arraycopy(langBytes, 0, data, 1, langBytes.size)
        System.arraycopy(textBytes, 0, data, 1 + langBytes.size, textBytes.size)
        return NdefRecord(
            NdefRecord.TNF_WELL_KNOWN,
            NdefRecord.RTD_TEXT, ByteArray(0), data
        )
    }

}