package org.moten.david.lifty.snippet

import _root_.net.liftweb.http._
import S._
import _root_.net.liftweb.util._
import Helpers._
import _root_.scala.xml._
import js._
import JsCmds._
import JE._
import SHtml._
import java.util.Date

case class TimeEntry(Date )

object VarTable {
  def render(in: NodeSeq): NodeSeq = {
    val df = new java.text.SimpleDateFormat("EEE d MMM yyyy");
    val toRender = (1 to 10).map(i =>
      (df.format(new Date()), "0830", "1230"))
    toRender.flatMap { item =>
      def doRow(template: NodeSeq): NodeSeq = {
        bind(
          "var", template,
          "date" -> Text(item._1),
          "start" -> Text(item._2),
          "finish" -> Text(item._3))
      }
      bind("var", in, "row" -> doRow _)
    }
  }
}