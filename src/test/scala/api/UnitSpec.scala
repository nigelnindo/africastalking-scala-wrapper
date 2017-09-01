package api

import api.sms._
import org.scalatest.{AsyncFlatSpec, Matchers}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by nigelnindo on 7/21/17.
  */
class UnitSpec extends AsyncFlatSpec with Matchers {

  val API_KEY: String = "My Africa's Talking API key"
  val USER_NAME = "My Africa's talking Username"

  val sMSSender = SmsSender(USER_NAME, API_KEY)

  it should "not allow an empty list of numbers for BulkSimpleSMS, BulkShortCode, and BulkSenderId" in {
    val bulkSimpleSMSFuture = sMSSender.send(SimpleSMS(List(),"Send a message"))
    val bulkShortCodeFuture = sMSSender.send(ShortCode("", List(), "Send a message"))
    val bulkSenderIdFuture = sMSSender.send(SenderId("",List(),"Send a message"))

    bulkSimpleSMSFuture map {result => assert(result.error.isDefined)}
    bulkShortCodeFuture map {result => assert(result.error.isDefined)}
    bulkSenderIdFuture map {result => assert(result.error.isDefined)}
  }

}
