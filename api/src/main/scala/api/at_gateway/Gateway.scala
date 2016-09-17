package api.at_gateway

import scalaj.http.{Http,HttpRequest,HttpResponse}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by nigelnindo on 9/17/16.
 */
case class Gateway(uri: String){
  override def toString = uri
}

case class GatewayResponse(response: Option[String] = None)

trait RequestCreator[A] {
  def createRequest(value: A): HttpRequest
}

case object Gateway{

  def send(request: HttpRequest): Future[GatewayResponse] = Future {
    GatewayResponse(Some(request.asString.toString))
  }

  def send[T](value: T, requestCreator: RequestCreator[T]): Future[GatewayResponse] = {
    for {
      sendResult <- send(requestCreator.createRequest(value)).recover{
        /**
         * TODO: Pattern match to catch different error types (i.e No Connection error, AT errors)
         */
        case _ => GatewayResponse(None)
      }
    } yield sendResult
  }

}
