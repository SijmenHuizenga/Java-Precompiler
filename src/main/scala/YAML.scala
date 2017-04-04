import java.util.Date

import scala.io.Source

/**
  * Created by Sijmen on 29-3-2017.
  */

object YAML {
  
  val MAXDEPTH = 100

  type YAML = Array[Array[String]]

  /**
    * Given a filename, returns a array that contains the data.
    * Data can be extracted from this array by the methods below.
    * Throws exception when file not found
    */
  def loadYaml(filename: String): YAML = {
    val source = Source.fromResource(filename)
    val lines = stringToLines(source.mkString) 
    
    var out: YAML = Array[Array[String]]()
    
    var levelNames: Array[String] = Array.ofDim(MAXDEPTH)
    
    lines
      .filterNot(line => line.trim.isEmpty)
      .filterNot(line => line.trim.startsWith("#"))
      .foreach(line => {
        val count = countPrefixSpaces(line)
        val level: Int = count/4
        val p = line.split(":")
        val key = p(0).replace(" ", "")
        levelNames(level) = key
        if(p.length > 1 && !p(1).trim.isEmpty){
          var kk = ""
          for(i <- 0 until level-1)
            kk = kk + levelNames(i) + "."
          kk = kk + key
          
          out = put(out, kk, p(1).trim())
        }
      })
    out
  }

  /**
    * Returns wether or not the field at the query exists.
    */
  def hasField(yaml: YAML, query: String): Boolean = {
    get(yaml, query) != null
  }

  /**
    * Gets a string value from the yaml according to the query.
    * Throws exception when query not found
    */
  def getString(yaml: YAML, query: String): String = {
    get(yaml, query)
  }

  /**
    * Gets a int value from the yaml according to the query.
    * Throws exception when query not found
    */
  def getInt(yaml: YAML, query: String): Int = {
    get(yaml, query).toInt
  }

  /**
    * Gets a float value from the yaml according to the query.
    * Throws exception when query not found
    */
  def getFloat(yaml: YAML, query: String): Float = {
    get(yaml, query).toFloat
  }

  /**
    * Gets a date value from the yaml according to the query.
    * Throws exception when query not found
    */
  def getDate(yaml: YAML, query: String, format: java.text.SimpleDateFormat): Date = {
    format.parse(get(yaml, query))
  }

  private def stringToLines(string: String) : Array[String] = {
    string.split("\n|\r").filterNot(p => p.trim().isEmpty)
  }

  private def countPrefixSpaces(line: String) : Int = {
    var count: Int = 0
    line.toCharArray.foreach(c => {
      if(c == ' ')
        count = count+1
      else if(c == '\t')
        count = count+4
      else
        return count
    })
    count
  }
  
  private def put(arr: Array[Array[String]], key: String, value: String): Array[Array[String]] = {
      arr :+ Array(key, value)
  }
  
  private def get(arr: Array[Array[String]], key: String): String = {
    val found = arr.find(p => p(0).equals(key))
    if(found.isEmpty)
      return null
    found.get(1)
  }
  
}

