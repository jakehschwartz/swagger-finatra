package com.github.xiaodongw.swagger.finatra

import com.twitter.finatra._
import com.wordnik.swagger.util.Json
import org.apache.commons.io.IOUtils

class SwaggerController(docPath: String = "/api-docs") extends Controller {
  get(docPath) { request =>
    val swagger = FinatraSwagger.swagger

    render.body(Json.mapper.writeValueAsString(swagger))
      .contentType("application/json").toFuture
  }

  get(docPath + "/ui") { request =>
    redirect(docPath + "/ui/index.html").toFuture
  }

  get(docPath + "/ui/*") { request =>
    val res = request.path.replace(docPath + "/ui/", "")

    resource(s"/public/swagger-ui/${res}").toFuture
  }

  /*
  render.static cannot load resource file when in non-production mode
  so use this method to work around this issue
   */
  private def resource(path: String): ResponseBuilder = {
    val stream  = getClass.getResourceAsStream(path)

    try {
      val bytes   = IOUtils.toByteArray(stream)
      stream.read(bytes)
      val mtype = FileService.extMap.getContentType('.' + path.split('.').last)

      render.status(200)
        .header("Content-Type", mtype)
        .body(bytes)
    } finally {
      IOUtils.closeQuietly(stream)
    }
  }
}
