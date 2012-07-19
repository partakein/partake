package controllers
import java.util.ArrayList
import in.partake.model.UserEx
import in.partake.resource.MessageCode
import in.partake.controller.PartakeActionContext

class ActionContext(val loginUser: Option[UserEx], val sessionToken: String, val currentURL: String) {
  var redirectURL: Option[String] = None
  var messages: Seq[MessageCode] = List.empty

  var sessionsToAddResult: List[(String, String)] = List.empty
  def shouldAddToSession(key: String, value: String): Unit =
    sessionsToAddResult = (key, value) :: sessionsToAddResult

  var headers: List[(String, String)] = List.empty
  def addHeader(key: String, value: String) =
    headers = (key -> value) :: headers
}
