import scala.io.Source
import edu.holycross.shot.cite._
import edu.holycross.shot.scm._
import edu.holycross.shot.ohco2._
import java.io._
import scala.annotation.tailrec
import java.util.Calendar

:load utilities.sc

val lib: CiteLibrary = loadLibrary("text/myText.cex")

val tr: TextRepository = lib.textRepository.get

val cat: Catalog = tr.catalog

val corp: Corpus = tr.corpus

/* Lets work chapter-by-chapter */

// There are a couple of ways of doing this; which is faster?

// Way 1, mostly pure Scala
val timeStart1 = Calendar.getInstance().getTimeInMillis()
val chapterCorpora1: Vector[Corpus] = {
	val chapterUrns: Vector[CtsUrn] = {
		corp.urns.map( _.collapsePassageTo(1) ).distinct
	}
	chapterUrns.map( cu => {
	 		Corpus( corp.nodes.filter( _.urn.collapsePassageTo(1) == cu ))	
	})
}
val timeEnd1 = Calendar.getInstance().getTimeInMillis()
println( s"chapterCorpora1 in ${timeEnd1 - timeStart1} milliseconds." )

// Way 2, using stuff from the OHCO2 library
val timeStart2 = Calendar.getInstance().getTimeInMillis()
val chapterCorpora2: Vector[Corpus] = {
	corp.chunkByCitation(1)
}
val timeEnd2 = Calendar.getInstance().getTimeInMillis()
println( s"chapterCorpora2 in ${timeEnd2 - timeStart2} milliseconds." )

println( s"( chapterCorpora1 == chapterCorpora2 ) = ${chapterCorpora1 == chapterCorpora2}.")

// type, e.g. 'showMe(chapterCorpora1(0))' to list results

