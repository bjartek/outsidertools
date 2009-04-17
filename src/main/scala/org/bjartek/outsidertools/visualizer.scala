package org.bjartek.outsidertools
import org.bjartek.outsidertools.domain._
import org.bjartek.outsidertools.render._
import org.bjartek.outsidertools.importer._
import scala.xml._

object CharacterVisualizer{
  def main(args: Array[String]) {
    var charName = args(0)
      val importer = CharacterXmlImporter(XML.load(charName))
      val char = importer.generate("Bjartek")
      RpolTextRenderer(char);
  }
}

