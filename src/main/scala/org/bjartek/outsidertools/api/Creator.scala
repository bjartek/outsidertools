package org.bjartek.outsidertools.model

import net.liftweb._
import http._
import rest._
import util._
import mapper._
import Helpers._
import net.liftweb.util.Helpers.toLong
import scala.xml.{Node, NodeSeq}
import org.bjartek.outsidertools.model._
import js._ 
import JE._
import JsCmds._ 

object RestAPI { 
  def dispatch: LiftRules.DispatchPF = { 
    case r @ Req("api" ::  "creator" :: mid :: Nil, "",  PostRequest) => 
      () => storeMap(mid,r) 
    case Req(List("api", _), "", _) => failure _ 
  } 

   def failure(): Box[LiftResponse] = { 
    val ret: Box[NodeSeq] = Full(<op id="FAILURE"></op>)
     Full(NotFoundResponse())
    }

  def storeMap(mid:String, request:Req) : Box[LiftResponse] = {
    
    try {
      val id = mid.toInt
     
      println(request);
      User.currentUser match {
        case  Full(user) => {
          var response: Box[LiftResponse] = Full(BadResponse())
          for {
            grid <- request.param("grid")
            cols <- request.param("cols")
            rows <- request.param("rows")
            map <- Battlemap.findByIdAndOwner(id, user)
            data <- JSONParser.parse(grid)
          } yield {
            println("We have valid stuff");
            map.grid(grid).rows(rows.toInt).cols(cols.toInt).validate match {
             case Nil => {
               map.save
               response = Full(OkResponse())
              }
             case x => 
           }
          }
          response
        }
        case _ => Full(UnauthorizedResponse("No session for your"))
      }
    } catch  {
      case _ => Full(BadResponse())
    }
  } 
}

