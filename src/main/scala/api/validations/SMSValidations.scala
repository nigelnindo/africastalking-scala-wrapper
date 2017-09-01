package api.validations

import api.sms._

/**
  * Created by nigelnindo on 7/21/17.
  */

trait SmsExceptions extends Exception
case class NoPhoneNumberException(msg: String) extends SmsExceptions

object SMSValidations {

  case class Validated(sms: Option[SMS], error: Option[Throwable])

  def validate(sms: SMS): Validated = sms match {
    // TODO: Even better, allow user to define their own validations.
    /**
      * Additional validations can be added a a list of functions. Evaluate all functions until they either
      * fail or when we don't have any more validations to run.
      */
    case SimpleSMS(nums,msg) =>
      if (nums.isEmpty) Validated(Some(sms), Some(NoPhoneNumberException("Provide at least one phone number"))) else Validated(Some(sms),None)
    case ShortCode(sc, nums, msg) => Validated(Some(sms),None)
      if (nums.isEmpty) Validated(Some(sms), Some(NoPhoneNumberException("Provide at least one phone number"))) else Validated(Some(sms),None)
    case SenderId(sid, nums, msg) => Validated(Some(sms),None)
      if (nums.isEmpty) Validated(Some(sms), Some(NoPhoneNumberException("Provide at least one phone number"))) else Validated(Some(sms),None)
    case PremiumSMS(sc, kw, num, msg) => Validated(Some(sms),None)
  }
}
