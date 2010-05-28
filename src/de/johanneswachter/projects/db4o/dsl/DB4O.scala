package de.johanneswachter.projects.db4o.dsl

import com.db4o.ObjectContainer
import com.db4o.query.Query
import com.db4o.query.Predicate

object DB4O {
	// Define wrapper classes for DB4O items
	case class DSLObjectContainer(val container : ObjectContainer){
		def filter[T](pred : Predicate[T])={
			
		}
		
		def select[T](clazz : Class[T]):DSLQuery[T]={
			DSLQuery(clazz)
		}
	}
	
	// Conversion from DB4O Container to wrapper type from DSL
	implicit def objectContainerToDSLObjectContainer(container : ObjectContainer):DSLObjectContainer={
		DSLObjectContainer(container)
	}
	
	implicit def closureToPredicate[T](f : T => Boolean):Predicate[T]={
		new Predicate[T]{
			def `match`(item : T):Boolean={
				f(item)
			}
		}
	}
	
	// Define DSL for Constraints
	case class DSLConstraint(symbol : Symbol, var bound : Any = 0){
		def is(obj:Any):DSLConstraint={
			this
		}
		
		def ===(obj:Any):DSLConstraint={
			this is obj
		}
		
		def smaller(obj:Any):DSLConstraint={
			this is obj
		}
		
		def <(obj:Any):DSLConstraint={
			this is obj
		}
		
		def smaller_or_equal(obj:Any):DSLConstraint={
			this is obj
		}
		
		def <=(obj:Any):DSLConstraint={
			this is obj
		}
		
		def bigger(obj:Any):DSLConstraint={
			this is obj
		}
		
		def >(obj:Any):DSLConstraint={
			this is obj
		}
		
		def bigger_or_equal(obj:Any):DSLConstraint={
			this is obj
		}
		
		def >=(obj:Any):DSLConstraint={
			this is obj
		}
			
		def starts_with(obj:String):DSLConstraint={
			this is obj
		}
		
		def ~|(obj:String):DSLConstraint={
			this is obj
		}
		
		def ends_with(obj:String):DSLConstraint={
			this is obj
		}
		
		def |~(obj:String):DSLConstraint={
			this is obj
		}
		
		def like(obj:String):DSLConstraint={
			this is obj
		}
		
		def ~(obj:String):DSLConstraint={
			this is obj
		}
	}
	
	// Define DSL for Queries
	case class DSLQuery[T](clazz : Class[T]){
		def where(constr : DSLConstraint):ExtendedDSLQuery[T]={
			ExtendedDSLQuery(this)
		}
		
		def order(order : DSLOrdering):DSLQuery[T]={
			this
		}
	}
	
	case class ExtendedDSLQuery[T](query : DSLQuery[T]){
		def and(constr : DSLConstraint):ExtendedDSLQuery[T]={
			query where constr
		}
		
		def or(constr : DSLConstraint):ExtendedDSLQuery[T]={
				query where constr
		}
		
		def order(ordering : DSLOrdering*):ExtendedDSLQuery[T]={
			ordering.foreach{
				o => query order o
			}
			this
		}
	}
	
	// Define DSL for Ordering
	abstract class DSLOrder
	case object ASC extends DSLOrder
	case object DESC extends DSLOrder

	// Ordering
	case class DSLOrdering(field : Symbol, var ordering : DSLOrder = ASC){
		def by(order : DSLOrder):DSLOrdering={
			ordering = order
			this
		}
	}

	// Define implicit conversion for symbol to use DSLs
	implicit def symbolToDSLConstraint(symbol : Symbol):DSLConstraint={
		DSLConstraint(symbol)
	}

	// convert symbols to DSLOrdering
	implicit def symbolToDSLOrdering(symbol : Symbol):DSLOrdering={
		DSLOrdering(symbol)
	}
	
	// def global factory functions
	def select[T](clazz : Class[T]):DSLQuery[T]={
		DSLQuery(clazz)
	}
}