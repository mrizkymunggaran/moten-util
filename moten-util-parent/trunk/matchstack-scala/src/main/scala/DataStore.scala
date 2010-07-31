package moten.david.matchstack.scala

class DataStore {
      
     trait IdentifierType {
         def strength: Double
         def rank: Double 
     }
     
     trait Identifier {
       def identifierType: IdentifierType
     }
     
     trait Timed {
       def time:  Long
     }
     
     trait TimedIdentifier extends Timed{
       def identifier: Identifier 
     }

     def t(x:Set[TimedIdentifier]) =  x.map(_.identifier.identifierType)
     
     def t(x:TimedIdentifier) = x.identifier.identifierType
     
     def id(x:Set[TimedIdentifier]) = x.map(_.identifier) 
     
     def gamma(x:Set[TimedIdentifier], y: Set[TimedIdentifier]) = y.filter(t(x) contains t(_))
     
     def mu(x:Set[TimedIdentifier], y:Set[TimedIdentifier])   
         = y.filter(a => !x.exists(b => x.exists(t(_)==t(b)) && a.time>b.time))
     
     def g(x:Set[TimedIdentifier], y:Set[TimedIdentifier]) = gamma(x,y) ++ mu(x,y)
     
     def isValid(x: Set[TimedIdentifier]) = x.size == t(x).size
     
     def greater(a:TimedIdentifier, b:TimedIdentifier) = 
             if (a!=null && a.identifier.identifierType.strength > b.identifier.identifierType.strength) 
                a 
             else   
                b 
     
     //in scala 2.8 this one can be done using .max on a collection
     def max(c:Collection[TimedIdentifier]):TimedIdentifier = 
               c.foldLeft(null.asInstanceOf[TimedIdentifier])((a,b) => greater(a,b))
               
     object IdentifierTypeStrength extends Ordering[IdentifierType] { 
         def compare(a: IdentifierType, b: IdentifierType) 
             = a.strength compare b.strength}
     
     def pm(z: Set[Set[TimedIdentifier]], x:Set[TimedIdentifier]) = {
       if (z.isEmpty) 
         z
       else  {
         if ( !z.exists(a => !x.exists( id(a) contains _.identifier))) 
           z
         else { 
           //val zid = z.map(id(_).intersect(id(x)))
           
           
         }
       }
     }
}



