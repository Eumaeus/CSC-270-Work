import scala.io.Source

:load utilities.sc

val myLines: Vector[String] = loadFile("text/Aristotle_Politics.txt")


val oneBigString: String = myLines.mkString(" ")


val myChars: Vector[Char] = oneBigString.toVector

val myBetterChars: Vector[String] = myChars.map( _.toString )

val noSpaces: Vector[String] = myBetterChars.filter( _ != " ")

val groupedChars: Vector[ (String, Vector[String] ) ] = {
	noSpaces.groupBy( c => c ).toVector
}

val charHistoUnsorted: Vector[ (String, Int) ] = {
	groupedChars.map( c => {
		( c._1, c._2.size )
	})
}

val charHisto: Vector[ (String, Int) ] = charHistoUnsorted.sortBy( _._2 ).reverse

/*
val crazyHisto: Vector[ ( String, Int ) ] = {
	myLines.mkString.filter( _ != ' ').groupBy( c => c).toVector.map( t => (t._1, t._2.size ) ).sortBy( _._2 ).reverse.map( t => ( t._1.toString, t._2 ) )
}
*/

// charHisto == crazyHisto

for ( h <- charHisto ) println( s"${h._1}\t${h._2}" )




