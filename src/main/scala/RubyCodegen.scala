import com.wordnik.swagger.codegen.BasicGenerator

import com.wordnik.swagger.core._

import java.io.File

object RubyCodegen extends RubyCodegen {
  def main(args: Array[String]) = generateClient(args)
}

class RubyCodegen extends BasicGenerator {
  override def apiNameFromPath(apiPath: String) = {
    val name = apiPath.split("\\.")(0).replaceAll("/", "")
    name.charAt(0).toUpperCase + name.substring(1) + "_api"
  }

  override def apiPackage = Some("lib")

  // location of templates
  override def templateDir = "src/main/resources/ruby"

  // template used for models
  modelTemplateFiles += "model.mustache" -> ".rb"

  // template used for models
  apiTemplateFiles += "api.mustache" -> ".rb"

  // where to write generated code
  override def destinationDir = "generated-code/ruby"

  // file suffix
  override def fileSuffix = ".rb"

  // package for models
  override def modelPackage = Some("models")

  // response classes
  override def processResponseClass(responseClass: String): Option[String] = {
    responseClass match {
      case "void" => None
      case e: String => Some(e)
    }
  }

  override def toVarName(name: String): String = toUnderscore(name)

  override def toMethodName(name: String): String = toUnderscore(name)

  def toUnderscore(name: String): String = {
    val sb = new StringBuilder
    for ((char) <- super.toVarName(name)) {
      if (char.isUpperCase) sb.append("_").append(char.toLowerCase)
      else sb.append(char)
    }
    sb.toString
  }

  override def toDeclaration(obj: DocumentationSchema) = {
    var datatype = obj.getType.charAt(0).toUpperCase + obj.getType.substring(1)

    datatype match {
      case "Array" => datatype = "List"
      case e: String => e
    }

    val defaultValue = toDefaultValue(datatype, obj)
    datatype match {
      case "List" => {
        val inner = {
          if (obj.items.ref != null) obj.items.ref
          else obj.items.getType
        }
        datatype = "java.util.List[" + inner + "]"
      }
      case _ =>
    }
    (datatype, defaultValue)
  }

  // supporting classes
  override def supportingFiles = List(
    ("monkey.mustache", destinationDir + File.separator + apiPackage.get, "monkey.rb"),
    ("swagger.mustache", destinationDir + File.separator + apiPackage.get, "swagger.rb"),
    ("swagger" + File.separator + "configuration.mustache", destinationDir + File.separator + apiPackage.get, "swagger" + File.separator + "configuration.rb"),
    ("swagger" + File.separator + "response.mustache", destinationDir + File.separator + apiPackage.get, "swagger" + File.separator + "response.rb"),
    ("swagger" + File.separator + "version.mustache", destinationDir + File.separator + apiPackage.get, "swagger" + File.separator + "version.rb"),
    ("swagger" + File.separator + "request.mustache", destinationDir + File.separator + apiPackage.get, "swagger" + File.separator + "request.rb"))
}