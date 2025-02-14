## Steps to reproduce:

1. export OTEL_JAVAAGENT_DEBUG=true
2. In separate terminal `python3 server.py` (OTEL colector mock)
3. `sbt clean stage`
4. `./target/universal/stage/bin/tapirtest` (run sample application)
5. In third terminal you could query the smple endpoints

## Results
1. First tapir endpoint

```
curl http://localhost:8080/hello/1/a?name=b
Hello a b
```

Debug log. Transaction name is set to "GET"

<code>
[otel.javaagent 2025-02-14 19:11:34:481 +0000] [default-akka.actor.default-dispatcher-8] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET' : b7e3fc8503802258faab589fb3576ff6 9cb636ea89b1a0dc SERVER [tracer: io.opentelemetry.akka-http-10.0:2.11.0-alpha] AttributesMap{data={http.request.method=GET, network.protocol.version=1.1, server.address=localhost, user_agent.original=curl/8.7.1, url.path=/hello/1/a, url.query=name=b, url.scheme=http, server.port=8080, thread.name=default-akka.actor.default-dispatcher-8, http.response.status_code=200, thread.id=28}, capacity=128, totalAddedValues=11}
</code>

2. Second tarpir endpoint

```
curl http://localhost:8080/hello/2/c
Hello2 c
```

The same result. We get only "GET"

<code>
[otel.javaagent 2025-02-14 19:20:22:536 +0000] [default-akka.actor.default-dispatcher-5] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET' : 94069c7d7884bb3fd06325205107149e a9b300bd301ee4ec SERVER [tracer: io.opentelemetry.akka-http-10.0:2.11.0-alpha] AttributesMap{data={http.request.method=GET, network.protocol.version=1.1, server.address=localhost, user_agent.original=curl/8.7.1, url.path=/hello/2/c, url.scheme=http, server.port=8080, thread.name=default-akka.actor.default-dispatcher-5, http.response.status_code=200, thread.id=24}, capacity=128, totalAddedValues=10}
</code>

3. Third endpoint. Plain AKKA route without using tapir.

```
curl http://localhost:8080/hello/3/d
```

OTEL debug log contain proper transaction name with placeholders - "GET null/hello/3/*"

<code>
[otel.javaagent 2025-02-14 19:13:09:347 +0000] [default-akka.actor.default-dispatcher-12] INFO io.opentelemetry.exporter.logging.LoggingSpanExporter - 'GET null/hello/3/*' : 91ec5ac7c6ee4cbc628b806dc0186f35 31d1f1c5be5b17cf SERVER [tracer: io.opentelemetry.akka-http-10.0:2.11.0-alpha] AttributesMap{data={http.request.method=GET, http.route=null/hello/3/*, network.protocol.version=1.1, server.address=localhost, user_agent.original=curl/8.7.1, url.path=/hello/3/d, url.scheme=http, server.port=8080, thread.name=default-akka.actor.default-dispatcher-12, http.response.status_code=200, thread.id=37}, capacity=128, totalAddedValues=11}
</code>