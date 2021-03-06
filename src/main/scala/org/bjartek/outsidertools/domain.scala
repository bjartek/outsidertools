package org.bjartek.outsidertools.domain

import scala.collection.immutable.Map

case class Equipment(var name:String, var kind:String, var count:Int, var ecount:Int, var url:String) {
  lazy val htmlName = {
    "<a href=" + url + ">" + name + "</a>"
  }
}
case class Armor(n:String, c:Int, ec:Int, u:String) extends Equipment(n, "Armour", c, ec, u)
case class MagicArmour(var magic: String, var magicUrl: String, n:String, c:Int, ec:Int, u:String) extends Equipment(n, "MagicArmour", c, ec, u)
case class Weapon(n:String, c:Int, ec:Int, u:String) extends Equipment(n, "Weapon", c, ec, u)
case class MagicWeapon(n:String, c:Int, ec:Int, u:String, var magic:String, var magicUrl:String) extends Equipment(n, "MagicWeapon", c, ec, u) {
  
  override lazy val htmlName = {
    "<a href=" + url + ">" + name + "</a>" + "<a href=" + magicUrl + ">(" + magic + ")</a>"
  }

}
case class Ritual(n:String, c:Int, ec:Int, u:String)  extends Equipment(n, "Ritual", c, ec, u)

case class Rules(val typ: String, val value:String, val url: Option[String])

case class PowerWeapon(val name:String, val hit: String, val dmg: String, val stat:String, val defense: String, val hitSummary: String, val dmgSummary: String, val cond: String);

case class Power(val name: String, val url: Option[String], val usage: String, val action: String, val weapons:Map[String, PowerWeapon]) {

  lazy val htmlName = {
    url match {
      case Some(x) => "<a href=\"" + x + "\">" + name + "</a>"
      case None => name
    }
  }

}

case class Skill(var name:String, var score:Int, var url: Option[String]) {
  lazy val htmlName = {
    url match {
      case Some(x) => "<tr><td><a href=\"" + x + "\">" + name + "</a></td><td>" + score + "</td></tr>"
      case None => "<tr><td>" + name + "</td><td>" +score + "</td></tr>"
    }
  }


}

case class Stat(var name: String, var score:Int, var mod:Int) {

  lazy val shortName = name.slice(0,3).toUpperCase

  def check(level:Int) = ((score - 10)/2) + Math.floor(level / 2).toInt

}

case class Character() {

  var statOrder = "Strength" :: "Constitution" :: "Dexterity" :: "Intelligence" :: "Wisdom" :: "Charisma" :: Nil
  var skillOrder = "Athletics" :: "Endurance" :: "Acrobatics" :: "Stealth" :: "Thievery" :: "Arcana" :: "History" :: "Religion" :: "Dungeoneering" :: "Heal" :: "Insight" :: "Nature" :: "Perception" :: "Bluff" :: "Diplomacy" :: "Intimidate" :: "Streetwise" :: Nil

  var statSkills = ("Strength" ->List("Athletics")) :: ("Constitution" ->  List("Endurance")) :: ("Dexterity" -> List("Acrobatics", "Stealth", "Thievery")) :: ("Intelligence" -> List("Arcana", "History", "Religion"))  :: ("Wisdom" ->  List("Dungeoneering", "Heal", "Insight", "Nature", "Perception")):: ("Charisma" -> List("Bluff", "Diplomacy", "Intimidate", "Streetwise")) :: Nil

  var passiveInsight = 0
  var passivePerception = 0

  var power:Map[String, Power] = _
  var skill: Map[String, Skill] = _
  var stat: Map[String, Stat] = _

  var name:String = ""
  var player:String = ""
  var deity: String = ""
  var race:String = ""
  var raceUrl: Option[String] = _
  var clazz:String = ""
  var clazzUrl: Option[String] = _

  var level:Int = 0
  var alignment: String = ""
  var xp: Int = 0

  var height: String = ""
  var weight: String = ""
  var age: String = ""
  var appearance: String = ""
  var companions: String = ""
  var notes : String = ""
  var money: String = ""
  var bank : String = ""
  var size: String = ""
  var traits : String = ""
  var gender: String = ""

  var hp = 0
  def bloddy : Int = (hp / 2.0).floor.toInt
  def sv: Int = {
    var sv = (hp / 4.0).floor.toInt

    if(race equals "Dragonborn")  
      sv += stat("Constitution").mod;
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
  var feats: Map[String, Option[String]] = _
  var racialTrait:  List[String] = _
  var classFeature:List[String] = _
  var equipment:Map[String, Equipment] = _

}

