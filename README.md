# africastalking-scala-wrapper
Future based Scala wrapper providing convenience methods for working with Africa's Talking REST API.

Currently provides support for SMS and Airtime APIs. If you have any questions, or have a feature request, please send an email to nigelnindo@gmail.com


### Quick Start

```scala
// Provide an execution context to run Future
import scala.concurrent.ExecutionContext.Implicits.global

val API_KEY: String = "My Africa's Talking API key"
val USER_NAME = "My Africa's talking Username"

val sMSSender = SMSSender(USER_NAME, API_KEY)

sMSSender.send(SimpleSMS("+254XXXXXX","Hi guys, this is my message")).onSuccess{
      case gatewayResponse => if (gatewayResponse.error.isEmpty) {
        gatewayResponse.response // do something with response
      }
    }
```

### General Notes

Create an object by using `SMSSender` or `AirtimeSender`, passing your Africa's Talking username and API key. Call the `send` method of this object to use the API.

The `send` method returns a `Future[GateWayResponse]`. You can compose it together with other Futures, process its result via callbacks, or block the current thread by awaiting the Future (not recommended). 

`GatewayResponse` has a signature `response: Option[String], error: Option[String]`. If no errors occur while processing the request, the error parameter will be a `None` type. The Future always succeeds, passing any errors that occur via the error parameter of `GatewayResponse`. Check whether any errors occured as shown in the Quick Start example.

## `SMSSender`
You can pass the following to its `send` method
```scala
SimpleSMS(number: String, message: String)
```
```scala
BulkSimpleSMS(numbers: List[String], message: String)
```
```scala
ShortCode(myShortCode: String, number: String, message: String)
```
```scala
BulkShortCode(myShortCode: String, numbers: List[String], message: String)
```
```scala
SenderId(mySenderId: String, to: String, message: String)
```
```scala
BulkSenderId(mySenderId: String, numbers: List[String], message: String)
```
```scala
PremiumSMS(myShortCode: String, myPremiumKeyword: Option[String], number: String, message: String)
```

## `AirtimeSender` 

You can pass the following to its `send` method

```scala
AirtimeSingle(recipient: AirtimeRecipient)
```
```scala
AirtimeMultiple(recipient: List[AirtimeRecipient])
```
`AirtimeRecipient` has the following signature:
```scala
AirtimeRecipient(number: String, amount: Int)
```
