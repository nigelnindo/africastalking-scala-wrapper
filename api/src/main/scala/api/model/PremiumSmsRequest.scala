package api.model

/**
  * Created by nigelnindo on 7/18/17.
  */
case class PremiumSmsRequest(username: String, message: String, to: String,
                             bulkSMSMode: String, from: String, keyword: Option[String])
