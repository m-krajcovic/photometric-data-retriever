@file:JvmName("Main")

package cz.muni.physics.pdr.keplerebc

import cz.muni.physics.pdr.java.PluginUtils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import java.util.regex.Pattern

// ... 🧐
fun main(args: Array<String>) {
//    var args = arrayOf("011013201")
    if (args.isEmpty()) return
    val kicId = args[0].dropWhile { it == '0' }
    val url = URL("http://keplerebs.villanova.edu/data/?k=$kicId.00&cadence=sc&data=data")
    BufferedReader(InputStreamReader(PluginUtils.copyUrlOpenStream(url, kicId, 5))).use {
        it.forEachLine { line: String ->
            if (!line.trim().startsWith("#")) {
                val tsv = line.split(Pattern.compile("\\s+"))
                var time = tsv[0].toDouble()
                time += (if (time <= 57139) 66.184 else 67.184) + 2454833
                val flux = tsv[6]
                val err = tsv[7]
                println("$time,$flux,$err")
            }
        }
    }
}

