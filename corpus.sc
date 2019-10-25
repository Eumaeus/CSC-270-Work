import scala.io.Source
import edu.holycross.shot.cite._
import edu.holycross.shot.scm._
import edu.holycross.shot.ohco2._
import java.io._
import scala.annotation.tailrec

:load utilities.sc

val lib: CiteLibrary = loadLibrary("text/myText.cex")

val tr: TextRepository = lib.textRepository.get

val cat: Catalog = tr.catalog

val corp: Corpus = tr.corpus

/* CATALOG GAMES */

println(s"\nThings you can do with a catalog.\n")

cat.texts

println()

cat.versions

println()

cat.exemplars

println()

cat.works

println()

// The catalog can give you info about a text, if you know its URN

val versionUrn: CtsUrn = cat.versions.head // get the first version in the Set

cat.groupName(versionUrn)

cat.workTitle(versionUrn)

cat.versionLabel(versionUrn)

// Or, you can query each CatalogEntry specifically…

val catEntry: CatalogEntry = cat.texts.head

// get a description of the citation scheme for this text…

catEntry.citationScheme

/* Just as you created a CiteLibrary out of CEX file, you can
	 go the other way… here we'll create a CEX entry for a CatalogEntry,
	 specifying "#" at the delimiter.
*/

val ceCexString: String = catEntry.cex("#")

/* CORPUS GAMES */
