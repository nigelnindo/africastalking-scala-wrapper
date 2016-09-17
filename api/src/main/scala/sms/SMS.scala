package sms

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

import at_gateway.{GatewayResponse, Gateway, GatewayConverter}

/**
 * Created by nigelnindo on 9/17/16.
 */
sealed trait SMS

case class SingleSMS(number: String, message: String) extends SMS
case class BulkSMS(numbers: List[String], message: String) extends SMS
case class ShortCode(myShortCode: String, message: String) extends SMS
case class SenderId(mySenderId: String, message: String) extends SMS
//case class PremiumSMS() extends SMS

case object SMS {

  val smsGatewayConverter = new GatewayConverter[SMS] {
    override def convert(value: SMS): Gateway = value match {
      case SingleSMS(num, msg) => Gateway("")
      case BulkSMS(nums, msg) => Gateway("")
      case ShortCode(sc, msg) => Gateway("")
      case SenderId(sid, msg) => Gateway("")
    }
  }

  def send(sms: SMS): Future[GatewayResponse] = {
    Gateway.send(sms,smsGatewayConverter).recover{
      case _ => GatewayResponse(None) // if send future fails, return a none
    }
  }

}
