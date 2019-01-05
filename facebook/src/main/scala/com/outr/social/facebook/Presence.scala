package com.outr.social.facebook

import java.net.URLEncoder

import scala.util.matching.Regex

object Presence {
  private val j: Vector[(Char, String)] = Vector(
   '_' -> "%",
   'A' -> "%2",
   'B' -> "000",
   'C' -> "%7d",
   'D' -> "%7b%22",
   'E' -> "%2c%22",
   'F' -> "%22%3a",
   'G' -> "%2c%22ut%22%3a1",
   'H' -> "%2c%22bls%22%3a",
   'I' -> "%2c%22n%22%3a%22%",
   'J' -> "%22%3a%7b%22i%22%3a0%7d",
   'K' -> "%2c%22pt%22%3a0%2c%22vis%22%3a",
   'L' -> "%2c%22ch%22%3a%7b%22h%22%3a%22",
   'M' -> "%7b%22v%22%3a2%2c%22time%22%3a1",
   'N' -> ".channel%22%2c%22sub%22%3a%5b",
   'O' -> "%2c%22sb%22%3a1%2c%22t%22%3a%5b",
   'P' -> "%2c%22ud%22%3a100%2c%22lc%22%3a0",
   'Q' -> "%5d%2c%22f%22%3anull%2c%22uct%22%3a",
   'R' -> ".channel%22%2c%22sub%22%3a%5b1%5d",
   'S' -> "%22%2c%22m%22%3a0%7d%2c%7b%22i%22%3a",
   'T' -> "%2c%22blc%22%3a1%2c%22snd%22%3a1%2c%22ct%22%3a",
   'U' -> "%2c%22blc%22%3a0%2c%22snd%22%3a1%2c%22ct%22%3a",
   'V' -> "%2c%22blc%22%3a0%2c%22snd%22%3a0%2c%22ct%22%3a",
   'W' -> "%2c%22s%22%3a0%2c%22blo%22%3a0%7d%2c%22bl%22%3a%7b%22ac%22%3a",
   'X' -> "%2c%22ri%22%3a0%7d%2c%22state%22%3a%7b%22p%22%3a0%2c%22ut%22%3a1",
   'Y' -> "%2c%22pt%22%3a0%2c%22vis%22%3a1%2c%22bls%22%3a0%2c%22blc%22%3a0%2c%22snd%22%3a1%2c%22ct%22%3a",
   'Z' -> "%2c%22sb%22%3a1%2c%22t%22%3a%5b%5d%2c%22f%22%3anull%2c%22uct%22%3a0%2c%22s%22%3a0%2c%22blo%22%3a0%7d%2c%22bl%22%3a%7b%22ac%22%3a"
  )
  private val i: Map[String, Char] = j.map {
    case (k, v) => v -> k
  }.toMap
  private val l: Vector[String] = j.map(_._2).reverse
  private val h: Regex = l.mkString("|").r

  def presenceEncode(str: String): String = {
    val stage1 = "([_A-Z])|%..".r.replaceAllIn(URLEncoder.encode(str, "UTF-8"), (m: Regex.Match) => {
      Option(m.group(1)).map(n => s"%${Integer.toHexString(n.charAt(0).toInt)}").getOrElse(m.group(0))
    }).toLowerCase
    h.replaceAllIn(stage1, (m: Regex.Match) => {
      i(m.group(0)).toString
    })
  }
}

/*
function generatePresence(userID) {
  var time = Date.now();
  return (
    "E" +
    presenceEncode(
      JSON.stringify({
        v: 3,
        time: parseInt(time / 1000, 10),
        user: userID,
        state: {
          ut: 0,
          t2: [],
          lm2: null,
          uct2: time,
          tr: null,
          tw: Math.floor(Math.random() * 4294967295) + 1,
          at: time
        },
        ch: {
          ["p_" + userID]: 0
        }
      })
    )
  );
}
 */