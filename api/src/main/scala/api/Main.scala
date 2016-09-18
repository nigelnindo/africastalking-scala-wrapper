package api

import scala.concurrent.ExecutionContext.Implicits.global

import api.sms.{SMSSender, SingleSMS}

/**
 * Created by nigelnindo on 9/17/16.
 */

object Main {
  def main(args: Array[String]): Unit = {

    import scala.util.{Failure, Success}

    val API_KEY: String = "98f96d823dacff5c49bc752f1696f178b2d450033ced27e6c45d8cc59bf4da6f"

    println("running")

    SMSSender("nigelnindo",API_KEY).send(SingleSMS("", "")).onComplete{
      case Success(gatewayResponse) => println(gatewayResponse)
      case Failure(err) => println(err)
    }

    Thread.sleep(10000)
  }
}

