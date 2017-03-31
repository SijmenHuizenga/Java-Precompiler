name := "Scala-YAML"

version := "1.0"

scalaVersion := "2.12.1"

scalaSource in Compile := baseDirectory.value / "src/main/scala"

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

initialize := {
  val _ = initialize.value
  if (sys.props("java.specification.version") != "1.8")
    sys.error("Java 8 is required for this project.")
}