import akka.actor._

// メッセージ
case class Work(str: String, time: Long)
case class Works(list: List[Work])
case class Finish()

// 親アクター
class ParentActor extends Actor {
  def receive = {
    case Works(works) => assign(works)
    case Finish()     => finish()
    case _            =>
  }

  private[this] def assign(works: List[Work]) = {
    works.zipWithIndex.foreach { case (work, i) =>
      // 子アクターを生成して仕事を投げる
      val child = context.actorOf(Props(classOf[ChildActor], i))
      child ! work
    }
  }

  private[this] def finish() = {
    // 子アクターの数がゼロなら、終了する
    val rest = context.children.size
    if (rest == 0) {
      println("All work finished.")
      self ! PoisonPill
    } else {
      println(s"Remains ${rest} task(s) yet.")
    }
  }
}

// 子アクター
class ChildActor(number: Int) extends Actor {
  def receive = {
    case Work(str, wait) => work(str, wait)
    case _               =>
  }

  // 仕事実行
  private[this] def work(str: String, wait: Long) = {
    Thread.sleep(wait)
    println(s"Actor No.${number} work finished. Message: ${str}")
    // 仕事が終わったら親に報告し、自害する
    context.parent ! Finish()
    self ! PoisonPill
  }
}
