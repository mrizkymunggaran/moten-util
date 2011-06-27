package org.moten.david.remdis

import java.io._
import scala.actors.Actor._
import scala.actors.remote.RemoteActor._
import scala.actors.remote._
import scala.actors._
import scala.io._
import java.net._
import Logger._

case class TaskId(jobId: String, taskId: String)
case object TaskIdRequested
case class TaskRequested(taskId: TaskId)
case class Task(taskId: TaskId, content: Array[Byte], options: Options)
case class ExecutableRequested(jobId: String)
case class Executable(jobId: String, jar: Option[Array[Byte]], mainClass: String, options: Options)
case class TaskFinished(taskId: TaskId)
case class TaskResultRequested(taskId: TaskId)
case class TaskResult(taskId: TaskId, result: Array[Byte])
case class TaskException(taskId: TaskId, message: String)

trait Options
case class JavaOptions(options: String) extends Options

case object TaskRequested
case object Stop
case object Test
case object ExecuteTask

class Coordinator(port: Int) extends Actor {
  def act() {
    info("starting coordinator on port " + port)
    alive(port)
    info("registering 'coordinator")
    register('coordinator, self)
    info("waiting for messages")
    loop {
      react {
        case TaskIdRequested => { info("TaskIdRequested"); sender ! TaskId("job1", "task1") }
        case TaskException(taskId, message) => info("task exception: " + message)
        case TaskFinished(taskId) => {
          info("TaskFinished"); info("sending TaskResultRequested")
          sender ! TaskResultRequested(taskId)
        }
        case TaskResult => info("task result returned")
        case TaskRequested(taskId: TaskId) => {
          info("replying with new task")
          sender ! Task(taskId, "payload".getBytes, JavaOptions("-DXmx512m"))
        }
        case ExecutableRequested(jobId) => {
          info("ExecutableRequested")
          sender ! Executable("job1", None, new Main().getClass().getName(), null)
        }
        case Stop => exit
      }
    }
  }
}

class Main {
  def main(args: Array[String]) {
    info("hello there the main has run with args: " + args.toList)
  }
}

object Coordinator extends App {
  val Port = 62831
  val port = if (args.length > 0) args(0).toInt else Port
  RemoteActor.classLoader = getClass.getClassLoader
  val c = new Coordinator(port)
  c.start
}

object Worker extends App {
  import scala.math._
  val randomPort = (System.currentTimeMillis / 1000 + 9000).toInt
  val port = if (args.length > 0) args(0).toInt else randomPort
  val coordinatorHost = if (args.length > 1) args(1) else "localhost"
  val coordinatorPort = if (args.length > 2) args(2).toInt else Coordinator.Port
  val coordinatorNode = Node(coordinatorHost, coordinatorPort)
  val w = new Worker(port, coordinatorNode);
  w.start
  w ! TaskIdRequested
}

class Worker(port: Int, coordinatorNode: Node) extends Actor {

  val executableDirectory = new File(System.getProperty("java.io.tmpdir"))

  def act {
    var currentTask: Option[Task] = None
    info("starting worker on port " + port)
    info("setting classLoader")
    RemoteActor.classLoader = getClass.getClassLoader
    alive(port)
    info("registering 'worker")
    register('worker, self)
    info("waiting for messages")

    loop {
      reactWithin(5000) {
        case TaskIdRequested => {
          getCoordinator ! TaskIdRequested
        }
        case t: TaskId =>
          getCoordinator ! TaskRequested(t)

        case TaskRequested => {
          info("requesting task")
          getCoordinator ! TaskRequested
        }
        case ExecutableRequested => {
          info("requesting task")
          getCoordinator ! ExecutableRequested
        }
        case ex: Executable => {
          info("executable arrived")
          saveExecutable(ex)
          self ! ExecuteTask
        }
        case t: Task => {
          info("task arrived")
          performTask(t)
          Thread.sleep(3000)
          self ! TaskIdRequested
        }
        case ExecuteTask => currentTask match {
          case Some(t: Task) => performTask(t)
          case None => {}
        }
        case t: TaskFinished => getCoordinator ! t

        case TaskResultRequested(taskId: TaskId) => {
          info("sending result")
          getCoordinator ! TaskResult(taskId, null)
        }
        case t: TaskException =>
          getCoordinator ! t

        case TIMEOUT => {
          info("resetting proxy")
          resetProxy
          self ! TaskRequested
        }
        case x => info("unexpected return " + x)
      }
    }
  }

  def saveExecutable(ex: Executable) {
    val filename = ex.jobId + ".jar"
    val file = new File(executableDirectory, filename)
    if (!file.exists) {
      info("writing jar to " + file)
      writeJar(file, ex.jar)
    }
  }

  def getCoordinator = {
    val c = select(Node("localhost", Coordinator.Port), 'coordinator)
    link(c)
    c
  }

  private def performTask(t: Task) {
    info("performing task")
    val classLoader = getClassLoader(t.taskId.jobId)
    val mainClass = getMainClass(t.taskId.jobId)
    info("instantiating object of type " + mainClass)
    val c = Class.forName(mainClass, true, classLoader)
    val obj = c.newInstance.asInstanceOf[{ def main(args: Array[String]) }]
    info("running main method of object")
    obj.main(List("some", "args").toArray)
    info("completed run")
    self ! TaskFinished(t.taskId)
    info("notified coordinator of result")
  }

  def getJarFile(jobId: String) =
    new File(executableDirectory, jobId + ".jar")

  def getMainClass(jobId: String) = new Main().getClass.getName

  private def getClassLoader(jobId: String): ClassLoader = {
    val file = getJarFile(jobId)
    if (file.length > 0) {
      info("adding jar to classpath")
      val urls = List(file.toURI.toURL).toArray
      info("creating new class loader")
      new URLClassLoader(urls)
    } else
      ClassLoader.getSystemClassLoader
  }

  private def writeJar(file: File, jar: Option[Array[Byte]]) {
    jar match {
      case Some(bytes) => {
        info("writing jar to temp file")
        val tempFile = File.createTempFile(file.getName, ".tmp")
        val fos = new FileOutputStream(tempFile)
        fos.write(bytes)
        fos.close
        info("renaming temp file to " + file)
        if (file.exists) file.delete
        tempFile.renameTo(file)
      }
      case None =>
        if (!file.createNewFile)
          throw new RuntimeException("could not create " + file)
    }
  }
}

object Demo {

}
/**
 * Logs to System.out with a timestamp.
 */
object Logger {
  import java.util.Date
  import java.text.SimpleDateFormat
  val df = new SimpleDateFormat("HH:mm:ss.SSS")
  var infoEnabled = true
  var debugEnabled = false
  def info(msg: => AnyRef) = if (infoEnabled)
    println(df.format(new Date()) + " INFO " + msg)
  def debug(msg: => AnyRef) = if (debugEnabled)
    println(df.format(new Date()) + " DEBUG " + msg)
}