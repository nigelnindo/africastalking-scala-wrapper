package api.validations

import api.sms._

/**
  * Created by nigelnindo on 7/21/17.
  */

case class Validated(sms: Option[SMS], error: Option[String])

object SMSValidations {
  def validate(sms: SMS): Validated = sms match {
    // TODO: Even better, allow user to define their own validations.
    /**
      * Additional validations can be added a a list of functions. Evaluate all functions until they either
      * fail or when we don't have any more validations to run.
      */
    case SimpleSMS(num,msg) => Validated(Some(sms),None)
    case BulkSimpleSMS(nums,msg) =>
      if (nums.isEmpty) Validated(Some(sms), Some("Provide at least one phone number")) else Validated(Some(sms),None)
    case ShortCode(sc, num, msg) => Validated(Some(sms),None)
    case BulkShortCode(sc, nums, msg) => Validated(Some(sms),None)
      if (nums.isEmpty) Validated(Some(sms), Some("Provide at least one phone number")) else Validated(Some(sms),None)
    case SenderId(sid, num, msg) => Validated(Some(sms),None)
    case BulkSenderId(sid, nums, msg) => Validated(Some(sms),None)
      if (nums.isEmpty) Validated(Some(sms), Some("Provide at least one phone number")) else Validated(Some(sms),None)
    case PremiumSMS(sc, kw, num, msg) => Validated(Some(sms),None)
  }
}
