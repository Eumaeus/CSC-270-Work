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

/* Lets work with chunks of passages */


// Five passaages at a time?

val chunkSize: Int = 5

/* We want, instead of one Corpus, a Vector[Corpus] 
	 where each Corpus has 'chunkSize' passages
*/

val chunks: Vector[Corpus] = {
	corp.nodes.sliding(chunkSize, chunkSize).toVector.map( nv => Corpus(nv))
}

/* Super Fancy! What if we want each chunk to have approximately
   the same number of characters?
*/

def equalSize( ogCorpus: Corpus, target: Int): Vector[Corpus] = {
	@tailrec def recurseEqualSize( resultCorpusVec: Vector[Corpus], whatsLeft: Corpus, target: Int): Vector[Corpus] = {

		val workingCorpusSize: Int = {
			if (resultsCorpusVec.size == 0) 0
			else {
				resultsCorpus.last.nodes.map(_.text).mkString(" ").size
			}
		}

		/* Three possibilities…
	 		 1. There is only one left in whatsLeft
	 		 2. We've just met the target
	 		 3. We haven't met the target
		*/
		if ( whatsLeft.size == 1) { // Add it and recurse
			val newResultVec: Vector[Corpus] = resultCorpusVec :+ whatsLeft
			newResultVect
		} else if (workingCorpusSize >= target) { // Recurse

		} else {
			val workingCorpus: Corpus = resultsCorpusVec.last	
		}
		
	}
}