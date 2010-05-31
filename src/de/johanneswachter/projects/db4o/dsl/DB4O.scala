package de.johanneswachter.projects.db4o.dsl

import com.db4o.ObjectContainer
import com.db4o.ObjectSet
import com.db4o.query.Query
import com.db4o.query.Constraint
import com.db4o.query.Predicate

import java.util.Comparator

// Wrapper Object to encapsulate all the DSL parts and conversions
object DB4O {
	// Lightweight Wrapper around the DB4O ObjectContainer class.
	case class DSLObjectContainer(val container : ObjectContainer){
		// Use a Native Query with the database.
		def filter[T](pred : Predicate[T])={
			container.query(pred)
		}
		
		// Use a Native Query with the database and specify an ordering.
		def filter[T](pred : Predicate[T], comparator : Comparator[T])={
			container.query(pred, comparator)
		}
		
		// Start building a SODA Query given the specified class.
		def select[T](clazz : Class[T]):DSLQuery[T]={
			DSLQuery(container.query, clazz)
		}
	}
	
	// Conversion from DB4O Container to wrapper type from DSL.
	implicit def objectContainerToDSLObjectContainer(container : ObjectContainer):DSLObjectContainer={
		DSLObjectContainer(container)
	}
	
	// Conversion from Closure to a Predicate used by Native Queries.
	implicit def closureToPredicate[T](f : T => Boolean):Predicate[T]={
		new Predicate[T]{
			def `match`(item : T):Boolean={
				f(item)
			}
		}
	}
	
	// Conversion from Closire to Comparator used to order Native Queries.
	implicit def closureToComparator[T](f : (T,T) => Int):Comparator[T]={
		new Comparator[T]{
			def compare(o1 : T, o2: T):Int={
				f(o1, o2)
			}
		}
	}
	
	// Define different operators
	abstract class Operator
	case object SMALLER extends Operator
	case object SMALLER_EQUAL extends Operator
	case object BIGGER extends Operator
	case object BIGGER_EQUAL extends Operator
	case object EQUALS extends Operator
	case object NOT_EQUALS extends Operator
	case object STARTS_WITH extends Operator
	case object ENDS_WITH extends Operator
	case object LIKE extends Operator
	
	// Define DSL for Constraints
	case class DSLConstraint(symbol : Symbol, var bound : Any = 0, var operator : Operator = EQUALS){
		def is(obj:Any):DSLConstraint={
			operator = EQUALS
			bound = obj
			
			this
		}
		
		def :=(obj:Any):DSLConstraint={
			this is obj
		}
		
		def is_not(obj:Any):DSLConstraint={
			operator = NOT_EQUALS
			bound = obj
			
			this
		}
		
		def !:=(obj:Any):DSLConstraint={
			this is_not obj
		}
		
		def smaller(obj:Any):DSLConstraint={
			operator = SMALLER
			bound = obj
			
			this
		}
		
		def <(obj:Any):DSLConstraint={
			this smaller obj
		}
		
		def smaller_or_equal(obj:Any):DSLConstraint={
			operator = SMALLER_EQUAL
			bound = obj
			
			this
		}
		
		def <=(obj:Any):DSLConstraint={
			this smaller_or_equal obj
		}
		
		def bigger(obj:Any):DSLConstraint={
			operator = BIGGER
			bound = obj
			
			this
		}
		
		def >(obj:Any):DSLConstraint={
			this bigger obj
		}
		
		def bigger_or_equal(obj:Any):DSLConstraint={
			operator = BIGGER_EQUAL
			bound = obj
			
			this
		}
		
		def >=(obj:Any):DSLConstraint={
			this bigger_or_equal obj
		}
			
		def starts_with(obj:String):DSLConstraint={
			operator = STARTS_WITH
			bound = obj
			
			this
		}
		
		def ~|(obj:String):DSLConstraint={
			this starts_with obj
		}
		
		def ends_with(obj:String):DSLConstraint={
			operator = ENDS_WITH
			bound = obj
			
			this
		}
		
		def |~(obj:String):DSLConstraint={
			this ends_with obj
		}
		
		def like(obj:String):DSLConstraint={
			operator = LIKE
			bound = obj
			
			this
		}
		
		def ~(obj:String):DSLConstraint={
			this like obj
		}
	}
	
