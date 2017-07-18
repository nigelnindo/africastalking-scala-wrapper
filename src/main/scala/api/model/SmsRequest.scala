package api.model

/**
  * Created by nigelnindo on 7/18/17.
  */
case class SmsRequest(username: String, message: String, to: String, from: Option[String])
