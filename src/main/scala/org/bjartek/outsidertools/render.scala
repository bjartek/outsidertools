package org.bjartek.outsidertools.render

import org.bjartek.outsidertools.domain._

class HtmlCardRenderer(val character:Character) {

  def generate =  {
    val stats = for(stat <- character.statSkills) yield {
      renderStat(stat) :: renderSkills(stat._2.tail)
    }
    <table> { stats } </table>
  }


   def renderSkills(skills: List[String]) = {
     for(s <- skills) yield { 
      val  skill =  character.skill(s)
      <tr><td class="small skill">{skill.name.toString}</td><td class="small">{ skill.score.toString }</td></tr>
   }
  }

  def renderStat(pair:Pair[String, List[String]]) = {
    val firstSkill = character.skill(pair._2.head)
    val stat = character.stat(pair._1)
    val numSkills = pair._2.length.toString

    <tr class="stat"><td rowspan={ numSkills }>{ stat.shortName }</td><td rowspan={ numSkills }>{ stat.check(character.level).toString } </td><td rowspan={ numSkills } class="small">{ stat.score.toString  }</td><td class="small skill">{ firstSkill.name }</td><td class="small">{firstSkill.score.toString }</td></tr>
  }
}


class  RpolTextRenderer(val character:Character) {


    var lines:List[String] = Nil;
    
    def generate() = {
      lines += "<b>Name:</b> "  + character.name
      lines += "<b>Player: </b>" + character.player
    lines += "<b>Race: </b>" + character.race
    lines += "<b>Class and Level:</b> " + character.clazz + " " + character.level
    lines += "<b>Alignement: </b>" + character.alignment
    lines += "<b>Deity: </b>" + character.deity
    lines += "<b>XP: </b>" + character.xp
    lines += "<b>Laug:</b>"
    lines += "<b>Tittel:</b>"
    lines += "<b>Tjenestepoeng:</b>"
    lines += ""
    lines += "<b>Age:</b> " + character.age
    lines += "<b>Gender:</b> " + character.gender
    lines += "<b>Size:</b> " + character.size
    lines += "<b>Height:</b> " + character.age
    lines += "<b>Weight:</b> " + character.weight
    lines += ""

    
    character.statOrder.foreach(s => lines += s + ": " + character.stat(s).score);
    
    lines += ""

    lines +="<b>HP: </b>" + character.hp
    lines +="<b>Bloodied:</b> " + character.bloddy
    lines +="<b>Surge Value:</b> " + character.sv
    lines +="<b>Surges/Day:</b> " + character.surges
    lines +="<b>Init:</b> " + character.init
    lines +="<b>Speed:</b> " + character.speed
    lines +=""

    lines +="<b>Passive Perception: </b>" + character.passivePerception
    lines +="<b>Passive Insight: </b>" + character.passiveInsight


    lines +=""
    lines +="<b>AC: </b>" + character.ac
    lines +="<b>Fortitude: </b>" + character.fort
    lines +="<b>Reflex: </b>" + character.ref
    lines +="<b>Will:</b> " + character.will

    lines +=""
    lines +="<b>Basic attacks</b>"
    renderPowers(_.name.contains("Basic"))

    lines += ""
    lines += "<b>Skills</b>"
    character.skillOrder.foreach(s => lines += character.skill(s).htmlName)

   
    lines += ""
    lines += "<b>Languages</b>"
    lines += character.languages.mkString(", ")

    lines += ""
    lines += "<b>Feats</b>"
    lines += character.feats.map(feat => feat._2 match { case Some(x) => "<a href=\"" + x + "\">" + feat._1 + "</a>"; case _ => feat._1 }).mkString(", ")


    lines += ""
    lines += "<b>Race Featues</b>"
    lines += character.racialTrait.mkString(", ")

    lines += ""
    lines += "<b>Class Featues</b>"
    lines += character.classFeature.mkString(", ")

    lines += ""
    lines += "<b>Equipment</b>"
    lines += character.equipment.filter(kv => kv._2.kind != "Ritual").map(kv => kv._2.htmlName).mkString(", ")

    lines += ""
    lines += "<b>At-Will powers</b>"
    renderPowers(_.usage == "At-Will")

    lines += ""
    lines += "<b>Encounter powers</b>"
    renderPowers(_.usage == "Encounter")

    lines += ""
    lines += "<b>Daily powers</b>"
    renderPowers(_.usage == "Daily")

    lines += ""
    lines += "<b>Rituals</b>"
    lines += character.equipment.filter(kv => kv._2.kind == "Ritual").map(kv => kv._2.htmlName).mkString(", ")

    lines += ""
    lines += "<b>Følgesvenner</b>"
    lines += character.companions

    lines += ""
    lines += "<b>Utseende</b>"
    lines += character.appearance

    lines += ""
    lines += "<b>Personlighets trekk</b>"
    lines += character.traits


    lines += ""
    lines += "Genearted by OutsiderTools - Rpol Character Visualizer."

 lines
  }

  def renderPowers(f: Power => Boolean) {
    for{
      attack <- character.power if f(attack._2)
     } yield {
      if(attack._2.weapons.toList.isEmpty) {
        lines += attack._2.htmlName + " as " + attack._2.action
      }else {
        for(weapon <- attack._2.weapons) yield {
         lines += attack._2.htmlName + " with " + weapon._2.name + " <blue>+" + weapon._2.hit + " vs " + weapon._2.defense + "</blue> <red>" + weapon._2.dmg + " dmg</red> as " + attack._2.action
        }
      }
    }
  }
}
