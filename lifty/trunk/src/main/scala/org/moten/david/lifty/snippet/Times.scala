package org.moten.david.lifty;
package snippet {

  import _root_.net.liftweb.http._
  import S._
  import _root_.net.liftweb.util._
  import Helpers._
  import _root_.scala.xml._
  import js._
  import JsCmds._
  import JE._
  import SHtml._
  import _root_.java.util._

  case class TimeEntry(date: Date, start: String, finish: String)

  class Times {
    //make df a def not a val because not thread safe
    def df = new java.text.SimpleDateFormat("EEE MMM dd yyyy")
    object date extends RequestVar(new Date())
    object times extends RequestVar("08301230")

    def nextDay(d: Date, times: String): Date = {
      val finish = times.substring(4, 8)
      println("before=" + d + " finish=" + finish.toInt)
      if (toMinutes(finish) >= 15 * 60) {
        println("incrementing date")
        val cal = new GregorianCalendar()
        cal.setTime(d);
        val factor =
          if (cal.get(Calendar.DAY_OF_WEEK) == 6) 3
          else 1
        return new Date(d.getTime() + factor * 24 * 60 * 60 * 1000L)
      } else return d;
    }

    def toMinutes(s: String): Int = {
      s.substring(0, 2).toInt * 60 + s.substring(2, 4).toInt
    }

    def toString(n: Int): String = {
      val minutes = n % 60
      val hours = n / 60
      val s1 = (if (hours < 10) "0" else "") + hours
      val s2 = (if (minutes < 10) "0" else "") + minutes
      return s1 + s2
    }

    def nextTimes(times: String): String = {
      val start = toMinutes(times.substring(0, 4))
      val finish = toMinutes(times.substring(4, 8))
      Log.info("finishMinutes=" + finish)
      if (finish >= 15 * 60)
        return "08301230"
      else return toString(finish + 30) + "1730"
    }

    def add(xhtml: NodeSeq): NodeSeq = {
      def processEntryAdd() {
        //        error("boo")
        println("hello")
        println("date=" + date.get)
        println("times=" + times.get)
        date(nextDay(date.get, times.get))
        times(nextTimes(times.get))
      }
      bind("entry", xhtml,
        "date" -%> text(df.format(date.get),
          x => date(df.parse(x))),
        "times" -%> text(times, x => times(x)),
        "submit" -%> submit("submit", processEntryAdd))
    }

    def list(in: NodeSeq): NodeSeq = {
      val df = new java.text.SimpleDateFormat("EEE MMM dd yyyy");
      val toRender = (1 to 10).map(
        i => TimeEntry(new Date(), "0839", "1230"))
      toRender.flatMap { item =>
        def doRow(template: NodeSeq): NodeSeq = {
          bind(
            "entry", template,
            "date" -> Text(df.format(item.date)),
            "start" -> Text(item.start),
            "finish" -> Text(item.finish))
        }
        bind("entry", in, "row" -> doRow _)
      }
    }

  }
  object Times {
    var number: Double = 0
  }
}
