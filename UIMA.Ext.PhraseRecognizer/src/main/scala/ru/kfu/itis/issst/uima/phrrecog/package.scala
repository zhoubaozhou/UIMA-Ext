/**
 *
 */
package ru.kfu.itis.issst.uima

/**
 * @author Rinat Gareev (Kazan Federal University)
 *
 */
import ru.kfu.itis.issst.uima.phrrecog.cas.Phrase
import org.opencorpora.cas.Word
import org.uimafit.util.FSCollectionFactory
import scala.collection.JavaConversions._
import ru.kfu.itis.issst.uima.phrrecog.cas.NounPhrase
import scala.collection.immutable.SortedSet
import scala.math.Ordering
import ru.kfu.itis.cll.uima.cas.AnnotationOffsetComparator
import scala.collection.mutable.ListBuffer
import ru.kfu.itis.cll.uima.cas.FSUtils

package object phrrecog {

  val PhraseTypeNP = "NP"
  val PhraseTypeVP = "VP"

  val annOffsetComp = Ordering.comparatorToOrdering(
    AnnotationOffsetComparator.instance(classOf[Word]))

  /**
   * Returns the first word of NP.
   * If ignoreAux is true then leading preposition or particle is ignored.
   */
  def getFirstWord(np: NounPhrase, ignoreAux: Boolean): Word = {
    val candidates = ListBuffer.empty[Word]
    candidates += np.getHead
    if (!ignoreAux && np.getPreposition != null) candidates += np.getPreposition
    if (!ignoreAux && np.getParticle != null) candidates += np.getParticle
    np.getDependents() match {
      case null =>
      case depsFS if depsFS.size == 0 =>
      case depsFS => candidates += FSCollectionFactory.create(depsFS, classOf[Word]).head
    }
    candidates.minBy(_.getBegin)
  }

  /**
   * Returns the last word of NP.
   * If ignoreAux is true then leading preposition or particle is ignored.
   */
  def getLastWord(np: NounPhrase, ignoreAux: Boolean): Word = {
    val candidates = ListBuffer.empty[Word]
    candidates += np.getHead
    if (!ignoreAux && np.getPreposition != null) candidates += np.getPreposition
    if (!ignoreAux && np.getParticle != null) candidates += np.getParticle
    np.getDependents() match {
      case null =>
      case depsFS if depsFS.size == 0 =>
      case depsFS => candidates += FSCollectionFactory.create(depsFS, classOf[Word]).last
    }
    candidates.maxBy(_.getBegin)
  }

  def getWords(np: NounPhrase, ignoreAux: Boolean): SortedSet[Word] = {
    var result = SortedSet.empty[Word](annOffsetComp) + np.getHead
    val depsFS = np.getDependents
    if (depsFS != null && depsFS.size() > 0) result ++= FSCollectionFactory.create(depsFS, classOf[Word])
    if (!ignoreAux && np.getPreposition != null) result += np.getPreposition
    if (!ignoreAux && np.getParticle != null) result += np.getParticle
    result
  }

  def getOffsets(np: NounPhrase): (Int, Int) = (getFirstWord(np, false).getBegin(), getLastWord(np, false).getEnd())

  def containWord(np: NounPhrase, w: Word): Boolean =
    np.getHead == w || np.getParticle == w || np.getPreposition == w ||
      FSUtils.contain(np.getDependents, w)
}