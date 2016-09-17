package at_gateway

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

trait GatewayConverter[A] {
  def convert(value: A): Gateway
}

case object Gateway{

  def send(gateway: Gateway): Future[HttpResponse[String]] = Future {
    val request: HttpRequest = Http(gateway.toString)
    request.asString
  }

  def send[T](value: T, gatewayConverter: GatewayConverter[T]): Future[GatewayResponse] = {
    for {
      sendResult <- send(gatewayConverter.convert(value))
    } yield GatewayResponse(Some(sendResult.toString))
  }

}
