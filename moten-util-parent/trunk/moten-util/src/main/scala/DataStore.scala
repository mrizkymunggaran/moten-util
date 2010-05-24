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
         = y.filter(a => !x.find(b => (t(x) contains t(b)) && a.time>b.time).isEmpty)
     
     def g(x:Set[TimedIdentifier], y:Set[TimedIdentifier]) = gamma(x,y) ++ mu(x,y)
     
     def pm(z: Set[Set[TimedIdentifier]], r:Set[TimedIdentifier], x:Set[TimedIdentifier]) = {
       if (z.size==0) z
       else  z
     }
}



