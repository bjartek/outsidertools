package org.bjartek.outsidertools.render

import org.bjartek.outsidertools.domain._

/*
object HtmlRenderer {

  def apply(character:Character) = {
    val stats = for(stat <- character.stats) yield {
      renderStat(stat) :: renderSkills(stat.skills.tail)
    }
    <table> { stats } </table>
  }


   def renderSkills(skills: List[Skill]) = {
     for(skill <- skills) yield {
      <tr><td class="small skill">{skill.name.toString}</td><td class="small">{ skill.value.toString }</td></tr>
   }
  }

  def renderStat(stat:Stat) = {
    val firstSkill = stat.skills.head

    <tr class="stat"><td rowspan={ stat.numberOfSkills }>{ stat.printName }</td><td rowspan={ stat.numberOfSkills }>{ stat.check.toString } </td><td rowspan={ stat.numberOfSkills } class="small">{ stat.value.toString  }</td><td class="small skill">{ firstSkill.name }</td><td class="small">{firstSkill.value.toString }</td></tr>
  }

}
*/


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

    for(kv <- character.stat) {
      lines += kv._1 + ": " + kv._2.score.toString
    }
    
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
    for(skill <- character.skill) {
      lines += skill._2.htmlName
    }
    
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
