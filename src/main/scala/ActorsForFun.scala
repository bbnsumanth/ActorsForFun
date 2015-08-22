import akka.actor._

/**
 * Created by KrazyKnight on 8/22/2015.
 */
//actorRef is the address class for actors
class ActorsForFun extends Actor {
  //address of this actor
  //each actor knows its address called self
  //this is an implicit variable,so no need to mention this explicitly

  implicit val self: ActorRef
  var count = 0
  // ! tell is a method in actorRef class
  // it is applied on actorRef and parameters are mess:Any and sender:ActorRef
  //sender parameter is implicit ,so we can skip that if we are using tell as !
  //if we want to give the sender as parameter use tell method instead of !

  //def tell(msg:Any,sender :actorRef) =this.!(msg)(sender)
  //def !(msg:any)(implicit sender:ActorRef = Actor.noSender)
  ///case ("get", customer: ActorRef) => customer ! count
  //since sender of the mess is available aa sender implicitly,we can use more compact  code\
  override def receive() = {
    case "incr" => count += 1
    case "get" => sender ! count
    case _ => count = 0

  }

}

/**
 * besides sending and receiving messages actors can do other things like
 * create another actor and send mess to it
 * it can change its behaviour
 * To do all this we need to know ActorContext
 * Actors only have behaviour described by its receive method
 * the execution is carried out by actorContext
 * ActorContext is available to actor implicitly
 *
 * trait ActorContext contains the following methods
 * def become(behaviour:Receive , discardOld:Boolean = true):unit
 * def unBecome():Unit
 * def actorOf(p:Props ,name:String):ActorRef
 * def stop(a;ActorRef):Unit
 *
 *
 */
class ActorsForFun1 extends Actor {
  implicit val self: ActorRef
  implicit val context: ActorContext
  //implicit val sender:ActorRef

  override def receive = counter(0)

  def counter(n: Int) = {
    case "incr" => context.become(counter(n + 1))
    case "get" => sender ! n

  }

}

class Main extends Actor {
  implicit val self: ActorRef
  implicit val context: ActorContext
  val counter = context.actorOf(Props[ActorsForFun], "counterActor")
  counter ! "incr"
  counter ! "incr"
  counter ! "incr"
  counter ! "get"

  def receive = {
    case count: Int => {
      println("received count :" + count)
      context.stop(self)
    }
  }

  /**
   * upon receiving a message an actor can do folowing things
   * send messages to sender(see ActorsForFun)
   * create actors and send messages to them(see Main)
   * designate the behaviour for the next message(ActorsForFun1)
   *
   */

}

