## Tcp Library
Tcp Server or Tcp Client를 만들기 위한 프로젝트 입니다.

```yaml
tcp:
  server:
    port: 65535
    max-connection: 100
    charset: utf-8
  client:
    host: localhost
    port: ${tcp.server.port}
    connection-timeout: 1000 # millis
    read-timeout: 5000 # millis
    charset: ${tcp.server.charset}
    pool:
      min-idle: 10
      max-idle: 10
      max-total: ${tcp.server.max-connection}

```