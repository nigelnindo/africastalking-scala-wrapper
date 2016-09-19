package api

import scala.concurrent.ExecutionContext.Implicits.global

import api.sms.{BulkSimpleSMS, SimpleSMS, SMSSender}

/**
 * Created by nigelnindo on 9/17/16.
 */

object Main {
  def main(args: Array[String]): Unit = {

    import scala.util.{Failure, Success}

    val API_KEY: String = "98f96d823dacff5c49bc752f1696f178b2d450033ced27e6c45d8cc59bf4da6f"
    val USER_NAME = "nigelnindo"

    //println("running")

    /**
     * Create an SMSSender objects. Can be re-used as many times as desired, and does not block the current thread.
     */

    val sMSSender = SMSSender(USER_NAME, API_KEY)

    /**
     * Use the sMSSender object to send a simple message
     *
     * The library handles failures gracefully, returning an Option type with a value if sending the message
     * was successful, or a None if it wasn't together with the appropriate error message
     *
     */

    sMSSender.send(BulkSimpleSMS(List("nigel","wiza","hungai"),"msg")).onComplete{
      case Success(gatewayResponse) => println(gatewayResponse)
      case Failure(err) =>
    }

    /**
     * The future code block is executed in a different thread (context)
     *
     * For example purposes, put current thread to sleep to sleep as we wait for a result from
     * the other thread.
     *
     * An alternative would be to call await instead of onComplete, but this blocks the current
     * thread while we await the result of the future, which is not a good thing, especially
     * in production.
     */

    Thread.sleep(10000)
  }
}

