package api.airtime

import scala.concurrent.{ExecutionContext, Future}

import api.at_gateway.{Gateway, GateWayResponse, RequestCreator}
import api.common.Common.AIRTIME_URL

import scalaj.http.{Http, HttpRequest}

/**
 * Created by nigelnindo on 9/19/16.
 */
sealed trait Airtime
case class AirtimeSingle(recipient: AirtimeRecipient) extends Airtime
case class AirtimeMultiple(recipient: List[AirtimeRecipient]) extends Airtime

case class AirtimeRecipient(number: String, amount: Int)

private case class Validated(value: Option[Airtime], err: Option[String])

case class AirtimeSender(username: String, apiKey: String) {

  private def recipientJSONObject(airtimeRecipient: AirtimeRecipient): String = {
    "{" + "\"phoneNumber\":" + airtimeRecipient.number+ ",\"amount\":" + airtimeRecipient.amount + "}"
  }

  private def httpRequestHelper(recipients: List[AirtimeRecipient]): HttpRequest = {
    //val recipient_array: String = "[" + recipients.reduceLeft((a,b) => recipientJSONObject(a) + "," + recipientJSONObject(b))+ "]" //fails due to error in types
    val array: String = "[" + recipients.foldLeft("")((string, airtimeRecipient) => string + "," +recipientJSONObject(airtimeRecipient))+ "]"
    val recipient_array = "[" + array.substring(2) // fold operation adds a comma to the beginning of the json array, so remove it
    val jsonData = "{" + "\"username\":" + username + ",\"recipients\":" + recipient_array + "}"
    println(jsonData)
    Http(AIRTIME_URL+"/send") postData jsonData headers ("Accept" -> "application/json",
      "content-type" -> "application/json", "apikey" -> "apikey" )
  }

  private val requestCreator = new RequestCreator[Airtime] {
    override def createRequest(value: Airtime): HttpRequest = value match {
      case AirtimeSingle(recipient) => httpRequestHelper(List(recipient))
      case AirtimeMultiple(recipients) => httpRequestHelper(recipients)
    }
  }

  private def validate(airtime: Airtime): Validated = airtime match {
    case AirtimeSingle(recipient) => Validated(Some(airtime),None)
    case AirtimeMultiple(recipients) => Validated(Some(airtime),None)
  }

  def send(airtime: Airtime)(implicit ex: ExecutionContext): Future[GateWayResponse] = {
    validate(airtime) match {
      case Validated(_airtime,err) if err.isEmpty => sendToGateway(_airtime.get)
      case Validated(_airtime,err) if err.isDefined => Future{GateWayResponse(None, Some(err.get))}
    }
  }

  private def sendToGateway(airtime: Airtime)(implicit ex: ExecutionContext): Future[GateWayResponse] = {
    Gateway.send(airtime,requestCreator).recover{
      case err => GateWayResponse(None,Some(err.toString))
    }
  }

}
