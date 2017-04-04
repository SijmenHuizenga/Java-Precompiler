import java.util.Date

import YAML.YAML

/**
  * Created by Sijmen on 29-3-2017.
  */
object Test {

  def main(args: Array[String]): Unit = {
    val format = new java.text.SimpleDateFormat("dd-MM-yyyy")

    val yaml = YAML.loadYaml("test2.yaml")

//    yaml.foreach(l => {
//      println(l)
//      println("----")
//    })

    expectError(() => YAML.loadYaml("test99.yaml"))

//    val q = s"invoice\\s*:.*".r
//    val str = "invoice: test"
//    assertTrue(q.unapplySeq(str).isDefined)


    //test has fields
    assertTrue(YAML.hasField(yaml, "invoice"))
    assertTrue(YAML.hasField(yaml, "bill-to.family"))
    assertTrue(YAML.hasField(yaml, "bill-to.address"))
    assertTrue(YAML.hasField(yaml, "bill-to.address.lines"))
    assertTrue(YAML.hasField(yaml, "ship-to"))
    assertEquals(YAML.getString(yaml, "bill-to.family"), "Dumars")
    assertTrue(YAML.hasField(yaml, "product"))
    assertTrue(YAML.hasField(yaml, "product.[0]"))
    assertTrue(YAML.hasField(yaml, "product.[1]"))
    assertFalse(YAML.hasField(yaml, "product.[2]"))
    assertTrue(YAML.hasField(yaml, "favoritemovies.[2]"))
    assertTrue(YAML.hasField(yaml, "comments"))
    assertTrue(YAML.hasField(yaml, "product.[1].description"))

    //get String, Date, Int, Float
    assertEquals(YAML.getString(yaml, "invoice"), "34843")
    assertEquals(YAML.getInt(yaml, "invoice"), 34843)
    assertEquals(YAML.getDate(yaml, "date", format), format.parse("2001-01-23"))
    assertEquals(YAML.getString(yaml, "date"), "2001-01-23")
    assertEquals(YAML.getFloat(yaml, "total"), 4443.52f)
    expectError(() => YAML.getInt(yaml, "total"))
    assertEquals(YAML.getString(yaml, "comments"), "Late afternoon is best. Backup contact is Nancy Billsmer @ 338-4338.")
    assertEquals(YAML.getString(yaml, "bill-to.address.lines"), "458 Walkman Dr.\nSuite #292")

    //is array, array size
    assertTrue(YAML.isArray(yaml, "product"))
    assertTrue(YAML.isArray(yaml, "favoritemovies"))
    assertEquals(YAML.getArrayLength(yaml, "product"), 2)
    assertEquals(YAML.getArrayLength(yaml, "favoritemovies"), 3)

    //get array
    assertEquals(YAML.getArray(yaml, "favoritemovies", YAML.getString), Array("Iron Man", "Iron Man 2", "Iron Man 3"))
    val productarray = YAML.getArray(yaml, "product", YAML.getYAML).asInstanceOf[Array[YAML]]
    assertEquals(YAML.getString(productarray(0), "sku"), "BL394D")
    assertEquals(YAML.getFloat(productarray(0), "price"), 450.00f)
    assertEquals(YAML.getFloat(productarray(1), "sku"), "BL4438H")
    assertEquals(YAML.getFloat(productarray(1), "price"), 2392.00f)

    expectError(() => YAML.getString(yaml, "test"))
    expectError(() => YAML.getArray(yaml, "invoice", YAML.getString))

    //variales
    assertTrue(YAML.hasField(yaml, "ship-to.family"))
    assertTrue(YAML.hasField(yaml, "ship-to.address"))
    assertTrue(YAML.hasField(yaml, "ship-to.address.lines"))
    assertTrue(YAML.hasField(yaml, "ship-to.address.city"))
    assertEquals(YAML.getString(yaml, "ship-to.city"), "Royal Oak")
  }

  def assertTrue(x: Boolean): Unit ={
    assertEquals(x, true)
  }

  def assertFalse(actual: Boolean): Unit ={
    assertEquals(actual, false)
  }

  def assertEquals(actual : Any, expected : Any) {
    if(expected == null && actual == null)
      return
    if(expected == null || actual == null)
      throw new IllegalArgumentException("expected " + tostr(expected) + " but got " + tostr(actual))
    if(expected.isInstanceOf[Array[Any]] || expected.isInstanceOf[List[Any]]){
      if(!expected.toString.equals(expected.toString)){
        throw new IllegalArgumentException("expected " + tostr(expected) + " but got " + tostr(actual))
      }
    }else if(!expected.equals(actual)){
      throw new IllegalArgumentException("expected " + tostr(expected) + " but got " + tostr(actual))
    }
  }
  
  def tostr(vas: Any): String = {
    vas match {
      case null => "null"
      case x: Array[Any] => x.toList.toString()
      case x => x.toString
    }
  }

  def expectError(predicate: () => Unit) {
    try{
      predicate()
    }catch{
      case _: Exception => return
    }
    throw new IllegalArgumentException("Expected error, got no error")
  }

}
