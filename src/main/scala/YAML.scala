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
    throw new NotImplementedError()
  }

  /**
    * Gets a string value from the yaml according to the query.
    * Throws exception when query not found
    */
  def getString(yaml: Array[Any], query: String): String = {
    throw new NotImplementedError()
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

  private def parseStringToBlockArray(text: String): Array[String] = {
    var out : Array[String] = Array("")
    text.split('\n').reverse.foreach(line => {
      if(out.last == "")
        out(out.length-1) = line
      else
        out(out.length-1) = line  + "\n" + out(out.length-1)
      if(!line.startsWith(" "))
        out = out :+ ""
    })
    out.reverse
  }
}

