package org.bjartek.outsidertools.render

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

object RpolTextRenderer {
  def apply(character:Character) = {
    println("<b>Name:</b> "  + character.name);
    println("<b>Player: </b>" + character.player);
    println("<b>Race: </b>" + character.race);
    println("<b>Class and Level:</b> " + character.clazz + " " + character.level)
    println("<b>Alignement: </b>" + character.alignment)
    println("<b>Deity: </b>" + character.deity);
    println("<b>XP: </b>" + character.xp);
    println("<b>Laug:</b>")
    println("<b>Tittel:</b>")
    println("<b>Tjenestepoeng:</b>")
    println("")
    println("<b>Age:</b>")
    println("<b>Gender:</b>")
    println("<b>Size:</b>")
    println("<b>Height:</b>")
    println("<b>Weight:</b>")
    println("<b>Eyes:</b>")
    println("<b>Hair:</b>")
    println("")

    for(stat <- character.stats) {
      println(stat.printName + ": " + stat.value.toString);
    }
    
    println("")
    println("<b>HP: </b>" + character.hp)
    println("<b>Bloodied:</b> " + character.bloddy)
    println("<b>Surge Value:</b> " + character.sv)
    println("<b>Surges/Day:</b> " + character.surges )
    println("<b>Init:</b> " + character.init)
    println("<b>Speed:</b> " + character.speed)
    println("")

    println("<b>Passive Perception: </b>" + character.passivePerception)
    println("<b>Passive Insight: </b>" + character.passiveInsight)


    println("")
    println("<b>AC: </b>" + character.ac)
    println("<b>Fortitude: </b>" + character.fort)
    println("<b>Reflex: </b>" + character.ref)
    println("<b>Will:</b> " + character.will)

    println("")
    println("<b>Basic attacks</b>")
    character.basicAttacks.foreach(attack => println(attack._1 + " + " + attack._2 + " vs AC. " + attack._3 + "+" + attack._4 + " dmg"))

    println("")
    println("<b>Skills</b>")
    for(stat <- character.stats; skill <- stat.skills) {
      println(skill.name + ": " + skill.value);
    }

    println("")
    println("<b>Languages</b>")
    println(character.languages.mkString(", "));

    println("")
    println("<b>Feats</b>")
    println(character.feats.mkString(", "));


    println("")
    println("<b>Race Featues</b>")
    println(character.raceFeatures.mkString(", "));

    println("")
    println("<b>Class Featues</b>")
    println(character.classFeatures.mkString(", "));

    println("")
    println("<b>Equipment</b>");
    println(character.equipment.map(_.name).mkString(", "))

    println("")
    println("<b>At-Will powers</b>")
    println(character.powernames("At-Will").mkString(", "));

    println("")
    println("<b>Encounter powers</b>")
    println(character.powernames("Encounter").mkString(", "));

    println("")
    println("<b>Daily powers</b>")
    println(character.powernames("Daily").mkString(", "));

    println("")
    println("<b>Rituals</b>")
    println(character.rituals.mkString(", "))

    println("")
    println("<b>Backround/History</b>")

    println("")
    println("Genearted by OutsiderTools - Character Visualizer.")
  }
}
