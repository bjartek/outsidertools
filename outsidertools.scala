import scala.util.matching.Regex
import scala.xml._
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
    char.name = filterText("Name")(0)
    char.player = player;
    char.race = firstTallyOrEmpty("Race")

    char.equipment = equipment.toList
    char.rituals = rituals.toList
    char.clazz = firstTallyOrEmpty("Class")
    char.level = filterTally("Level").toList.last.toString
    char.alignment =  firstTallyOrEmpty("Alignment") 
    char.xp = firstOrEmpty( filterText("Experience Points"))
    char.hp = characterStats("Hit Points")

    char.surges = characterStats("Healing Surges")
    char.init = characterStats("Initiative")
    char.speed = characterStats("Speed")
    char.ac = characterStats("AC")
    char.fort = characterStats("Fortitude Defense");
    char.ref = characterStats("Reflex Defense");
    char.will = characterStats("Will Defense");

    char.languages = filterTally("Language").toList
    char.feats = filterTally("Feat").toList
    char.raceFeatures = filterTally("Racial Trait").toList
    char.classFeatures = filterTally("Class Feature").toList
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
    var stats = new HashMap[String, Int];
    for(stat <- character \\ "Stat"; value <- stat.attribute("value").get; alias <- stat \ "alias") yield { 
      stats +=  alias(0).attribute("name").get.text -> value.text.toInt
    }
    stats
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
    var name = loot \\ "@name"
    var typ = loot \\  "@type"
    var lt = typ.toList.head
    var ln = name.toList.head
    lt.text match {
      case "Weapon" => {
        val wpn = Weapon(ln.text, searchRules(ln.text, "Weapon", "Item Slot"),  searchRules(ln.text, "Weapon", "Damage"), searchRules(ln.text, "Weapon", "Weapon Category"), searchRules(ln.text, "Weapon", "Proficiency Bonus").toInt, searchRules(ln.text, "Weapon", "Group"), lc.text.toInt, le.text.toInt)
          if(typ.contains("Magic Item")) wpn.magic = name(1).text
        wpn
      }
      case "Armor" => { 
        val armor = Armor(ln.text, searchRules(ln.text, "Armor", "Item Slot"), lc.text.toInt, le.text.toInt)
          if(typ.contains("Magic Item")) armor.magic = name(1).text
        armor
      }
      case "Ritual" => Ritual(ln.text, lc.text.toInt, le.text.toInt)
      case _ => Equipment(ln.text, lt.text, lc.text.toInt, le.text.toInt)
    }
  }

  val equipment = loot.filter(_.kind != "Ritual")

  val rituals = loot.filter(_.kind == "Ritual").map(_.name)

  lazy val rules =  for(field <- character \ "RulesElementField"; ft <- field.attribute("type").get; fn <- field.attribute("name").get; ff <- field.attribute("field").get) yield (Rules(fn.text, ft.text, ff.text, field.text.trim))

  lazy val tally = for(tally <- character \ "RulesElementTally" \ "RulesElement"; tt <- tally.attribute("type").get; tn <- tally.attribute("name").get) yield (tt.text, tn.text)

  val texts = for(ts <- character \ "textstring"; name <- ts.attribute("name").get) yield (name.text, ts.text.trim)

  def firstTallyOrEmpty(t:String) = {
    filterTally(t) match {
      case head :: Nil => head
      case _ => ""
    }
  }

  def firstOrEmpty(seq:Seq[String]) : String = {
    seq match {
      case head :: Nil => head
      case _ => ""
    }
  }

  def filterTally(t:String) = {
    for( element <- tally.filter(x => x._1 == t)) yield (element._2)
  }

  def filterText(t:String) = {
    for( element <- texts.filter(x => x._1 == t)) yield (element._2)
  }

  def searchRules(name:String, kind:String, field:String)  = {
     rules.filter(_.name == name).filter(_.kind == kind).filter(_.field == field).toList.head.value
  }
}


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

var charName = "Jax.dnd4e"
//var charName = args(0)
val importer = CharacterXmlImporter(XML.load(charName))
val char = importer.generate("Bjartek")

RpolTextRenderer(char);

