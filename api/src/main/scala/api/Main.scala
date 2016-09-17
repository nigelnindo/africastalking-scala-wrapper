package api

import scala.concurrent.ExecutionContext.Implicits.global

import api.sms.{SMS, SingleSMS}

/**
 * Created by nigelnindo on 9/17/16.
 */

object Main {
  def main(args: Array[String]): Unit = {
    import scala.util.{Failure, Success}
    println("running")
    SMS.send(SingleSMS("+254706250610", "Hi nigel :)")).onComplete{
      case Success(gatewayResponse) => println(gatewayResponse)
      case Failure(err) => println(err)
    }
    Thread.sleep(10000)
  }
}

