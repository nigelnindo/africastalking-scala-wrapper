package api.airtime

import api.validations.AirtimeValidations
import api.validations.AirtimeValidations.Validated

import scala.concurrent.{ExecutionContext, Future}

import api.at_gateway.{Gateway, GatewayResponse, RequestCreator}
import api.common.Common.AIRTIME_URL

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.RawHeader

/**
 * Created by nigelnindo on 9/19/16.
 */
sealed trait Airtime
case class AirtimeSingle(recipient: AirtimeRecipient) extends Airtime
case class AirtimeMultiple(recipient: List[AirtimeRecipient]) extends Airtime

case class AirtimeRecipient(number: String, amount: Int)

case class AirtimeSender(username: String, apiKey: String) {

  private def recipientJSONObject(airtimeRecipient: AirtimeRecipient): String = {
    "{" + "\"phoneNumber\":" + airtimeRecipient.number+ ",\"amount\":" + airtimeRecipient.amount + "}"
  }

  private def httpRequestHelper(recipients: List[AirtimeRecipient]): HttpRequest = {
    val array: String =
      "[" + recipients.foldLeft("")((string, recipient: AirtimeRecipient) => string + "," +recipientJSONObject(recipient))+ "]"
    val recipient_array =
      "[" + array.substring(2) // remove comma added to beginning of JSON array by fold operation
    val jsonData = "{" + "\"username\":" + username + ",\"recipients\":" + recipient_array + "}"

    HttpRequest(HttpMethods.POST, AIRTIME_URL+"/send")
      .withHeaders(RawHeader("Accept","application/json"), RawHeader("apikey","apiKey"))
        .withEntity(jsonData)
  }

  private val requestCreator = new RequestCreator[Airtime] {
    override def createRequest(value: Airtime): HttpRequest = value match {
      case AirtimeSingle(recipient) => httpRequestHelper(List(recipient))
      case AirtimeMultiple(recipients) => httpRequestHelper(recipients)
    }
  }

  def send(airtime: Airtime)(implicit ex: ExecutionContext): Future[GatewayResponse] = {
    AirtimeValidations.validate(airtime) match {
      case Validated(_airtime,err) if err.isEmpty => sendToGateway(_airtime.get)
      case Validated(_airtime,err) if err.isDefined => Future{GatewayResponse(None, Some(err.get))}
    }
  }

  private def sendToGateway(airtime: Airtime)(implicit ex: ExecutionContext): Future[GatewayResponse] = {
    Gateway.send(airtime,requestCreator).recover{
      case err => GatewayResponse(None,Some(err))
    }
  }

}
