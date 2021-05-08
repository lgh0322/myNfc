package com.vaca.mynfc

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.MifareClassic
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        var card_info: String? = ""
        val action = intent.action // 获取到本次启动的action
        if (action == NfcAdapter.ACTION_NDEF_DISCOVERED || action == NfcAdapter.ACTION_TECH_DISCOVERED || action == NfcAdapter.ACTION_TAG_DISCOVERED) { // 未知类型
            // 从intent中读取NFC卡片内容
             Log.e("fuck", "迪斯科浪费就考虑  $action")
            val tag: Tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)!!
            // 获取NFC卡片的序列号
            val ids: ByteArray = tag.id
            for(k in ids){
                println("的斯洛伐克"+k.toUByte().toInt().toString()+ intent.data.toString())
            }


        }
    }

    // 读取小区门禁卡信息
    fun readGuardCard(tag: Tag?): String? {
        val classic = MifareClassic.get(tag)
        var info: String
        try {
            classic.connect() // 连接卡片数据
            val type = classic.type //获取TAG的类型
            val typeDesc: String
            typeDesc = if (type == MifareClassic.TYPE_CLASSIC) {
                "传统类型"
            } else if (type == MifareClassic.TYPE_PLUS) {
                "增强类型"
            } else if (type == MifareClassic.TYPE_PRO) {
                "专业类型"
            } else {
                "未知类型"
            }
            info = String.format(
                "\t卡片类型：%s\n\t扇区数量：%d\n\t分块个数：%d\n\t存储空间：%d字节",
                typeDesc, classic.sectorCount, classic.blockCount, classic.size
            )
        } catch (e: Exception) {
            e.printStackTrace()
            info = e.message.toString()
        } finally { // 无论是否发生异常，都要释放资源
            try {
                classic.close() // 释放卡片数据
            } catch (e: Exception) {
                e.printStackTrace()
                info = e.message.toString()
            }
        }
        return info
    }
}