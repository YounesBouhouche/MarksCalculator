package com.example.markscalculator

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.Serializable

class Calculation(var level: String, var name: String = "", var sport : Boolean = true, var art : Boolean = true, var tamazight : Boolean = true) : Serializable {
    val subjects = when (level) {
        "1ap", "2ap" -> listOf("Arabic", "Math", "Islamic", "Tamazight", "Art", "Sport")
        "3ap", "4ap", "5ap" -> listOf("Arabic", "Math", "English", "Hist. Geo", "Islamic", "Art", "Tamazight", "Sport")
        "1am", "2am", "3am", "4am", "bem" -> listOf("Arabic", "Math", "French", "English", "Physics", "Sciences", "Hist. Geo", "Islamic", "Art", "Tamazight", "Sport")
        "1as_sci" -> listOf("Arabic", "Math", "French", "English", "Physics", "Sciences", "Hist. Geo", "Islamic", "Technology", "Informatic", "Art", "Tamazight", "Sport")
        "1as_lit" -> listOf("Arabic", "Math", "French", "English", "Physics", "Sciences", "Hist. Geo", "Islamic", "Informatic", "Art", "Tamazight", "Sport")
        "2as_math", "2as_sci" -> listOf("Arabic", "Math", "French", "English", "Physics", "Sciences", "Hist. Geo", "Islamic", "Art", "Tamazight", "Sport")
        "2as_math_tech"-> listOf("Arabic", "Math", "French", "English", "Physics", "Technology", "Hist. Geo", "Islamic", "Art", "Tamazight", "Sport")
        "2as_philo" -> listOf("Arabic", "Math", "French", "English", "Hist. Geo", "Sciences", "Physics", "Islamic", "Philosophy", "Art", "Tamazight", "Sport")
        "2as_lang" -> listOf("Arabic", "Math", "French", "English", "3rd language", "Hist. Geo", "Islamic", "Art", "Tamazight", "Sport")
        "2as_mng", "3as_mng" -> listOf("Arabic", "Math", "French", "English", "Hist. Geo", "Islamic", "Economy", "Law", "Management", "Tamazight", "Sport")
        "3as_math_tech", "bac_math_tech" -> listOf("Arabic", "Math", "French", "English", "Physics", "Technology", "Hist. Geo", "Islamic", "Philosophy", "Art", "Tamazight", "Sport")
        "3as_math", "bac_math", "3as_sci", "bac_sci" -> listOf("Arabic", "Math", "French", "English", "Physics", "Sciences", "Hist. Geo", "Islamic", "Philosophy", "Art", "Tamazight", "Sport")
        "3as_lang", "bac_lang" -> listOf("Arabic", "Math", "French", "English", "3rd language", "Hist. Geo", "Islamic", "Philosophy", "Art", "Tamazight", "Sport")
        "3as_philo", "bac_philo" -> listOf("Arabic", "Math", "French", "English", "Hist. Geo", "Islamic", "Philosophy", "Art", "Tamazight", "Sport")
        else -> listOf("")
    }

