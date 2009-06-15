package org.bjartek.outsidertools.snippet

import org.bjartek.outsidertools._
import model._ 
import net.liftweb._ 
import http._ 
import SHtml._ 
import S._ 
import js._ 
import JsCmds._ 
import mapper._ 
import util._ 
import Helpers._ 
import scala.xml.{NodeSeq, Text}

class BM { 

  def listMaps (xhtml : NodeSeq) : NodeSeq = User.currentUser match { 
    case Full(user) => { 
      val maps : NodeSeq = user.allMaps match { 
        case Nil => Text("You have no maps yet")
        case maps => maps.flatMap({map => 
          bind("m", chooseTemplate("map", "entry", xhtml), 
          "title" -> <a href={"/map/" + map.id.is + "/edit"}> {map.title.is}</a>, 
          "desc" -> Text(map.desc.toString), 
          "tile" -> <div class= { Text(map.tile.toString) + " ot_element" } />)
        }) 
      } 
      bind("map", xhtml, "entry" -> maps)
    } 
    case _ => <lift:embed what="welcome_msg" /> 
  } 

  def add(form: NodeSeq) = { 
    val battlemap = Battlemap.create.owner(User.currentUser)
      def checkAndSave(): Unit = {
      battlemap.validate match { 
        case Nil => battlemap.save; S.redirectTo("/map/" + battlemap.id.is + "/edit");
        case xs => S.error(xs) ; S.mapSnippet("BM.add", doBind) 
      } 
    }

    def doBind(form: NodeSeq) = {
      bind("battlemap", form, 
        "title" -> battlemap.title.toForm, 
        "desc" -> battlemap.desc.toForm, 
        "tile" -> battlemap.tile.toForm, 
        "submit" -> submit("New", checkAndSave)) 
    }
    doBind(form) 
  } 
  
  val a = List.fromString("abcdefghijklmnopqrstuvwxyz")

  val al = a.length

  //this blows when 0 or above a big number, but that does not matter here.
  private def alpha(num:Int) : String = {
  
    if(num <= al)  {
       a(num-1).toString
     } else {
       a((num / al) - 1).toString + a(num % al - 1)
     }
  }

   def paint(xhtml : NodeSeq) : NodeSeq = {
     
    S.param("map_id") match {
      case Full(id) => Battlemap.findById(id.toInt) match {
        case map :: Nil => {

          bind("map", xhtml, 
            "cols" -> 1.to(map.cols).map(x => <div> { alpha(x).toString } </div> ),
            "rows" -> 1.to(map.rows).map(x => <div> { x.toString } </div> ),
            "cells" -> map.grid.map(_.toForm))
             
        }
        case _ => Text("Count not find map with id " + id)
      }

      case _ => Text("No map id provided")
    }
  }
  

} 

