package org.bjartek.outsidertools.importer

import org.bjartek.outsidertools.domain._
import scala.xml._
import scala.collection.immutable.Map

case class CharacterXmlImporter(val char:Elem) {

  def findPower(name: String) = tally.filter(_.value == name).toList.head

  lazy val stats  = {
    Map() ++ (for{
        stat <- sheet \\ "Stat" 
        value <- stat \ "@value"
        name <- stat \ "@name"
      } yield { 
        name.text.toLowerCase -> value.text.toInt
      })
  }



  lazy val tally = for{
    tally <- sheet \ "RulesElementTally" \ "RulesElement" 
    tt <- tally \ "@type" 
    tn <- tally \ "@name"
  } yield {
    val url =  tally.attribute("url") match { case Some(node:Node) => Some(node.text) case None => None}
    Rules(tt.text, tn.text, url);
  }


  def filterTally(typ:String) = tally.filter(_.typ == typ).map(_.value)


    val sheet = char \ "CharacterSheet";


  lazy val details = sheet \ "Details"
  def detail(key:String) = (details \ key).text.trim
  lazy val race = tally.filter(_.typ=="Race").toList.head
  lazy val claz = tally.filter(_.typ=="Class").toList.head


  def generate() : Character  = {

    val c = Character();
    c.name = detail("name")
    c.level = detail("Level").toInt
    c.player = detail("Player")
    c.deity = detail("Deity")
    c.alignment = filterTally("Alignment").toList match { case List(element) => element; case _ => "" }
    c.size = filterTally("Size").toList.head
    c.languages = filterTally("Language").toList
    c.race = race.value
    c.raceUrl = race.url
    c.height = detail("Height")
    c.weight = detail("Weight")
    c.gender = detail("Gender")
    c.age = detail("Age")
    c.money = detail("CarriedMoney")
    c.bank = detail("StoredMoney") 
    c.traits = detail("Traits") 

    if(detail("Companions") != "") {
        c.companions = detail("Companions")
      }

    if(detail("Appearance") != "") {
        c.appearance = detail("Appearance")
      }


    if(detail("Experience") != "")  {
      c.xp = detail("Experience").toInt
     }

    c.hp = stats("hit points").toInt
    c.surges = stats("healing surges").toInt
    c.init = stats("initiative").toInt
    c.speed = stats("speed").toInt
    c.ac = stats("ac").toInt
    c.fort = stats("fortitude defense").toInt
    c.ref = stats("reflex defense").toInt
    c.will = stats("will defense").toInt
    c.clazz = claz.value
    c.clazzUrl = claz.url
    c.passivePerception = stats("passive perception")
    c.passiveInsight = stats("passive insight")
    c.feats = Map() ++ (tally.filter(_.typ == "Feat").map(kv => kv.value -> kv.url));
    c.classFeature = filterTally("Class Feature").toList
    c.racialTrait = filterTally("Racial Trait").toList
    c.skill = Map() ++ (tally.filter(_.typ == "Skill").map(kv => kv.value -> Skill(kv.value, stats(kv.value.toLowerCase), kv.url)))
    c.stat = Map() ++ (("Strength" :: "Constitution" :: "Dexterity" :: "Intelligence" :: "Wisdom" :: "Charisma" :: Nil).map(name => name -> Stat(name, stats(name.toLowerCase), stats(name.toLowerCase + " modifier"))))

    c.equipment = Map() ++ (for {                               
      loot <- sheet \\ "loot";
      le <- loot \ "@equip-count";        
      lc <- loot \ "@count" if lc.text.toInt != 0 
    } yield {
      val name = loot \\ "@name"
      val typ = loot \\  "@type"
      val url = loot \\ "@url"

      val lu = url.toList.head
      val lt = typ.toList.head
      val ln = name.toList.head

      lt.text match {
        case "Weapon" => {
          if(typ.contains("Magic Item")) {
            ln.text -> MagicWeapon(ln.text, lc.text.toInt, le.text.toInt, lu.text, name(1).text, url(1).text)
          } else {
            ln.text -> Weapon(ln.text, lc.text.toInt, le.text.toInt, lu.text)
          }
        }
        case "Armor" => { 
          if(typ.contains("Magic Item")) {
            ln.text -> MagicArmour(name(1).text, url(1).text, ln.text, lc.text.toInt, le.text.toInt, lu.text);
          }else {
            ln.text -> Armor(ln.text, 
              lc.text.toInt, 
              le.text.toInt, 
              lu.text)
          }
        }
        case "Ritual" => ln.text -> Ritual(ln.text, lc.text.toInt, le.text.toInt, lu.text)
        case _ => ln.text -> Equipment(ln.text, lt.text, lc.text.toInt, le.text.toInt, lu.text)
      }
    })


    c.power =  Map() ++ (for{
      power <- sheet \\ "Power"
      name <- power \ "@name"
    } yield {
      val specifics = Map() ++ (for{
          specific <- power \ "specific"
          sn <- specific \ "@name"
        }  yield { 
          sn.text.trim -> specific.text.trim
        });

           val weapons = Map() ++ (for{
          weapon <- power \ "Weapon"
          wn <- weapon \ "@name"
          hit <- weapon \ "AttackBonus"
          dmg <- weapon \ "Damage"
          stat <- weapon \ "AttackStat"
          defense <- weapon \ "Defense"
          hitSummary <- weapon \ "HitComponents"
          dmgSummary <- weapon \ "DamageComponents"
        } yield {

          val cond = weapon \ "Conditions"
          wn.text -> PowerWeapon(wn.text.trim, hit.text.trim, dmg.text.trim, stat.text.trim, defense.text.trim, hitSummary.text.trim, dmgSummary.text.trim, cond.text.trim);
        }) ;
      
      name.text -> Power(name.text, findPower(name.text).url, specifics("Power Usage"), specifics("Action Type"),  weapons)
      
    })
    c
  }
}
