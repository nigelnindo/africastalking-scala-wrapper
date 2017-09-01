package api.validations

import api.airtime.{Airtime, AirtimeMultiple, AirtimeSingle}

/**
  * Created by nigelnindo on 8/31/17.
  */

trait AirtimeExceptions extends Exception

object AirtimeValidations {

  case class Validated(airtime: Option[Airtime], error: Option[Throwable])

  def validate(airtime: Airtime): Validated = airtime match {
    case AirtimeSingle(recipient) => Validated(Some(airtime),None)
    case AirtimeMultiple(recipients) => Validated(Some(airtime),None)
  }

}
