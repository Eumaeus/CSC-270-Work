import better.files._
import java.io.{file => jfile}
import edu.holycross.shot.cite._
import edu.holycross.shot.scm._
import edu.holycross.shot.ohco2._
import scala.annotation.tailrec
import edu.furman.classics.csc270._

:load utilities.sc

val lib: CiteLibrary = loadLibrary("text/arist_politics.cex")

val tr: TextRepository = lib.textRepository.get

val corp: Corpus = tr.corpus

// 2. Split it into manageable chunks. THERE ARE SEVERAL WAYS TO DO THIS!
val corpVec: Vector[Corpus] = hocuspocus.corpusToChapters( corp, drop = 3 )
//val corpVec: Vector[Corpus] = hocuspocus.equalDivs( corp, n = 5 )
//val corpVec: Vector[Corpus] = hocuspocus.equalSize( corp, target = 5000 )
println(" \n ")
println(s"Will write ${corpVec.size} pages…")
println(" \n ")

