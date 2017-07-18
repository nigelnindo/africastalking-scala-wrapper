package api.sms

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader

import scala.concurrent.{ExecutionContext, Future}

import api.common.Common.SMS_URL

import api.at_gateway.{GateWayResponse, RequestCreator, Gateway}

/**
 * Created by nigelnindo on 9/17/16.
 */
sealed trait SMS

case class SimpleSMS(number: String, message: String) extends SMS
case class BulkSimpleSMS(numbers: List[String], message: String) extends SMS
case class ShortCode(myShortCode: String, number: String, message: String) extends SMS
case class BulkShortCode(myShortCode: String, numbers: List[String], message: String) extends SMS
case class SenderId(mySenderId: String, to: String, message: String) extends SMS
case class BulkSenderId(mySenderId: String, numbers: List[String], message: String) extends SMS
case class PremiumSMS(myShortCode: String, myPremiumKeyword: Option[String], number: String, message: String) extends SMS

private case class Validated(sms: Option[SMS], error: Option[String])

case class SMSSender(username: String, apiKey: String) {

  // convenience method for creating HttpRequest objects
  private def httpRequestHelper(msg: String, nums: List[String], sender: Option[String]): HttpRequest = {
    var seq = Seq("username" -> username, "message" -> msg)
    // Add recipients
    seq = seq ++ Seq[(String,String)]( "to" -> nums.reduceLeft( _ + "," + _ ))
    // check if we should add the sender identifier i.e shortCode/senderId to the request
    sender match {
      case Some(_sender) => seq = seq ++ Seq("from" -> _sender)
      case None =>
    }

    HttpRequest(HttpMethods.GET, SMS_URL)
      .withHeaders(RawHeader("accept","application/json"), RawHeader("apikey","apiKey"))

  }

  private def premiumHttpHelper(sms: PremiumSMS): HttpRequest = {
    var seq = Seq("username" -> username, "message" -> sms.message, "to" -> sms.number,
      "bulkSMSMode" -> "0", "from" -> sms.myShortCode)

    if (sms.myPremiumKeyword.isDefined){
      seq = seq ++ Seq("keyword" -> sms.myPremiumKeyword.get)
    }

    HttpRequest(HttpMethods.GET, SMS_URL)
      .withHeaders(RawHeader("accept","application/json"), RawHeader("apikey","apiKey"))

  }

  private val requestCreator = new RequestCreator[SMS] {
    override def createRequest(value: SMS): HttpRequest = value match {
      case SimpleSMS(num,msg) => httpRequestHelper(msg, List(num), None)
      case BulkSimpleSMS(nums, msg) => httpRequestHelper(msg, nums, None)
      case ShortCode(sc, num, msg) => httpRequestHelper(msg, List(num), Some(sc))
      case BulkShortCode(sc, nums, msg) => httpRequestHelper(msg, nums, Some(sc))
      case SenderId(sid, num, msg) => httpRequestHelper(msg, List(num), Some(sid))
      case BulkSenderId(sid, nums, msg) => httpRequestHelper(msg, nums, Some(sid))
      case _sms: PremiumSMS => premiumHttpHelper(_sms)
    }
  }

  private def validate(sms: SMS): Validated = sms match {
    // TODO: add validation logic i.e check phone numbers are valid
    // TODO: create a package for all your validations. Even better, allow user to define their own validations.
    case SimpleSMS(num,msg) => Validated(Some(sms),None)
    case BulkSimpleSMS(nums,msg) => Validated(Some(sms),None)
    case ShortCode(sc, num, msg) => Validated(Some(sms),None)
    case BulkShortCode(sc, nums, msg) => Validated(Some(sms),None)
    case SenderId(sid, num, msg) => Validated(Some(sms),None)
    case BulkSenderId(sid, nums, msg) => Validated(Some(sms),None)
    case PremiumSMS(sc, kw, num, msg) => Validated(Some(sms),None)
  }

  def send(sms: SMS)(implicit ex: ExecutionContext): Future[GateWayResponse] = {
    validate(sms) match {
      case Validated(_sms,err) if err.isEmpty => sendToGateway(_sms.get)
      case Validated(_sms,err) if err.isDefined => Future {GateWayResponse(None, Some(err.get))}
    }
  }

  private def sendToGateway(sms: SMS)(implicit ex: ExecutionContext): Future[GateWayResponse] = {
    Gateway.send(sms,requestCreator).recover{
      case err => GateWayResponse(None, Some(err.toString))
    }
  }

}