    var data = HashMap<String, HashMap<String, Double>>()
    var factors: Double = 0.0
    init {
        for (item in subjects) {
            val subItem = HashMap<String, Double>()
            subItem["Mark"] = 0.0
            subItem["Factor"] = 1.0
            if (!level.startsWith("bac")) {
                subItem["First Test"] = 0.0
                subItem["Second Test"] = 0.0
                subItem["Exam"] = 0.0
                subItem["CC"] = 0.0
            }
            data[item] = subItem
        }
        when (level) {
            "1am", "2am", "3am", "4am" -> {
                data["Arabic"]!!["Factor"] = 4.0
                data["Math"]!!["Factor"] = 3.0
                data["Physics"]!!["Factor"] = 2.0
                data["French"]!!["Factor"] = 2.0
                data["English"]!!["Factor"] = 2.0
                data["Hist. Geo"]!!["Factor"] = 2.0
                data["Islamic"]!!["Factor"] = 2.0
                data["Sciences"]!!["Factor"] = 3.0
            }
            "1as_sci" -> {
                data["Arabic"]!!["Factor"] = 3.0
                data["Math"]!!["Factor"] = 5.0
                data["Physics"]!!["Factor"] = 4.0
                data["French"]!!["Factor"] = 2.0
                data["English"]!!["Factor"] = 2.0
                data["Hist. Geo"]!!["Factor"] = 2.0
                data["Islamic"]!!["Factor"] = 2.0
                data["Sciences"]!!["Factor"] = 4.0
                data["Technology"]!!["Factor"] = 2.0
                data["Informatic"]!!["Factor"] = 2.0
            }
            "1as_lit" -> {
                data["Arabic"]!!["Factor"] = 5.0
                data["Math"]!!["Factor"] = 2.0
                data["Physics"]!!["Factor"] = 4.0
                data["French"]!!["Factor"] = 3.0
                data["English"]!!["Factor"] = 3.0
                data["Hist. Geo"]!!["Factor"] = 3.0
                data["Islamic"]!!["Factor"] = 2.0
                data["Sciences"]!!["Factor"] = 2.0
                data["Informatic"]!!["Factor"] = 2.0
            }
            "2as_sci" -> {
                data["Arabic"]!!["Factor"] = 2.0
                data["Math"]!!["Factor"] = 5.0
                data["Physics"]!!["Factor"] = 4.0
                data["French"]!!["Factor"] = 2.0
                data["English"]!!["Factor"] = 2.0
                data["Hist. Geo"]!!["Factor"] = 2.0
                data["Islamic"]!!["Factor"] = 2.0
                data["Sciences"]!!["Factor"] = 5.0
                data["Philosophy"]!!["Factor"] = 2.0
            }
            "3as_sci", "bac_sci" -> {
                data["Arabic"]!!["Factor"] = 3.0
                data["Math"]!!["Factor"] = 5.0
                data["Physics"]!!["Factor"] = 5.0
                data["French"]!!["Factor"] = 2.0
                data["English"]!!["Factor"] = 2.0
                data["Hist. Geo"]!!["Factor"] = 2.0
                data["Islamic"]!!["Factor"] = 2.0
                data["Sciences"]!!["Factor"] = 6.0
                data["Philosophy"]!!["Factor"] = 2.0
            }
            "2as_math" -> {
                data["Arabic"]!!["Factor"] = 2.0
                data["Math"]!!["Factor"] = 6.0
                data["Physics"]!!["Factor"] = 5.0
                data["French"]!!["Factor"] = 2.0
                data["English"]!!["Factor"] = 2.0
                data["Hist. Geo"]!!["Factor"] = 2.0
                data["Islamic"]!!["Factor"] = 2.0
                data["Sciences"]!!["Factor"] = 2.0
            }
            "3as_math", "bac_math" -> {
                data["Arabic"]!!["Factor"] = 3.0
                data["Math"]!!["Factor"] = 7.0
                data["Physics"]!!["Factor"] = 6.0
                data["French"]!!["Factor"] = 2.0
                data["English"]!!["Factor"] = 2.0
                data["Hist. Geo"]!!["Factor"] = 2.0
                data["Islamic"]!!["Factor"] = 2.0
                data["Sciences"]!!["Factor"] = 2.0
                data["Philosophy"]!!["Factor"] = 2.0
            }
            "2as_math_tech" -> {
                data["Arabic"]!!["Factor"] = 2.0
                data["Math"]!!["Factor"] = 6.0
                data["Physics"]!!["Factor"] = 5.0
                data["French"]!!["Factor"] = 2.0
                data["English"]!!["Factor"] = 2.0
                data["Hist. Geo"]!!["Factor"] = 2.0
                data["Islamic"]!!["Factor"] = 2.0
                data["Technology"]!!["Factor"] = 6.0
            }
            "3as_math_tech", "bac_math_tech" -> {
                data["Arabic"]!!["Factor"] = 3.0
                data["Math"]!!["Factor"] = 6.0
                data["Physics"]!!["Factor"] = 6.0
                data["French"]!!["Factor"] = 2.0
                data["English"]!!["Factor"] = 2.0
                data["Hist. Geo"]!!["Factor"] = 2.0
                data["Islamic"]!!["Factor"] = 2.0
                data["Technology"]!!["Factor"] = 6.0
                data["Philosophy"]!!["Factor"] = 2.0
            }
            "2as_mng" -> {
                data["Arabic"]!!["Factor"] = 2.0
                data["Math"]!!["Factor"] = 6.0
                data["Physics"]!!["Factor"] = 5.0
                data["French"]!!["Factor"] = 2.0
                data["English"]!!["Factor"] = 2.0
                data["Hist. Geo"]!!["Factor"] = 2.0
                data["Islamic"]!!["Factor"] = 2.0
                data["Sciences"]!!["Factor"] = 2.0
                data["Economy"]!!["Factor"] = 4.0
                data["Law"]!!["Factor"] = 2.0
                data["Management"]!!["Factor"] = 5.0
                data["Philosophy"]!!["Factor"] = 2.0
            }
            "3as_mng", "bac_mng" -> {
                data["Arabic"]!!["Factor"] = 3.0
                data["Math"]!!["Factor"] = 7.0
                data["Physics"]!!["Factor"] = 6.0
                data["French"]!!["Factor"] = 2.0
                data["English"]!!["Factor"] = 2.0
                data["Hist. Geo"]!!["Factor"] = 2.0
                data["Islamic"]!!["Factor"] = 2.0
                data["Sciences"]!!["Factor"] = 2.0
                data["Economy"]!!["Factor"] = 5.0
                data["Law"]!!["Factor"] = 2.0
                data["Management"]!!["Factor"] = 6.0
                data["Philosophy"]!!["Factor"] = 2.0
            }
            "2as_lang" -> {
                data["Arabic"]!!["Factor"] = 4.0
                data["Math"]!!["Factor"] = 2.0
                data["French"]!!["Factor"] = 4.0
                data["English"]!!["Factor"] = 4.0
                data["3rd language"]!!["Factor"] = 4.0
                data["Hist. Geo"]!!["Factor"] = 4.0
                data["Islamic"]!!["Factor"] = 2.0
            }
            "3as_lang", "bac_lang" -> {
                data["Arabic"]!!["Factor"] = 5.0
                data["Math"]!!["Factor"] = 2.0
                data["French"]!!["Factor"] = 5.0
                data["English"]!!["Factor"] = 5.0
                data["3rd language"]!!["Factor"] = 4.0
                data["Hist. Geo"]!!["Factor"] = 2.0
                data["Islamic"]!!["Factor"] = 2.0
                data["Philosophy"]!!["Factor"] = 2.0
            }
            "2as_philo" -> {
                data["Arabic"]!!["Factor"] = 5.0
                data["Math"]!!["Factor"] = 2.0
                data["Physics"]!!["Factor"] = 2.0
                data["French"]!!["Factor"] = 3.0
                data["English"]!!["Factor"] = 3.0
                data["Hist. Geo"]!!["Factor"] = 4.0
                data["Islamic"]!!["Factor"] = 2.0
                data["Sciences"]!!["Factor"] = 2.0
                data["Philosophy"]!!["Factor"] = 5.0
            }
            "3as_philo", "bac_philo" -> {
                data["Arabic"]!!["Factor"] = 6.0
                data["Math"]!!["Factor"] = 2.0
                data["French"]!!["Factor"] = 3.0
                data["English"]!!["Factor"] = 3.0
                data["Hist. Geo"]!!["Factor"] = 4.0
                data["Islamic"]!!["Factor"] = 2.0
                data["Philosophy"]!!["Factor"] = 6.0
            }
        }
        for (item in subjects)  {
            factors += data[item]!!["Factor"]!!.toDouble()
        }
    }
    var total: Double = 0.0
    var result: Double = 0.0

