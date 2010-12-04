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
    def df = new java.text.SimpleDateFormat("EEE d MMM yyyy")
    object date extends RequestVar(new Date())
    object start extends RequestVar("08301230")
    object finish extends RequestVar("")
    object item extends SessionVar("")
    
    def nextDay(d:Date): Date =
      new Date(d.getTime() + 24 * 60 * 60 * 1000L)

    def add(xhtml: NodeSeq): NodeSeq = {
      def processEntryAdd() {
          error("boo")
      }
      bind("entry", xhtml,
        "date" -%> text(df.format(date.get),
          x => date(nextDay(df.parse(x)))),
        "start" -%> text(start,start(_)),
        "finish" -%> text(finish, finish(_)),
        "submit" -%> submit("submit",processEntryAdd))
    }

    def list(in: NodeSeq): NodeSeq = {
      val df = new java.text.SimpleDateFormat("EEE d MMM yyyy");
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
