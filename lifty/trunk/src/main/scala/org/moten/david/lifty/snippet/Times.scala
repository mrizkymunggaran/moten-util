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

  class Times {
    val df = new java.text.SimpleDateFormat("EEE d MMM yyyy")
    object date extends RequestVar(new Date())
    object start extends RequestVar("08301230")
    object finish extends RequestVar("")
    object item extends SessionVar("")
    def nextDate(s: String): String =
      df.format(new Date(df.parse(s).getTime() + 24 * 60 * 60 * 1000L))

    def add(xhtml: NodeSeq): NodeSeq = {
      def processEntryAdd() {
      }
      bind("entry", xhtml,
        "date" -%> text(df.format(date.get),
          x => date(df.parse(x))),
        "start" -%> ajaxText(start, { value => SetHtml("list", "gotcha") }),
        "finish" -%> text(finish, finish(_)))
    }

  }
  object Times {
    var number: Double = 0
  }
}
