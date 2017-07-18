package api.serializers

import api.model.{PremiumSmsRequest, SmsRequest}
import spray.json.DefaultJsonProtocol

/**
  * Created by nigelnindo on 7/18/17.
  */

object Marshal extends DefaultJsonProtocol{
  implicit val smsRequestFormat = jsonFormat4(SmsRequest)
  implicit val premiumSmsFormat = jsonFormat6(PremiumSmsRequest)
}

