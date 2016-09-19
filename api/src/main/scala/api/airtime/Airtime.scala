package api.airtime

import scala.concurrent.{ExecutionContext, Future}

import api.at_gateway.{GateWayResponse, RequestCreator}
import api.common.Common.AIRTIME_URL

import scalaj.http.{Http, HttpRequest}

/**
 * Created by nigelnindo on 9/19/16.
 */
sealed trait Airtime
case class AirtimeSingle() extends Airtime
case class AirtimeMultiple() extends Airtime

case class Validated(value: Option[Airtime], err: Option[String])

object Airtime {

  private val requestCreator = new RequestCreator[Airtime] {
    override def createRequest(value: Airtime): HttpRequest = value match {
      case AirtimeSingle() => Http(AIRTIME_URL)
      case AirtimeMultiple() => Http(AIRTIME_URL)
    }
  }

  def validate(airtime: Airtime): Validated = {
    //todo: add validation
    case AirtimeSingle() => Validated(Some(airtime),None)
    case AirtimeMultiple() => Validated(Some(airtime),None)
  }

  def send(airtime: Airtime)(implicit ex: ExecutionContext): Future[GateWayResponse] = {
    validate(airtime) match {
      case Validated(_airtime,err) if err.isEmpty => sendToGateway(_airtime.get)
      case Validated(_airtime,err) if err.isDefined => Future{GateWayResponse(None, Some(err.get))}
    }
  }

  def sendToGateway(airtime: Airtime)(implicit ex: ExecutionContext): Future[GateWayResponse] = {

  }

}
