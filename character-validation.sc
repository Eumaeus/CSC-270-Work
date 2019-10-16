import scala.io.Source

:load utilities.sc

/* Load the file */

val myLines: Vector[String] = loadFile("text/Aristotle_Politics.txt")

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
  println(s"| '${c}'  | '${hex}'        |")
}


/* More Infomative Histogram */

/*
val strVec: Vector[String] = myLines.map( ln => {
  ln.toVector.map( _.toString )
}).flatten

val strHisto: Vector[(String, Int)] = {
  val grouped: Vector[ ( String, Vector[String] ) ] = strVec.groupBy( c => c ).toVector
  grouped.map( g => {
    ( g._1, g._2.size )
  }).sortBy(_._2).reverse
}

val moreInformative: Vector[ (String, Int) ] = {
	strHisto.map( sh => {
		sh._1 match {
			case " " => ( "space", sh._2)
			case "\t" => ( "tab", sh._2)
			case _ => sh
		}
	})
}

for ( c <- moreInformative ) println(s"'${c._1}'\t${c._2}")
*/