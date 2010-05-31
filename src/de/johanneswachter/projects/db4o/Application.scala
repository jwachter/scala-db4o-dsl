package de.johanneswachter.projects.db4o

import com.db4o._
import dsl.DB4O._
import model._

object Application {
  def main(args : Array[String]) : Unit = {
     val db = Db4o.openFile("test-dsl-simpsons.db")
	 db filter ((s : String) => true)
	 
	 val results_1 = db select Person.getClass where('lastName ~ "on") and('firstName ~| "B") execute
	 
		for(v <- results_1){
			println(v)
		}
		 
  }
}
