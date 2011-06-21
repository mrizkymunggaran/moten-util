package org.moten.david.remdis.worker

import org.mortbay.jetty.Server
import org.mortbay.jetty.servlet._
import java.io._
import java.util.UUID
import javax.servlet.http._

import java.io._

class Manager(val reportToUrl: String) {
  var keepGoing = true

  def run {
    val server = new Server(6060)
    val root = new Context(server, "/", Context.SESSIONS)
    //    root.addServlet(new ServletHolder(new StatusServlet(this)), "/*");
    server.start();
    while (keepGoing) {
      Thread.sleep(5000)
    }
    server.stop();
    server.join();
  }
}
import scala.actors._

case class Task(payload: InputStream, taskId: String, reportToUrl: String)
case class TaskException(taskId: String, message: String)
case class TaskFinished(taskId: String, result: InputStream)
case class TaskRequested(actor: Actor, jobId: String)

object Tasks {
  val tasks = new Tasks(List())
}
class Tasks(list: List[Task]) extends Actor {
  def act {
    react {
      case TaskException(taskId, message) => println(message)
      case TaskFinished(taskId, result) => println("task finished: " + taskId)
      case TaskRequested(actor, jobId) => {
        val taskId = jobId + "." + UUID.randomUUID.toString.substring(0, 8) + ""
        actor ! Task(null, taskId, "http://localhost:8080")
      }
    }
  }
}

class TaskRequesterServlet extends HttpServlet {
  override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    val job = request.getParameter("jobId")

  }
}

class Worker(val homeUrl: String) {

  var keepGoing = true

  def run {
    val server = new Server(8080)
    val root = new Context(server, "/", Context.SESSIONS)
    root.addServlet(new ServletHolder(new StatusServlet(this)), "/*")
    server.start();
    while (keepGoing) {
      val task = next()
      perform(task)
      Thread.sleep(5000)
    }
    server.stop();
    server.join();
  }

  def next(): Task =
    new Task(new ByteArrayInputStream("hello".getBytes), "abc123", homeUrl + "/report")

  def perform(task: Task) {
    println("performing " + task.taskId)
  }
}

class StatusServlet(server: Worker) extends HttpServlet {
  override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
    response.getWriter.println("No current tasks from " + server.homeUrl)
  }
}

object Main {
  def main(args: Array[String]) {
    new Worker(if (args.length > 0) args(0) else "http://localhost:8080").run
  }
}

