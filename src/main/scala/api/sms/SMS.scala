package api.sms

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.client.RequestBuilding._
import api.validations.SMSValidations.Validated
import api.validations.{SMSValidations}

import scala.concurrent.{ExecutionContext, Future}

import api.common.Common.SMS_URL
import api.at_gateway.{GatewayResponse, RequestCreator, Gateway}
import api.model.{PremiumSmsRequest, SmsRequest}

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import api.serializers.Marshal._

/**
 * Created by nigelnindo on 9/17/16.
 */
sealed trait SMS

case class SimpleSMS(numbers: List[String], message: String) extends SMS
case class ShortCode(myShortCode: String, numbers: List[String], message: String) extends SMS
case class SenderId(mySenderId: String, numbers: List[String], message: String) extends SMS
case class PremiumSMS(myShortCode: String, myPremiumKeyword: Option[String], number: String, message: String) extends SMS

/**
  * SmsSender requires your username and apiKey.
  *
  * You can optionally pass in url as a final parameter. This is
  * important for testing your code using Africa's Talking's sandbox
  * environment, or if the production URL's defined in the library
  * are outdated.
  */

case class SmsSender(username: String, apiKey: String, url: String = SMS_URL)
                    (implicit ec: ExecutionContext) {

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

    //application/x-www-form-urlencoded
    HttpRequest(HttpMethods.POST, url)
      .withHeaders(RawHeader("Accept","application/json"), RawHeader("apikey",apiKey))
      .withEntity(FormData(seq.toMap).toEntity)

    /** Code commented out below replaces everything above. Doesn't work because we can't
      * set Content-Type to 'application/json'
      */
    /*
    val requestObject = SmsRequest(username, msg, nums.reduceLeft( _ + "," + _), sender)
    Post(url, requestObject)
      .withHeaders(RawHeader("Accept","application/json"),
        RawHeader("apikey", apiKey))
    */
  }

  private def premiumHttpHelper(sms: PremiumSMS): HttpRequest = {

    var seq = Seq("username" -> username, "message" -> sms.message, "to" -> sms.number,
                "bulkSMSMode" -> "0", "from" -> sms.myShortCode)
    if (sms.myPremiumKeyword.isDefined){
        seq = seq ++ Seq("keyword" -> sms.myPremiumKeyword.get)
      }

    HttpRequest(HttpMethods.POST, url)
      .withHeaders(RawHeader("Accept","application/json"), RawHeader("apikey",apiKey))
      .withEntity(FormData(seq.toMap).toEntity)

    /**
      * Code below can also replace code above if Content-Type is resolved
      */
    /*
    val requestObject = PremiumSmsRequest(username, sms.message, sms.number, "0", sms.myShortCode, sms.myPremiumKeyword)
    Post(url, requestObject)
        .withHeaders(RawHeader("Accept","application/json"),
          RawHeader("apikey", apiKey))
    */

  }

  private val requestCreator = new RequestCreator[SMS] {
    override def createRequest(value: SMS): HttpRequest = value match {
      case SimpleSMS(nums, msg) => httpRequestHelper(msg, nums, None)
      case ShortCode(sc, nums, msg) => httpRequestHelper(msg, nums, Some(sc))
      case SenderId(sid, nums, msg) => httpRequestHelper(msg, nums, Some(sid))
      case _sms: PremiumSMS => premiumHttpHelper(_sms)
    }
  }

  def send(sms: SMS): Future[GatewayResponse] = {
    SMSValidations.validate(sms) match {
      case Validated(_sms,err) if err.isEmpty => sendToGateway(_sms.get)
      case Validated(_sms,err) if err.isDefined => Future {GatewayResponse(None, Some(err.get))}
    }
  }

  private def sendToGateway(sms: SMS): Future[GatewayResponse] = {
    Gateway.send(sms,requestCreator).recover{
      case err => GatewayResponse(None, Some(err))
    }
  }

}
