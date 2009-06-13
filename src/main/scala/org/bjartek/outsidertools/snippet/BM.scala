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

  def add(form: NodeSeq) = { 

    val battlemap = Battlemap.create.owner(User.currentUser)
     def checkAndSave(): Unit = {
      battlemap.validate match { 
        case Nil => battlemap.save ; S.notice("Added "+battlemap.desc + " " + battlemap.title + " " + battlemap.tile) 
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

  private def toShow =  Battlemap.findAll(By(Battlemap.owner, User.currentUser))

  private def desc(td: Battlemap, reDraw: () => JsCmd) =  {
    swappable(<span>{td.desc}</span>, 
    <span>{ajaxText(td.desc, 
      v => {td.desc(v).save; reDraw()})} 
    </span>)
  }

  private def title(td: Battlemap, reDraw: () => JsCmd) =  {
    swappable(<span>{td.title}</span>, 
    <span>{ajaxText(td.title, 
      v => {td.title(v).save; reDraw()})} 
    </span>)
  }

  private def tile(td: Battlemap, reDraw: () => JsCmd) =  {
    swappable(<span>{td.tile}</span>, 
    <span>{ajaxText(td.tile, 
      v => {td.tile(v).save; reDraw()})} 
    </span>)
  }
 
  
  private def doList(reDraw: () => JsCmd)(html: NodeSeq): NodeSeq = {
    toShow.flatMap(td => 
    bind("battlemap", html, 
      "title" -> title(td, reDraw), 
      "desc" -> desc(td, reDraw),
      "tile" -> tile(td, reDraw) 
   ))
  }

  def list(html: NodeSeq) = { 
    val id = S.attr("all_id").open_! 
 
    def inner(): NodeSeq = { 
      def reDraw() = SetHtml(id, inner()) 
        bind("battlemap", html, 
          "list" -> doList(reDraw) _) 
    } 
  inner() 
  }
} 

