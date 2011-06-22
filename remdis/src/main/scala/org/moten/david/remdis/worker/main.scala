package org.moten.david.remdis.worker

import java.io._
import java.util.UUID
import scala.actors._
import scala.actors.Actor._
import scala.actors.remote._
import scala.actors.remote.RemoteActor._

case class TaskId(jobId: String, taskId: String)
trait Options
case class JavaOptions(options: String) extends Options
case class Task(taskId: TaskId, content: Array[Byte], options: Options)
case class TaskException(taskId: TaskId, message: String)
case class TaskFinished(taskId: TaskId, result: Array[Byte])
case object TaskRequested
case class ExecutableRequested(jobId: String)
case class Executable(jar: Array[Byte], mainClass: String, options: Options)
case object Stop

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
          reply(Task(TaskId("job1", "task1"), "payload".getBytes, JavaOptions("-DXmx512m")))
        }
        case ExecutableRequested(jobId) => reply(Executable(null, new Main().getClass().getName(), null))
        case Stop => { println("exiting"); exit }
      }
    }
  }
}

class Main {
  def main(args: Array[String]) {
    println("hello there the main has run with args: " + args)
  }
}

object Coordinator {
  val Port = 9000
  def run(args: Array[String]) {
    RemoteActor.classLoader = getClass.getClassLoader
    val c = new Coordinator(Port)
    c.start
  }
}

object Worker extends App {
  println("setting classLoader")
  RemoteActor.classLoader = getClass.getClassLoader
  println("getting remote actor")
  val coordinator = select(Node("localhost", Coordinator.Port), 'coordinator)
  println("sending message to remote actor")
  coordinator !? TaskRequested match {
    case t: Task => {
      println("task = " + t)
      println("task content=" + new String(t.content))
      coordinator !? ExecutableRequested(t.taskId.jobId) match {
        case ex: Executable => {
          println("executable = " + ex)
          if (ex.jar != null) {
            println("adding jar to classpath")
          }
          println("instantiating object of type " + ex.mainClass)
          val c = Class.forName(ex.mainClass)
          val obj = c.newInstance.asInstanceOf[{ def main(args: Array[String]) }]
          println("running main method of object")
          obj.main(List("hello", "there").toArray)
          println("completed run")
          coordinator ! TaskFinished(t.taskId, "boo".getBytes)
          println("notified coordinator of result")
        }
        case _ => println("unexpected return")
      }
    }
    case None => println("failed to get task")
    case _ => println("unexpected return")
  }

}
