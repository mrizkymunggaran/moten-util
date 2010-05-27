package moten.david.matchstack.scala

class DataStore {
      
     trait IdentifierType {
         def strength: Double
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
         = y.filter(a => !x.exists(b => (t(x) contains t(b)) && a.time>b.time))
     
     def g(x:Set[TimedIdentifier], y:Set[TimedIdentifier]) = gamma(x,y) ++ mu(x,y)
     
     def pm(z: Set[Set[TimedIdentifier]], r:Set[TimedIdentifier], x:Set[TimedIdentifier]) = {
       if (z.isEmpty) 
         z
       else  {
         if ( !z.exists(a => !x.exists( t(a) contains t(_)))) 
           z
         else { 
          //find the maximum strength identifier that intersects with R id
           val intersects = z.filter(a => id(a).exists(id(x) contains _))
           def greater(a:TimedIdentifier, b:TimedIdentifier) = 
             if (a!=null && a.identifier.identifierType.strength > b.identifier.identifierType.strength) 
             	a 
             else  
                b
           def max(c:Collection[TimedIdentifier]):TimedIdentifier = 
        	   c.foldLeft(null.asInstanceOf[TimedIdentifier])((a,b) => greater(a,b))  
           z
         }
       }
     }
}



