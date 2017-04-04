/**
  * Created by Sijmen on 29-3-2017.
  */
object Test {

  def main(args: Array[String]): Unit = {
    val format = new java.text.SimpleDateFormat("dd-MM-yyyy")

    val yaml = YAML.loadYaml("test1.yaml")

    expectError(() => YAML.loadYaml("test99.yaml"))
    
    //test has fields
    assertTrue(YAML.hasField(yaml, "invoice"))
    assertTrue(YAML.hasField(yaml, "bill-to.family"))
    assertTrue(YAML.hasField(yaml, "bill-to.address.postal"))
    assertTrue(YAML.hasField(yaml, "ship-to"))
    assertEquals(YAML.getString(yaml, "bill-to.family"), "Dumars")

    //get String, Date, Int, Float
    assertEquals(YAML.getString(yaml, "invoice"), "34843")
    assertEquals(YAML.getInt(yaml, "invoice"), 34843)
    assertEquals(YAML.getDate(yaml, "date", format), format.parse("2001-01-23"))
    assertEquals(YAML.getString(yaml, "date"), "2001-01-23")
    assertEquals(YAML.getFloat(yaml, "total"), 4443.52f)
    expectError(() => YAML.getInt(yaml, "total"))
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
