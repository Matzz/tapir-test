package com.matzz

import io.opentelemetry.api.GlobalOpenTelemetry
import io.opentelemetry.api.trace.Tracer
import sttp.tapir.server.metrics.{EndpointMetric, Metric}

import scala.concurrent.Future

object OTELInterceptor {
  private lazy val tracer: Tracer = GlobalOpenTelemetry
    .get()
    .getTracerProvider
    .get("scope.name", "scope.version")

  lazy val otelMetric: Metric[Future, Tracer] =
    Metric(
      metric = tracer,
      onRequest = (serverRequest, agg, me) =>
        me.eval {
          val span = agg.spanBuilder("unknown").startSpan()


          EndpointMetric(
            onEndpointRequest = Some(endpoint =>
              me.eval {
                val path = endpoint.showPathTemplate(includeAuth = false, showQueryParam = None)
                val method = endpoint.method.map(_.method).getOrElse("")
                span.updateName(s"${method} ${path}")
              }
            ),
            onResponseHeaders = None,
            onResponseBody = Some((ep, response) =>
              me.eval {
                span.end()
              },
            ),
            onException = Some((ep, throwable) =>
              me.eval {
                span.end()
              },
            )
          )
        },
    )
}
