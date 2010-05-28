package de.johanneswachter.projects.db4o

import com.db4o._
import dsl.DB4O._
import model._

object Application {
  def main(args : Array[String]) : Unit = {
     val db = Db4o.openFile("test-dsl.db");
	  
	 db filter ((s : String) => true)
	 
	 db select Person.getClass where('name is "Something") and('firstName is "Another") or('lastname is "Foobar") order('name by ASC)
	 
	 db select Person.getClass where('name ~ "Test") and('lastName <= 10) or('age === 10) order('name by ASC, 'foo by DESC)
	 
	 'symbol by ASC
	 'name is 10
  }
}
