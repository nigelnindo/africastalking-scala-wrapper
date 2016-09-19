package api.at_gateway

import scalaj.http.HttpRequest

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by nigelnindo on 9/17/16.
 */
case class Gateway(uri: String){
  override def toString = uri
}

case class GateWayResponse(response: Option[String] = None, error: Option[String])

trait RequestCreator[A] {
  def createRequest(value: A): HttpRequest
}

case object Gateway{

  def send(request: HttpRequest): Future[GateWayResponse] = Future {
    //throw new Exception("just throw something")
    GateWayResponse(Some(request.asString.toString), None)
  }

  def send[T](value: T, requestCreator: RequestCreator[T]): Future[GateWayResponse] = {
    for {
      sendResult <- send(requestCreator.createRequest(value)).recover{
        case err => GateWayResponse(None, Some(err.toString))
      }
    } yield sendResult
  }

}