	// Trait to abstract the constrain to query mapping
	trait QueryUtil {
		def constrain(query : Query, constraint : DSLConstraint):Constraint={
			// Match for different comparations to create the right query.
			constraint.operator match {
				case NOT_EQUALS => query.descend(constraint.symbol.name).constrain(constraint.bound).equal().not()
				case EQUALS => query.descend(constraint.symbol.name).constrain(constraint.bound).equal
				case SMALLER => query.descend(constraint.symbol.name).constrain(constraint.bound).smaller
				case SMALLER_EQUAL => query.descend(constraint.symbol.name).constrain(constraint.bound).smaller().or(query.descend(constraint.symbol.name).constrain(constraint.bound).equal)
				case BIGGER => query.descend(constraint.symbol.name).constrain(constraint.bound).greater
				case BIGGER_EQUAL => query.descend(constraint.symbol.name).constrain(constraint.bound).greater().or(query.descend(constraint.symbol.name).constrain(constraint.bound).equal)
				case STARTS_WITH => query.descend(constraint.symbol.name).constrain(constraint.bound).startsWith(true)
				case ENDS_WITH => query.descend(constraint.symbol.name).constrain(constraint.bound).endsWith(true)
				case LIKE => query.descend(constraint.symbol.name).constrain(constraint.bound).like
				case _ => query.descend(constraint.symbol.name).constrain(constraint.bound).equal
			}
		}
	}
	
	// DSL Wrapper for defining queries
	case class DSLQuery[T](query : Query, clazz : Class[T]) extends QueryUtil{
		// Define a where clause.
		def where(constr : DSLConstraint):ExtendedDSLQuery[T]={
			
			// Return a re-wrapped Extended DSL Query
			ExtendedDSLQuery(query)
		}
		
		// Define the Ordering
		def order(order : DSLOrdering):DSLQuery[T]={
			this
		}
		
		def execute()={
			query.execute
		}
	}
	
	// Extended wrapper
	case class ExtendedDSLQuery[T](query : Query) extends QueryUtil{
		def and(constr : DSLConstraint):ExtendedDSLQuery[T]={
			query.constraints().and(constrain(query, constr))
			
			this
		}
		
		def or(constr : DSLConstraint):ExtendedDSLQuery[T]={
			query.constraints().or(constrain(query, constr))
			
			this			
		}
		
		def order(ordering : DSLOrdering*):ExtendedDSLQuery[T]={
			ordering.foreach{
				o => o.ordering match {
					case ASC => query.descend(o.field.name).orderAscending
					case DESC => query.descend(o.field.name).orderDescending
					case _ => query.orderAscending
				}
			}
			this
		}
		
		def execute()={
			query.execute
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

	// Conversion from symbol to constraint.
	implicit def symbolToDSLConstraint(symbol : Symbol):DSLConstraint={
		DSLConstraint(symbol)
	}

	// Conversion from symbol to ordering.
	implicit def symbolToDSLOrdering(symbol : Symbol):DSLOrdering={
		DSLOrdering(symbol)
	}
	
	// Lightweight Wrapper for ObjectSet
	class EnhancedObjectSet[T](os : ObjectSet[T]) extends Iterator[T]{
		def hasNext : Boolean = os.hasNext
		def next : T = os.next
	}
	
	// Make for comprehensions ppossible
	implicit def objectSetToEnhancedObjectSet[T](os : ObjectSet[T]):EnhancedObjectSet[T] = new EnhancedObjectSet(os)
}