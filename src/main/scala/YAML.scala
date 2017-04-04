import java.util.Date

import scala.io.Source
import scala.reflect.ClassTag

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
      case line : String => {
        var value = getValueFromSimpleLine(line)
        value = value.substring(countSpacesInBeginning(value))
        if(value.startsWith(">") && isMultiline(value)){
          value = value.substring(1)
          value = removeSpacesInBeginning(stringToLines(value)).mkString
        }else if(value.startsWith("|") && isMultiline(value)){
          value = value.substring(1)
          value = removeSpacesInBeginning(stringToLines(value)).mkString("\n")
        }
        value
      }
      case x => println(x); throw new IllegalArgumentException("Key not found")
    }
  }

  /**
    * Gets a int value from the yaml according to the query.
    * Throws exception when query not found
    */
  def getInt(yaml: Array[Any], query: String): Int = {
    getString(yaml, query).toInt
  }

  /**
    * Gets a float value from the yaml according to the query.
    * Throws exception when query not found
    */
  def getFloat(yaml: Array[Any], query: String): Float = {
    getString(yaml, query).toFloat
  }

  /**
    * Gets a date value from the yaml according to the query.
    * Throws exception when query not found
    */
  def getDate(yaml: Array[Any], query: String, format: java.text.SimpleDateFormat): Date = {
    format.parse(getString(yaml, query))
  }

  /**
    * returns wether or not the given query is an array
    * Throws exception when query not found
    */
  def isArray(yaml: Array[Any], query: String): Boolean = {
    val x = getYAML(yaml, query)
    x match {
      case _: Array[Any] => true
      case _ => false
    }
  }

  /**
    * Get the length of the array at the qiven query.
    * Throws exception when query not found
    */
  def getArrayLength(yaml: Array[Any], query: String): Int = {
    getYAML(yaml, query).length
  }

  /**
    * Returns the array at the given query of the type in the getter.
    * Throws exception when query not found
    */
  def getArray[T:ClassTag](yaml: Array[Any], query: String, getter: (Array[Any], String) => T): Array[T] = {
    var i = -1
    val x: Array[Any] = getYAML(yaml, query)
    var out: Array[T] = Array[T]()
    x.foreach(f => {
      i+=1
      f match {
        case f: Array[Any] => out = out :+ getter(f, s"[$i]")
        case f: T => out = out :+ f
        case _ => throw new IllegalStateException("Could not find it")
      }
    })
    out
  }

  /**
    * Get a yaml datastructure at a sertain node. This is used for key-value fields inside arrays
    * Throws exception when query not found
    */
  def getYAML(yaml: Array[Any], query: String): Array[Any] = {
    findElement(yaml, query) match {
      case x : Array[Any] => x
      case x: String => parseStringToBlockArray(getValueFromSimpleLine(x)).asInstanceOf[Array[Any]]
      case _ => throw new IllegalArgumentException("Key not found")
    }
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
    stringToLines(text).reverse.foreach(x => {
      var line = x
      if(line.startsWith("-"))
        line = line.substring(2)
      
      if (!line.trim().isEmpty && !line.startsWith("#")) {
        if (out.last == "")
          out(out.length - 1) = line
        else
          out(out.length - 1) = line + "\n" + out(out.length - 1)
        if (!line.startsWith(" "))
          out = out :+ ""
      }
    })
    out.filterNot(p => p.trim().isEmpty).reverse
  }

  private def getValueFromSimpleLine(line: String) : String = {
    var text = line
    if(text.startsWith("-")){
      text = text.replaceFirst("-", " ")
    }else{
      text = text.split(":", 2)(1)
    }
    if(!isMultiline(text))
      return text
    val splitted = stringToLines(text)
    removeSpacesInBeginning(splitted).mkString("\n")
  }

  private def removeSpacesInBeginning(splitted : Array[String]): Array[String] = {
    val toRemove = countSpacesInBeginning(splitted(0))
    splitted.map(b => b.substring(toRemove))
  }

  private def countSpacesInBeginning(str : String) : Int = {
    var spaceCount = 0
    for (c <- str.toCharArray)
      if (c == ' ')
        spaceCount += 1
      else
        return spaceCount
    spaceCount
  }

  private def stringToLines(string: String) : Array[String] = {
    string.split("\n|\r").filterNot(p => p.trim().isEmpty)
  }

  private def isMultiline(string : String) : Boolean = {
    string.contains('\n') || string.contains('\r')
  }

  private def findElement(yaml: Array[Any], query: String) : Any = {
    val split : Array[String] = query.split('.')
    if(split.length == 0)
      null
    else if(split.length == 1)
      findElementFromQueryPath(yaml, split)
    else
      findElementAllTypes(yaml, Array(split.head), split.tail)
  }

  private def findElementAllTypes(yaml: Array[Any], qprefix: Array[String], queryelements: Array[String]) : Any = {
    if(queryelements.isEmpty)
      return findElementFromQueryPath(yaml, qprefix)

    val m = findElementAllTypes(yaml, qprefix :+ queryelements.head, queryelements.tail)
    if(m != null)
      return m

    findElementAllTypes(yaml, qprefix.dropRight(1) :+ (qprefix.last + "." + queryelements.head), queryelements.tail)
  }

  private def findElementFromQueryPath(yaml: Array[Any], queryelements: Array[String]) : Any = {
    var curyaml : Any = yaml

    for (i <- queryelements.indices) {
      curyaml match {
        case s: Array[Any] => curyaml = findElementInThisLayer(curyaml.asInstanceOf[Array[Any]], queryelements(i))
        case s: String =>
          if(isMultiline(s))
            curyaml = parseStringToBlockArray(getValueFromSimpleLine(s))
          else if(i == queryelements.length)
            return s
          else
            return null
          curyaml = findElementInThisLayer(curyaml.asInstanceOf[Array[Any]], queryelements(i))
        case _ => return null
      }
    }
    curyaml
  }

  private def findElementInThisLayer(yaml: Array[Any], querypart: String): Any = {
    if(querypart.startsWith("[") && querypart.endsWith("]")){
      val index = querypart.substring(1, querypart.length-1).toInt
      if(index >= yaml.length)
        return null
      yaml(index)
    }else{
      yaml.find(p => {
        val q = s"$querypart\\s*:[\\s\\S]*".r

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

}

