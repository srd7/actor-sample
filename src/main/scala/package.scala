import akka.actor._

object ActorSample {
  private[this] val system = ActorSystem()
  private[this] val parentActor = system.actorOf(Props[ParentActor])

  def main(args: Array[String]) {
    val workList = args.map( str =>
      Work(str, str.length.toLong * 1000L)
    ).toList

    parentActor ! Works(workList)
  }
}