    fun updateData(newData: HashMap<String, HashMap<String, Double>>) {
        this.data.clear()
        this.data.putAll(newData)
    }

    fun calculate(): Double {
        result = when (level) {
            "1ap", "2ap", "3ap", "4ap", "5ap" -> {
                data["Sport"]!!["Factor"] = if (sport) 1.0 else 0.0
                for (item in subjects) {
                    data[item]!!["Mark"] = data[item]!!["Exam"]!!
                }
                total = 0.0
                for (item in subjects) {
                    total += data[item]!!["Mark"]!! * data[item]!!["Factor"]!!
                }
                factors = subjects.count().toDouble()
                total / factors
            }
            "1am", "2am", "3am", "4am", "bem" -> {
                data["Sport"]!!["Factor"] = if (sport) 1.0 else 0.0
                for (item in subjects) {
                    if(level != "bem") data[item]!!["Mark"] = ((data[item]!!["First Test"]!! + data[item]!!["Second Test"]!!) / 2 + data[item]!!["CC"]!! + data[item]!!["Exam"]!! * 3) / 5
                }
                total = 0.0
                for (item in subjects) {
                    total += data[item]!!["Mark"]!! * data[item]!!["Factor"]!!
                }
                factors = 0.0
                for (item in subjects) {
                    factors += data[item]!!["Factor"]!!
                }
                total / factors
            }
            "" -> 0.0
            "bac_math", "bac_sci", "bac_math_tech", "bac_mng", "bac_lang", "bac_philo",
            "3as_math", "3as_sci", "3as_math_tech", "3as_mng", "3as_lang", "3as_philo",
            "2as_math", "2as_sci", "2as_math_tech", "2as_mng", "2as_lang", "2as_philo",
            "1as_sci", "1as_lit" -> {
                data["Sport"]!!["Factor"] = if (sport) 1.0 else 0.0
                data["Art"]!!["Factor"] = if (art) 1.0 else 0.0
                data["Tamazight"]!!["Factor"] = if (tamazight) 1.0 else 0.0
                for (item in subjects) {
                    if(!level.startsWith("bac"))
                        data[item]!!["Mark"] = ((data[item]!!["First Test"]!! + data[item]!!["Second Test"]!!) / 2 + data[item]!!["CC"]!! + data[item]!!["Exam"]!! * 2) / 4
                }
                total = 0.0
                for (item in subjects) {
                    total += data[item]!!["Mark"]!! * data[item]!!["Factor"]!!
                }
                factors = 0.0
                for (item in subjects) {
                    factors += data[item]!!["Factor"]!!
                }
                total / factors
            }
            else -> 0.0
        }
        return result
    }

    fun generateQrCode(foregroundColor: Int = Color.BLACK, backgroundColor: Int = Color.TRANSPARENT, size: Int = 500): ImageBitmap {
        val bitMatrix = QRCodeWriter().encode(Gson().toJson(CalculationData(name, level, data, sport, art, tamazight)), BarcodeFormat.QR_CODE, size, size)
        val w = bitMatrix.width
        val h = bitMatrix.height
        val pixels = IntArray(w * h)
        for (y in 0 until h) {
            for (x in 0 until w) {
                pixels[y * w + x] = if (bitMatrix[x, y]) foregroundColor else backgroundColor
            }
        }
        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h)
        return bitmap.asImageBitmap()
    }
}