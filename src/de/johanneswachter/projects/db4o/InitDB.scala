package de.johanneswachter.projects.db4o

import com.db4o._
import model._

object InitDB {
  def main(args : Array[String]) : Unit = {
     val db = Db4o.openFile("test-dsl-simpsons.db")
     
     db store Person("Bart","Simpson", 10)
     db store Person("Lisa","Simpson", 8)
     db store Person("Marge","Simpson", 34)
     db store Person("Maggie","Simpson", 2)
     db store Person("Homer","Simpson", 36)
     db store Person("Ned","Flanders", 30)
     db store Person("Rod","Flanders", 6)
     db store Person("Todd","Flanders", 8)
     db store Person("Ralph","Wiggum", 8)
     db store Person("Martin","Prince", 7)
     db store Person("Milhouse","van Houten", 8)
     db store Person("Jimbo","Jones", 12)
     db store Person("Seymour","Skinner", 46)
  }
}
