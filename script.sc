import scala.io.Source

:load utilities.sc

val myLines: Vector[String] = loadFile("text/Aristotle_Politics.txt")

/* Character Histogram */

val charVec: Vector[Char] = myLines.map( ln => {
  ln.toVector
}).flatten

val charHisto: Vector[(Char, Int)] = {
  val grouped: Vector[ ( Char, Vector[Char] ) ] = charVec.groupBy( c => c ).toVector
  grouped.map( g => {
    ( g._1, g._2.size )
  }).sortBy(_._2).reverse
}

val justChars: Vector[Char] = charVec.distinct

for ( c <- charHisto ) println(s"'${c._1}'\t${c._2}")
