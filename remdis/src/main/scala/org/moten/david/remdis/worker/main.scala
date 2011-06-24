package org.moten.david.remdis.worker

import java.io._
import scala.actors.Actor._
import scala.actors.remote.RemoteActor._
import scala.actors.remote._
import scala.actors._
import scala.io._
import java.net._

case class TaskId(jobId: String, taskId: String)
trait Options
case class JavaOptions(options: String) extends Options
case class Task(taskId: TaskId, content: Array[Byte], options: Options)
case class TaskException(taskId: TaskId, message: String)
case class TaskFinished(taskId: TaskId, result: Array[Byte])
case object TaskRequested
case class ExecutableRequested(jobId: String)
case class Executable(jar: Option[Array[Byte]], mainClass: String, options: Options)
case object Stop
case object Test

class Coordinator(port: Int) extends Actor {
  def act() {
    println("starting coordinator on port " + port)
    alive(port)
    println("registering 'coordinator")
    register('coordinator, self)
    println("waiting for messages")
    loop {
      react {
        case TaskException(taskId, message) => println("task exception: " + message)
        case TaskFinished(taskId, result) => println("task finished: " + taskId)
        case TaskRequested => {
          println("replying with new task")
          sender ! Task(TaskId("job1", "task1"), "payload".getBytes, JavaOptions("-DXmx512m"))
        }
        case ExecutableRequested(jobId) => reply(Executable(None, new Main().getClass().getName(), null))
        case Stop => { println("exiting"); exit }
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
  w ! TaskRequested
}

class Worker(port: Int) extends Actor {

  def act {
    println("starting worker on port " + port)
    println("setting classLoader")
    RemoteActor.classLoader = getClass.getClassLoader
    alive(port)
    println("registering 'worker")
    register('worker, self)
    println("waiting for messages")

    loop {
      reactWithin(5000) {
        case t: Task => {
          performTask(getCoordinator, t)
          Thread.sleep(3000)
          self ! TaskRequested
        }
        case TaskRequested =>{
    println("requesting task")
    getCoordinator ! TaskRequested
      }
        case ExecutableRequested => {
          println("requesting task")
          getCoordinator ! ExecutableRequested
        }
        case Executable
        case TIMEOUT => {
          println("resetting proxy")
          resetProxy
          self ! TaskRequested
        }
        case x => println("unexpected return " + x)
      }
    }
  }

  def requestTask {

  }

  def getCoordinator = {
    val c = select(Node("localhost", Coordinator.Port), 'coordinator)
    link(c)
    c
  }

  private def performTask(coordinator: AbstractActor, t: Task) {
    println("task = " + t)
    println("task content=" + new String(t.content))
    self ! ExecutableRequested(t.taskId.jobId)
    
      case ex: Executable => {
        performTask(coordinator, t, ex)
      }
      case _ => println("unexpected return")
    }
  }

  private def performTask(coordinator: AbstractActor, t: Task, ex: Executable) {
    println("executable = " + ex)
    val classLoader = getClassLoader(t, ex)
    println("instantiating object of type " + ex.mainClass)
    val c = Class.forName(ex.mainClass, true, classLoader)
    val obj = c.newInstance.asInstanceOf[{ def main(args: Array[String]) }]
    println("running main method of object")
    obj.main(List("some", "args").toArray)
    println("completed run")
    coordinator ! TaskFinished(t.taskId, "boo".getBytes)
    println("notified coordinator of result")
  }

  private def getClassLoader(t: Task, ex: Executable): ClassLoader = {
    ex.jar match {
      case Some(jar) => {
        val directory = new File(System.getProperty("java.io.tmpdir"))
        val filename = t.taskId.jobId + ".jar"
        val file = new File(directory, filename)
        if (!file.exists) {
          println("writing jar to " + file)
          writeJar(file, jar)
        }
        println("adding jar to classpath")
        val urls = List(file.toURI.toURL).toArray
        println("creating new class loader")
        new URLClassLoader(urls)
      }
      case None => ClassLoader.getSystemClassLoader
    }
  }

  private def writeJar(file: File, jar: Array[Byte]) {
    println("writing jar to temp file")
    val tempFile = File.createTempFile(file.getName, ".tmp")
    val fos = new FileOutputStream(tempFile)
    fos.write(jar)
    fos.close
    println("renaming temp file to " + file)
    if (file.exists) file.delete
    tempFile.renameTo(file)
  }
}

