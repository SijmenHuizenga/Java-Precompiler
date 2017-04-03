import java.util.Date

import scala.io.Source

/**
  * Created by Sijmen on 29-3-2017.
  */

object YAML {
  /**
    * Given a filename, returns a array that contains the data.
    * Data can be extracted from this array by the methods below.
    * Throws exception when file not found
    */
  def loadYaml(filename: String): Array[Any] = {
    val source = Source.fromResource(filename)
    parseStringToBlockArray(source.mkString).asInstanceOf[Array[Any]]
  }

  /**
    * Returns wether or not the field at the query exists.
    */
  def hasField(yaml: Array[Any], query: String): Boolean = {
    findElement(yaml, query) match {
      case null => false
      case _ => true
    }
  }

  /**
    * Gets a string value from the yaml according to the query.
    * Throws exception when query not found
    */
  def getString(yaml: Array[Any], query: String): String = {
    findElement(yaml, query) match {
      case line : String => getValueFromSimpleLine(line)
      case _ => throw new IllegalArgumentException("Key not found")
    }
  }

  /**
    * Gets a int value from the yaml according to the query.
    * Throws exception when query not found
    */
  def getInt(yaml: Array[Any], query: String): Int = {
    throw new NotImplementedError()
  }

  /**
    * Gets a float value from the yaml according to the query.
    * Throws exception when query not found
    */
  def getFloat(yaml: Array[Any], query: String): Float = {
    throw new NotImplementedError()
  }

  /**
    * Gets a date value from the yaml according to the query.
    * Throws exception when query not found
    */
  def getDate(yaml: Array[Any], query: String): Date = {
    throw new NotImplementedError()
  }

  /**
    * returns wether or not the given query is an array
    * Throws exception when query not found
    */
  def isArray(yaml: Array[Any], query: String): Boolean = {
    throw new NotImplementedError()
  }

  /**
    * Get the length of the array at the qiven query.
    * Throws exception when query not found
    */
  def getArrayLength(yaml: Array[Any], query: String): Int = {
    throw new NotImplementedError()
  }

  /**
    * Returns the array at the given query of the type in the getter.
    * Throws exception when query not found
    */
  def getArray[T](yaml: Array[Any], query: String, getter: (Array[Any], String) => T): Array[T] = {
    throw new NotImplementedError()
  }

  /**
    * Get a yaml datastructure at a sertain node. This is used for key-value fields inside arrays
    * Throws exception when query not found
    */
  def getYAML(yaml: Array[Any], query: String): Array[Any] = {
    throw new NotImplementedError()
  }

  /**
    * given input gives a array with all top level elements
    * as strings. example:
    *   a: b
    *   b: c
    *   c:
    *     d: e
    *     f: g
    * ["a: b", "b: c", "c:\n  d:e\n  f: g"]
    */
  private def parseStringToBlockArray(text: String): Array[String] = {
    var out : Array[String] = Array("")
    stringToLines(text).reverse.foreach(line => {
      if (!line.startsWith("#")) {
        if (out.last == "")
          out(out.length - 1) = line
        else
          out(out.length - 1) = line + "\n" + out(out.length - 1)
        if (!line.startsWith(" "))
          out = out :+ ""
      }
    })
    out.reverse
  }

  private def getValueFromSimpleLine(line: String) : String = {
    line.split(':')(1)
  }

  private def stringToLines(string: String) : Array[String] = {
    string.split("\n|\r")
  }

  private def findElement(yaml: Array[Any], query: String) : Any = {
    val split : Array[String] = query.split('.')
    //todo: add options #next
    findElement(yaml, split)
  }

  private def findElement(yaml: Array[Any], queryelements: Array[String]) : Any = {
    var curyaml : Any = yaml

    for (i <- 0 until (queryelements.length-1)) {
      curyaml match {
        case s : Array[Any] => {
          curyaml = findElementInThisLayer(curyaml.asInstanceOf[Array[Any]], queryelements(i))
        }
        case _ => return null
      }
    }
    if(!curyaml.isInstanceOf[Array[Any]])
      return false
    findElementInThisLayer(curyaml.asInstanceOf[Array[Any]], queryelements.last)
  }

  private def findElementInThisLayer(yaml: Array[Any], querypart: String): Any = {
    yaml.find(p => {
      val q = s"$querypart\\s*:.*".r

      val x = p match {
        case unparsedstring: String => {
          q.unapplySeq(unparsedstring).isDefined
        }
        case parsed: Array[Any] =>{
          q.unapplySeq(parsed(0).asInstanceOf[String]).isDefined
        }
        case _ => false
      }
      x
    }).orNull
  }

}

