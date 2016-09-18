package api.sms

import scala.concurrent.{ExecutionContext, Future}

import api.common.Common._

import api.at_gateway.{RequestCreator, GatewayResponse, Gateway}

import scalaj.http.{HttpRequest, Http}

/**
 * Created by nigelnindo on 9/17/16.
 */
sealed trait SMS

case class SimpleSMS(number: String, message: String) extends SMS
case class BulkSimpleSMS(numbers: List[String], message: String) extends SMS
case class ShortCode(myShortCode: String, number: String, message: String) extends SMS
case class BulkShortCode(myShortCode: String, numbers: List[String], message: String) extends SMS
case class SenderId(mySenderId: String, message: String) extends SMS
case class BulkSenderId(mySenderId: String, numbers: List[String], message: String) extends SMS
// case class PremiumSMS() extends SMS

case class SMSSender(username: String, apiKey: String) {

  // TODO: before creating the request, validate the params that have been passed to it

  def httpRequestHelper(msg: String, nums: List[String], sender: Option[String]): HttpRequest = {
    var seq = Seq("username" -> username, "message" -> msg)
    // Add recipients
    seq = seq ++ Seq[(String,String)]( "to" -> nums.reduceLeft( _ + "," + _ ))
    // check if we should add the sender identifier i.e shortCode/senderId to the request
    sender match {
      case Some(_sender) => seq = seq ++ Seq("from" -> _sender)
    }
    Http(SMS_URL) postForm seq headers ("Accept" -> "application/json", "apikey" -> "apiKey")
  }

  val requestCreator= new RequestCreator[SMS] {
    override def createRequest(value: SMS): HttpRequest = value match {
      case SimpleSMS(num,msg) => httpRequestHelper(msg, List(msg), None)
      case BulkSimpleSMS(nums, msg) => httpRequestHelper(msg, nums, None)
      case ShortCode(sc, num, msg) => Http(SMS_URL)
      case BulkShortCode(sc, nums, msg) => Http(SMS_URL)
      case SenderId(sid, num, msg) => Http(SMS_URL)
      case BulkSenderId(sid, nums, msg) => Http(SMS_URL)
    }
  }

  def send(sms: SMS)(implicit ex: ExecutionContext): Future[GatewayResponse] = {
    Gateway.send(sms,requestCreator).recover{
      case _ => GatewayResponse(None)
    }
  }

}
