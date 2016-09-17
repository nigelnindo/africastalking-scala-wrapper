package api.sms

import scala.concurrent.{ExecutionContext, Future}

import api.common.Common._

import api.at_gateway.{RequestCreator, GatewayResponse, Gateway}

import scalaj.http.{HttpRequest, Http}

/**
 * Created by nigelnindo on 9/17/16.
 */
sealed trait SMS

case class SingleSMS(number: String, message: String) extends SMS
case class BulkSMS(numbers: List[String], message: String) extends SMS
case class ShortCode(myShortCode: String, message: String) extends SMS
case class SenderId(mySenderId: String, message: String) extends SMS
// case class PremiumSMS() extends SMS

case object SMS {

  val requestCreator= new RequestCreator[SMS] {
    override def createRequest(value: SMS): HttpRequest = value match {
      case SingleSMS(num, msg) => Http("https://www.google.com/")
      case BulkSMS(nums, msg) => Http("")
      case ShortCode(sc, msg) => Http("")
      case SenderId(sid, msg) => Http("")
    }
  }

  def send(sms: SMS)(implicit ex: ExecutionContext): Future[GatewayResponse] = {
    Gateway.send(sms,requestCreator).recover{
      /** *
        * synchronously executed, keep it simple
        * if send future fails, return a none
        */
      case _ => GatewayResponse(None)
    }
  }

}
