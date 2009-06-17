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
     
      User.currentUser match {
        case  Full(user) => 
          request.param("grid") match {
            case Full(grid) => 
              Battlemap.findByIdAndOwner(id, user) match {
                case Full(map) => 
                  JSONParser.parse(grid) match {
                    case Full(data) =>  {
                      println("FOOOOO");
                       map.grid(grid).validate match {
                         case Nil => map.save; println("We have saved the field"); println(map.grid.is); Full(OkResponse())
                         case x => println("foobar"); println(x); Full(BadResponse())
                      }
                    }
                    case _ => println("Could not parse Json");Full(BadResponse())
                }
                case _ => println("Could not find map"); Full(BadResponse()) 
             }
            case _ => println("grid param does not exist"); Full(BadResponse())
          }
        case _ => Full(UnauthorizedResponse("No session for your"))
      }
    } catch  {
      case _ => Full(BadResponse())
    }
  } 
}

