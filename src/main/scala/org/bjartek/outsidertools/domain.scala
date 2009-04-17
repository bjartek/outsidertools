package org.bjartek.outsidertools.domain

import scala.util.matching.Regex
import scala.collection.mutable.HashMap

case class Equipment(var name:String, var kind:String, var count:Int, var ecount:Int)

case class Armor(n:String, var slot:String, c:Int, ec:Int) extends Equipment(n, "Armour", c, ec) {
  var magic: String = _
 }
case class Weapon(n:String, var slot:String, var dmgDice:String, var category:String, var proficiency:Int, var groups:String, c:Int, ec:Int) extends Equipment(n, "Weapon", c, ec){
  var magic: String = ""

  def bonus: Int = {

    def extractBonus() : Int = {
      val numRegex = """[\w\s]+ \+(\d+)""".r
      val numRegex(bonus) = magic
      bonus.toInt
    }

    if(magic != "") {
     extractBonus
    } else {
      0
    }
  }

  def toHit = proficiency + bonus
  def damage = bonus 
}

case class Ritual(n:String, c:Int, ec:Int)  extends Equipment(n, "Ritual", c, ec)

case class Power(var name:String) {
  
  var usage:String = ""
  var keywords:String = ""
  var attack:String = ""
  var hit: String = ""
  var level:Int = 0
  var attackType:String = ""

  override def toString = {
    "Level " + level + " " + usage + " power: "  + name + " ( " + attackType +  " - " + keywords + " ) " + attack + " " + hit
  }

}

case class Rules(val name:String, val kind:String, val field: String, val value:String)                                                                                                                             

case class Skill(val name:String, val value:Int)

case class Stat(val name:String, val value:Int, val mod:Int, val check:Int, val skills:List[Skill]) {

    def printName : String  = { 
      name.slice(0,3).capitalize
    }

    def numberOfSkills : String = {
      skills.length.toString
    }

}


case class Character(stats:List[Stat]) {

  def passiveInsight = stats.filter(f => f.name == "Wisdom").head.skills.filter(f => f.name == "Insight").head.value + 10
  def passivePerception = stats.filter(f => f.name == "Wisdom").head.skills.filter(f => f.name == "Perception").head.value + 10

  var powers:HashMap[String, Power] = _

  def powernames(pt:String) = for(power <- powers.filter(f => f._2.usage == pt)) yield power._2.name

  var name:String = ""
  var player:String = ""
  var deity: String = ""
  var race:String = ""
  var clazz:String = ""
  var level:String = ""
  var alignment: String = ""
  var xp: String = ""

  var hp = 0
  def bloddy : Int = (hp / 2.0).floor.toInt
  def sv: Int = {
    var sv = (hp / 4.0).floor.toInt
    
     if(race equals "Dragonborn")  
      sv += stats.filter(_.name =="Constitution").head.mod
     sv
  }

  var surges = 0
  var init = 0
  var speed = 0

  var ac = 0
  var fort = 0
  var ref = 0
  var will = 0

  var languages: List[String] = _
  var feats: List[String] = _
  var raceFeatures:  List[String] = _
  var classFeatures:List[String] = _

  var rituals:List[String] = _
  var equipment:List[Equipment] = _

  lazy val meleeWpns = equipment.filter( _ match {
    case Weapon(_, _, _, kind, _, bonus, count, equip) if(equip == 1 && kind.contains("Melee")) => true
    case _ =>  false
  })

  lazy val  rangedWpns = equipment.filter( _ match {
    case Weapon(_, _, _, kind, _, bonus, count, equip) if(equip == 1 && kind.contains("Ranged")) => true
    case _ =>  false
  })

  def basicAttacks() = {
    val dex = stats.filter(_.name == "Dexterity").head

    val ranged = for(w <- rangedWpns) yield {
      val wpn = w.asInstanceOf[Weapon]
       
       (wpn.name, wpn.toHit + dex.check, wpn.dmgDice, dex.mod + wpn.damage)
    }

    val str = stats.filter(_.name == "Strength").head

    val melee = for(w <- meleeWpns) yield {
      val wpn = w.asInstanceOf[Weapon]
       (wpn.name, wpn.toHit + str.check, wpn.dmgDice, str.mod + wpn.damage)
    }

    melee ::: ranged 
  }

}


