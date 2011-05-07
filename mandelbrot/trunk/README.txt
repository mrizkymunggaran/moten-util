To run the viewer: 
  mvn clean package exec:java
  
To create the movie frames:
 mvn clean package exec:java -Dname=GeneratorMain
 
To create the movie:
  ./make-movie.sh