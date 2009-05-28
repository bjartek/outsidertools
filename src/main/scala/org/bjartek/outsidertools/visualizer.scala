package org.bjartek.outsidertools
import org.bjartek.outsidertools.domain._
import org.bjartek.outsidertools.render._
import org.bjartek.outsidertools.importer._
import scala.xml._
import java.io._

object CharacterVisualizer{
  def main(args: Array[String]) {
      var charName = args(0)
      val importer = CharacterXmlImporter(XML.load(charName))
      val char = importer.generate()
      val renderer = new RpolTextRenderer(char);

     if(args.length == 2) {
      val out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(args(1))),"UTF8"));

      out.write(renderer.generate.mkString("\n"))
      out.close
      "done"
    }else {
     renderer.generate.foreach(println);
    }
  }
}

