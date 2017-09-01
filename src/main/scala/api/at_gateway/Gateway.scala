package api.at_gateway

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContext, Future}
/**
 * Created by nigelnindo on 9/17/16.
 */

case class Gateway(uri: String){
  override def toString = uri
}

case class GatewayResponse(response: Option[String], error: Option[Throwable])

trait RequestCreator[A] {
  def createRequest(value: A): HttpRequest
}

case object Gateway{

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  /**
    * send function will covert a Future[HttpResponse] into a
    * Future[GatewayResponse].
    *
    * We will recover from any errors in the Future by providing
    * error details inside GatewayResponse.
    */

  def send(request: HttpRequest)
          (implicit ec: ExecutionContext): Future[GatewayResponse] =  {
    //TODO: Send actual HTTP requests to Africa's Talking
    println(request)
    val responseFuture: Future[HttpResponse] =
      Http().singleRequest(request)
    responseFuture.map( x => {
      println(x.toString())
      GatewayResponse(Some(x.entity.toString), None)
    })
  }

  /**
    * Auxiliary send function to be used by any interested parties so that
    * they can create a HttpRequest.
    */

  def send[T](value: T, requestCreator: RequestCreator[T])
             (implicit ec: ExecutionContext): Future[GatewayResponse] = {
    for {
      sendResult <- send(requestCreator.createRequest(value)).recover{
        case err => GatewayResponse(None, Some(err))
      }
    } yield sendResult
  }

}
