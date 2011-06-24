package org.moten.david.remdis.worker

import java.io._
import scala.actors.Actor._
import scala.actors.remote.RemoteActor._
import scala.actors.remote._
import scala.actors._
import scala.io._
import java.net._

case class TaskId(jobId: String, taskId: String)
case class TaskIdRequested
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
case object TaskIdRequested

class Coordinator(port: Int) extends Actor {
  def act() {
    println("starting coordinator on port " + port)
    alive(port)
    println("registering 'coordinator")
    register('coordinator, self)
    println("waiting for messages")
    loop {
      react {
        case TaskIdRequested => { println("TaskIdRequested"); sender ! TaskId("job1", "task1") }
        case TaskException(taskId, message) => println("task exception: " + message)
        case TaskFinished(taskId) => { println("TaskFinished"); sender ! TaskResultRequested(taskId) }
        case TaskResult => println("task result returned")
        case TaskRequested(taskId: TaskId) => {
          println("replying with new task")
          sender ! Task(taskId, "payload".getBytes, JavaOptions("-DXmx512m"))
        }
        case ExecutableRequested(jobId) => {
          println("ExecutableRequested")
          sender ! Executable("job1", None, new Main().getClass().getName(), null)
        }
        case Stop => exit
      }
    }
  }
}

class Main {
  def main(args: Array[String]) {
    println("hello there the main has run with args: " + args.toList)
  }
}

object Coordinator {
  val Port = 9000
  def main(args: Array[String]) {
    RemoteActor.classLoader = getClass.getClassLoader
    val c = new Coordinator(Port)
    c.start
  }
}

object Worker extends App {
  val w = new Worker(9001)
  w.start
  w ! TaskIdRequested
}

class Worker(port: Int) extends Actor {

  val executableDirectory = new File(System.getProperty("java.io.tmpdir"))

  def act {
    var currentTask: Option[Task] = None
    println("starting worker on port " + port)
    println("setting classLoader")
    RemoteActor.classLoader = getClass.getClassLoader
    alive(port)
    println("registering 'worker")
    register('worker, self)
    println("waiting for messages")

    loop {
      reactWithin(5000) {
        case TaskIdRequested => {
          getCoordinator ! TaskIdRequested
        }
        case t: TaskId =>
          getCoordinator ! TaskRequested(t)

        case TaskRequested => {
          println("requesting task")
          getCoordinator ! TaskRequested
        }
        case ExecutableRequested => {
          println("requesting task")
          getCoordinator ! ExecutableRequested
        }
        case ex: Executable => {
          println("executable arrived")
          saveExecutable(ex)
          self ! ExecuteTask
        }
        case t: Task => {
          println("task arrived")
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
          println("TaskResultRequested")
          getCoordinator ! TaskResult(taskId, null)
        }
        case t: TaskException =>
          getCoordinator ! t

        case TIMEOUT => {
          println("resetting proxy")
          resetProxy
          self ! TaskRequested
        }
        case x => println("unexpected return " + x)
      }
    }
  }

  def saveExecutable(ex: Executable) {
    val filename = ex.jobId + ".jar"
    val file = new File(executableDirectory, filename)
    if (!file.exists) {
      println("writing jar to " + file)
      writeJar(file, ex.jar)
    }
  }

  def getCoordinator = {
    val c = select(Node("localhost", Coordinator.Port), 'coordinator)
    link(c)
    c
  }

  private def performTask(t: Task) {
    println("performing task")
    val classLoader = getClassLoader(t.taskId.jobId)
    val mainClass = getMainClass(t.taskId.jobId)
    println("instantiating object of type " + mainClass)
    val c = Class.forName(mainClass, true, classLoader)
    val obj = c.newInstance.asInstanceOf[{ def main(args: Array[String]) }]
    println("running main method of object")
    obj.main(List("some", "args").toArray)
    println("completed run")
    self ! TaskFinished(t.taskId)
    println("notified coordinator of result")
  }

  def getJarFile(jobId: String) =
    new File(executableDirectory, jobId + ".jar")

  def getMainClass(jobId: String) = new Main().getClass.getName

  private def getClassLoader(jobId: String): ClassLoader = {
    val file = getJarFile(jobId)
    if (file.length > 0) {
      println("adding jar to classpath")
      val urls = List(file.toURI.toURL).toArray
      println("creating new class loader")
      new URLClassLoader(urls)
    } else
      ClassLoader.getSystemClassLoader
  }

  private def writeJar(file: File, jar: Option[Array[Byte]]) {
    jar match {
      case Some(bytes) => {
        println("writing jar to temp file")
        val tempFile = File.createTempFile(file.getName, ".tmp")
        val fos = new FileOutputStream(tempFile)
        fos.write(bytes)
        fos.close
        println("renaming temp file to " + file)
        if (file.exists) file.delete
        tempFile.renameTo(file)
      }
      case None =>
        if (!file.createNewFile)
          throw new RuntimeException("could not create " + file)
    }
  }
}