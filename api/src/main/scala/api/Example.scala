package api

import api.airtime.{AirtimeRecipient, AirtimeMultiple, AirtimeSender}

import scala.concurrent.ExecutionContext.Implicits.global // context for running future

import api.sms.{BulkSimpleSMS, SimpleSMS, SMSSender}

/**
 * Created by nigelnindo on 9/17/16.
 */

object Example {
  def main(args: Array[String]): Unit = {

    val API_KEY: String = "My Africa's Talking API key"
    val USER_NAME = "My Africa's talking Username"

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
     *
     */

    sMSSender.send(SimpleSMS("+254XXXXXX","Hi guys, this is my message")).onSuccess{
      case gatewayResponse => if (gatewayResponse.error.isEmpty) {
        gatewayResponse.response // do something with response
      }
    }

    val airtimeSender = AirtimeSender(USER_NAME, API_KEY)

    airtimeSender.send(AirtimeMultiple(List(AirtimeRecipient("+245",10),AirtimeRecipient("+254",20)))).onSuccess{
      case gatewayResponse => println(gatewayResponse)
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

