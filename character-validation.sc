import scala.io.Source

:load utilities.sc

/* Load the file */

val myLines: Vector[String] = loadFile("text/Aristotle_Politics.txt")
//val myLines: Vector[String] = loadFile("text/char_test.txt")

/* Generate a Character Histogram */

val charVec: Vector[Char] = myLines.map( ln => {
  ln.toVector
}).flatten

val charHisto: Vector[(Char, Int)] = {
  val grouped: Vector[ ( Char, Vector[Char] ) ] = charVec.groupBy( c => c ).toVector
  grouped.map( g => {
    ( g._1, g._2.size )
  }).sortBy(_._2).reverse
}

/* Get a Sorted Vector of Distinct Characters */

val justChars: Vector[Char] = charVec.distinct

val sortedChars = justChars.sortBy( c => c)

/* Generate a Character Report */

val tableHead: String = "\n\n| Char | Unicode Hex |\n|------|-------------|"
println(tableHead)

for ( c <- sortedChars ) {
  val hex: String = c.toHexString
  println(s"| ` ${c}`  | `${hex}`        |")
}


