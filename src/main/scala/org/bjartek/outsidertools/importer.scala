package org.bjartek.outsidertools.importer

import org.bjartek.outsidertools.domain._
import scala.xml._
import scala.collection.mutable.HashMap
import scala.collection.immutable.Map

case class CharacterXmlImporter(val character:Elem) {

  def generate(player: String) : Character  = {
    //here we provide some raw data that could be pulled from the XML. Ie what is
    //the skills/stats

    val stats = stat("Strength", List("Athletics")) :: 
      stat("Constitution", List("Endurance")) ::
      stat("Dexterity" , ("Acrobatics":: "Stealth":: "Thievery" :: Nil)) ::
      stat("Intelligence" , ("Arcana":: "History":: "Religion" :: Nil)) ::
      stat("Wisdom" , ("Dungeoneering":: "Heal":: "Insight":: "Nature":: "Perception" :: Nil)) ::
      stat("Charisma" , ("Bluff":: "Diplomacy":: "Intimidate":: "Streetwise" :: Nil)) :: Nil

    val char = Character(stats)
    char.name = findText("Name")
    char.player = player;

    char.equipment = equipment.toList
    char.rituals = rituals.toList

    char.race = findTally("Race")
    char.clazz = findTally("Class")
    char.level = findTally("Level")
    char.alignment =  findTally("Alignment")

    char.xp = findText("Experience Points")

    char.hp = characterStats("Hit Points")
    char.surges = characterStats("Healing Surges")
    char.init = characterStats("Initiative")
    char.speed = characterStats("Speed")
    char.ac = characterStats("AC")
    char.fort = characterStats("Fortitude Defense");
    char.ref = characterStats("Reflex Defense");
    char.will = characterStats("Will Defense");

    char.languages = filterTally("Language")
    char.feats = filterTally("Feat")
    char.raceFeatures = filterTally("Racial Trait")
    char.classFeatures = filterTally("Class Feature")
    char.powers = powerMap
    char
  }

  
  def powerMap = {
     var powers = new HashMap[String, Power]
     for(rule <- rules.filter(_.kind=="Power")) {                    
      if( ! powers.contains(rule.name))  {
        powers += rule.name -> Power(rule.name)
      }
       val power = powers(rule.name)
       rule.field match {
         case "Power Usage" => power.usage = rule.value 
         case "Keywords" => power.keywords = rule.value 
         case "Level" => power.level = rule.value.toInt
         case "Attack" => power.attack = rule.value
         case "Hit" => power.hit = rule.value
         case "Attack Type" => power.attackType = rule.value
         case _ => println(rule.field)
       }
    }
    powers
  }
  
  val characterStats = {
    Map() ++ (for{
      stat <- character \\ "Stat" 
      value <- stat \ "@value"
      alias <- stat \ "alias"
     } yield { 
      alias(0).attribute("name").get.text -> value.text.toInt
    })
  }

  val level = characterStats("Level");
  val mod = level / 2;

  def stat(name: String, skills: List[String]) : Stat = {
    Stat(name, characterStats(name), (characterStats(name) - 10) / 2,  (characterStats(name) - 10) / 2 + mod, generateSkills(skills))
  }

  def generateSkills(skills: List[String]) : List[Skill] = {
    for(skillName <- skills) yield {
      Skill(skillName, characterStats(skillName))
    }
  }

  val loot = for {                               
    loot <- character \ "loot";
    le <- loot \ "@equip-count";        
    lc <- loot \ "@count"              
  } yield {
    val name = loot \\ "@name"
    val typ = loot \\  "@type"
    val lt = typ.toList.head
    val ln = name.toList.head
    lt.text match {
      case "Weapon" => {
        val wpn = Weapon(
          ln.text, 
          searchRules(ln.text, "Weapon", "Item Slot"),  
          searchRules(ln.text, "Weapon", "Damage"), 
          searchRules(ln.text, "Weapon", "Weapon Category"), 
          searchRules(ln.text, "Weapon", "Proficiency Bonus").toInt, 
          searchRules(ln.text, "Weapon", "Group"), 
          lc.text.toInt, 
          le.text.toInt)
        if(typ.contains("Magic Item")) 
          wpn.magic = name(1).text
        wpn
      }
      case "Armor" => { 
        val armor = Armor(
          ln.text, 
          searchRules(ln.text, "Armor", "Item Slot"), 
          lc.text.toInt, 
          le.text.toInt)
        if(typ.contains("Magic Item")) 
          armor.magic = name(1).text
        armor
      }
      case "Ritual" => Ritual(ln.text, lc.text.toInt, le.text.toInt)
      case _ => Equipment(ln.text, lt.text, lc.text.toInt, le.text.toInt)
    }
  }

  val equipment = loot.filter(_.kind != "Ritual")

  val rituals = loot.filter(_.kind == "Ritual").map(_.name)

  lazy val rules =  for{
    field <- character \ "RulesElementField"
    ft <- field \ "@type"
    fn <- field \ "@name"
    ff <- field \ "@field"
  } yield (Rules(fn.text, ft.text, ff.text, field.text.trim))

  lazy val tally = for{
    tally <- character \ "RulesElementTally" \ "RulesElement"; 
    tt <- tally \ "@type" 
    tn <- tally \ "@name"
  } yield (tt.text, tn.text)

  val texts = for{
    ts <- character \ "textstring"
    name <- ts \ "@name"
  } yield  (name.text, ts.text.trim)

  def filterTally(t:String) = {
    (for( element <- tally.filter(x => x._1 == t)) yield (element._2)).toList
  }

  def findTally(t:String) = {
      tally.find(_._1 == t) match {
        case Some(tally) => tally._2
        case None => ""
      }
   }

  def findText(t:String) = {
    texts.find(_._1 == t) match {
        case Some(txt) => txt._2
        case None => ""
      }
  }

  def searchRules(name:String, kind:String, field:String)  = {
     rules.filter(_.name == name).filter(_.kind == kind).filter(_.field == field).toList.head.value
  }
}




