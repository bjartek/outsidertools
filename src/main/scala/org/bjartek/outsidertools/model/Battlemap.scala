package org.bjartek.outsidertools.model

import net.liftweb._ 
import mapper._ 
import http._ 
import SHtml._ 
import util._
import scala.xml._

class Battlemap  extends LongKeyedMapper[Battlemap] with IdPK { 
  def getSingleton = Battlemap

  object owner extends MappedLongForeignKey(this, User) 

  object title extends MappedPoliteString(this, 15) 

  object tile extends MappedPoliteString(this, 15) {
    override def fieldId = Some(Text("previewTile"))
  }

  object desc extends MappedPoliteString(this, 128) 

  object cols extends MappedInt(this) {
   override def defaultValue = 10
  }  

  object rows extends MappedInt(this) {
   override def defaultValue = 10
  }  


  def grid = {
    for{
      r <- 1.to(rows.is)
      c <- 1.to(cols.is)
    } yield {
      Cell(r,c, tile.is)
    }
  }

} 

object Battlemap extends Battlemap with LongKeyedMetaMapper[Battlemap] {
  def findById(id: Int)  = Battlemap.findAll(By(Battlemap.id, id))
}

case class Cell(val row:Int, val col:Int, val tile:String) {

  def toForm = {
    <div id={ ( row + "_" + col) } class={ "tile drop cell " + " col" + col + " row" + row + " " + tile}></div>
  }
}
