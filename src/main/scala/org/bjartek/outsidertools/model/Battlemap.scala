package org.bjartek.outsidertools.model

import net.liftweb._ 
import mapper._ 
import http._ 
import js._ 
import JsCmds._ 
import JE._
import SHtml._ 
import util._
import scala.xml._

class Battlemap  extends LongKeyedMapper[Battlemap] with IdPK { 
  def getSingleton = Battlemap

  object owner extends MappedLongForeignKey(this, User) {
    override def dbIndexed_? = true
  }

  object title extends MappedPoliteString(this, 15) 

   object desc extends MappedTextarea(this, 4096) {
       override def displayName = "Description"
       override def textareaRows  = 3 
       override def textareaCols = 30
   }

  object grid extends MappedText(this) {
    override def defaultValue = ""
  }

  object cols extends MappedInt(this) {
   override def defaultValue = 10
  }  

  object rows extends MappedInt(this) {
   override def defaultValue = 10
  }  

  def toJson = {
    JsCrVar("map", 
      JsObj(
        ("title", title.is),
        ("desc", desc.is),
        ("id", id.is),
        ("rows", rows.is),
        ("cols", rows.is),
        ("grid", gridOrEmpty)
      )
    )
  } 

  def gridOrEmpty() : JsArray = {
   
    JSONParser.parse(grid.is) match {
      case Full(data) =>  {
        var list:List[JsObj] = ( data.asInstanceOf[List[Map[String, AnyVal]]].map{x=>  
          JsObj(
            ("id", x("id").asInstanceOf[String]), 
            ("tile", x("tile").asInstanceOf[String]), 
            ("col", x("col").asInstanceOf[Double].toInt), 
            ("row", x("row").asInstanceOf[Double].toInt), 
            ("note", x("note").asInstanceOf[String]), 
            ("desc", x("desc").asInstanceOf[String]), 
            ("enabled", x("enabled").asInstanceOf[Boolean]))
        }).toList
        JsArray(list :_*);
      }
      case _ => JsArray();
    }
  }

} 

object Battlemap extends Battlemap with LongKeyedMetaMapper[Battlemap] {
  def findById(id: Int)  = Battlemap.findAll(By(Battlemap.id, id))
  def findByIdAndOwner(id: Int, user:User) = Battlemap.find(By(Battlemap.id, id), By(Battlemap.owner, user.id))
}
