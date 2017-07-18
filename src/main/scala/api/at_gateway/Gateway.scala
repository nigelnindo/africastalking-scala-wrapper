package api.at_gateway

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Created by nigelnindo on 9/17/16.
 */

case class Gateway(uri: String){
  override def toString = uri
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
}

case class GateWayResponse(response: Option[String] = None, error: Option[String])

trait RequestCreator[A] {
  def createRequest(value: A): HttpRequest
}

case object Gateway{

  def send(request: HttpRequest): Future[GateWayResponse] =  {
    //TODO: Send actual HTTP requests to Africa's Talking
    //GateWayResponse(Some(request.asString.toString), None)
    val responseFuture: Future[HttpResponse] =
      Http().singleRequest(request)
    responseFuture.map(x => GateWayResponse(Some(x.entity.toString), None))
  }

  def send[T](value: T, requestCreator: RequestCreator[T]): Future[GateWayResponse] = {
    for {
      sendResult <- send(requestCreator.createRequest(value)).recover{
        case err => GateWayResponse(None, Some(err.toString))
      }
    } yield sendResult
  }

}
