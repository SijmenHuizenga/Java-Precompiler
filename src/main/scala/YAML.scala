import java.util.Date

import sun.reflect.generics.reflectiveObjects.NotImplementedException

import scala.io.Source
import scala.reflect.ClassTag

/**
  * Created by Sijmen on 29-3-2017.
  */

object YAML {

  type YAML = Array[YAMLDECL]
  type YAMLDECL = (String, AnyVal, Array[Any])
  
  /**
    * Given a filename, returns a array that contains the data.
    * Data can be extracted from this array by the methods below.
    * Throws exception when file not found
    */
  def loadYaml(filename: String): YAML = {
    val source = Source.fromResource(filename)
    parse(source.mkString)
  }

  /**
    * Returns wether or not the field at the query exists.
    */
  def hasField(yaml: YAML, query: String): Boolean = {
    throw new NotImplementedException()
  }

  /**
    * Gets a string value from the yaml according to the query.
    * Throws exception when query not found
    */
  def getString(yaml: YAML, query: String): String = {
    throw new NotImplementedException()
  }

  /**
    * Gets a int value from the yaml according to the query.
    * Throws exception when query not found
    */
  def getInt(yaml: YAML, query: String): Int = {
    throw new NotImplementedException()
  }

  /**
    * Gets a float value from the yaml according to the query.
    * Throws exception when query not found
    */
  def getFloat(yaml: YAML, query: String): Float = {
    throw new NotImplementedException()
  }

  /**
    * Gets a date value from the yaml according to the query.
    * Throws exception when query not found
    */
  def getDate(yaml: YAML, query: String, format: java.text.SimpleDateFormat): Date = {
    throw new NotImplementedException()
  }

  /**
    * returns wether or not the given query is an array
    * Throws exception when query not found
    */
  def isArray(yaml: YAML, query: String): Boolean = {
    throw new NotImplementedException()
  }

  /**
    * Get the length of the array at the qiven query.
    * Throws exception when query not found
    */
  def getArrayLength(yaml: YAML, query: String): Int = {
    throw new NotImplementedException()
  }

  /**
    * Returns the array at the given query of the type in the getter.
    * Throws exception when query not found
    */
  def getArray[T:ClassTag](yaml: YAML, query: String, getter: (YAML, String) => T): Array[T] = {
    throw new NotImplementedException()
  }

  /**
    * Get a yaml datastructure at a sertain node. This is used for key-value fields inside arrays
    * Throws exception when query not found
    */
  def getYAML(yaml: YAML, query: String): Array[Any] = {
    throw new NotImplementedException()
  }

  def parse(input: String): YAML = {
    var output: Array[YAMLDECL] = Array[YAMLDECL]()
    val lines: Array[String] = stringToLines(input)
    
    //the amount of spaces on every level
    var spaceLevels: Array[Int] = Array()
    def getLevel(line: String): Int = {
      var level = -1
      if(line.startsWith(" ") || line.startsWith("\t")){
        val spacesCount = countPrefixSpaces(line)

        for(i <- spaceLevels.indices){
          if(spaceLevels(i) == spacesCount)
            level = i
        }
        if(level == -1){
          if(spaceLevels.length == 0){
            level = 0
            spaceLevels = spaceLevels :+ spacesCount
          }
          if(spacesCount > spaceLevels.last){
            level = spaceLevels.length
            spaceLevels = spaceLevels :+ spacesCount
          }
        }
      }else{
        level = 0
        spaceLevels = Array(0)
      }
      level
    }
    
    //the key to every level
    var levelKeys: Array[String] = Array()

    def parseArrayItemLine(line: String) = {

    }
    
    def parseDeclarationLine(line: String) = {
      
    }
    
    def parseLine(line: String) = {
      val level: Int = getLevel(line)
      if(line.trim().startsWith("-"))
        parseArrayItemLine(line)
      else
        parseDeclarationLine(line)
    }
    
    lines.foreach {
      case line if line.trim().startsWith("#") => line.charAt(0)
      case line => parseLine(line)
    }
    
    output
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


}

