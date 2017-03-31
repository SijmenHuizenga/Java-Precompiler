**Original post [here](https://blog.sijmen.it/2017/03/31/scala-challenge-yaml/)**

Three years ago, when i was 15 years old, i wrote my first file parser. The parser could parse simple YAML structures and access its values using string key's. Now, 5 years later, to finish up project *Hello Scala* i challenge myself to re-do this parser in Scala using only the [Functional Programming Paradigm](https://en.wikipedia.org/wiki/Functional_programming) part of Scala. In this blog post, i will explain the challenge. 

The goal is to create a Parser that accepts a subset of features from the [YAML 1.2](http://yaml.org/spec/1.2/spec.html) standard and uses just-in-time parsing to provide values by given queries.

An example input follows
```YAML
invoice: 34843
date   : 2001-01-23
bill-to: &id001
    given  : Chris
    family : Dumars
    address:
        lines: |
            458 Walkman Dr.
            Suite #292
        city    : Royal Oak
        state   : MI
        postal  : 48046
ship-to: *id001
##these are all the orderd products:
product:
    - sku         : BL394D
      quantity    : 4
      description : Basketball
      price       : 450.00
    - sku         : BL4438H
      #quantity of this type;
      quantity    : 1
      description : Super Hoop
      price       : 2392.00
favoritemovies:
    - Iron Man
    - Iron Man 2
    - Iron Man 3
tax  : 251.42
total: 4443.52
comments: >
    Late afternoon is best.
    Backup contact is Nancy
    Billsmer @ 338-4338.
```
This is just an example of an infinitive amount of input's possible with the given yaml features. To be clear, the following features will be supported in this yaml parser:

* key value paris
* String values
* Int values
* Float values
* Date values
* Multiline arrays using the `-` character with all above value types.
* Constant decleration using the `&`
* Constant reference using the `*`
* multiline strings preserving multilines using `|`
* multiline strings collapsing text using the `>` 
* Comments using the `#`

Corresponding method declerations with descriptions:
```
/**
 * Given a filename, returns a array that contains the data. 
 * Data can be extracted from this array by the methods below.
 * Throws exception when file not found
 */
def loadYaml(filename: String): Array[Any]

/**
 * Returns wether or not the field at the query exists.
 */ 
def hasField(yaml: Array[Any], query: String) : Boolean

/**
 * Gets a string value from the yaml according to the query.
 * Throws exception when query not found
 */
def getString(yaml: Array[Any], query: String): String

/**
 * Gets a int value from the yaml according to the query.
 * Throws exception when query not found
 */
def getInt(yaml: Array[Any], query: String): Int

/**
 * Gets a float value from the yaml according to the query.
 * Throws exception when query not found
 */
def getFloat(yaml: Array[Any], query: String): Float

/**
 * Gets a date value from the yaml according to the query.
 * Throws exception when query not found
 */
def getDate(yaml: Array[Any], query: String): Date

/**
 * returns wether or not the given query is an array
 * Throws exception when query not found
 */
def isArray(yaml: Array[Any], query: String): Boolean

/**
 * Get the length of the array at the qiven query.
 * Throws exception when query not found
 */
def getArrayLength(yaml: Array[Any], query: String): Int

/**
 * Returns the array at the given query of the type in the getter.
 * Throws exception when query not found
 */
def getArray[T](yaml: Array[Any], query: String, getter: (Array[Any], String) => T): Array[T]

/**
 * Get a yaml datastructure at a sertain node. This is used for key-value fields inside arrays
 * Throws exception when query not found
 */
def getYAML(yaml: Array[Any], query: String): Array[Any]
```

Values can be accessed using querie keys. Normal strings are used to get a top-level-element. Using a ``.`` a sub-element is accessed. So ``a.b`` gets the value "Hello World":
```YAML
a:
    b: hello world!
```
Using `[n]` you can access array elements. For example ``a.b[1]`` is "Hello World":
```YAML
a: 
    b: 
	- Hello Country
	- Hello World
```
When accessing fields references are flattened. So the query ``b.hello`` returns "Hello World":
```YAML
a: &id001
  hello: Hello World
b: $id001
```

All parsing will happen just-in-time. This means the input will not be parsed when loaded, but when the field is first accessed. This makes performance super effective when loading big files and only a few elements are accessed.
