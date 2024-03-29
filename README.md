![Build Status](https://github.com/KvalitetsIT/stakit-backend/workflows/CICD/badge.svg)
# stakit-backend

Template repository showing how to be a good Java Spring Boot citizen in a k8s cluster.

## A good citizen

Below is a set of recommendations for being a good service. The recommendations are not tied to a specific language or 
framework.

1. Configuration through environment variables.
2. Expose readiness endpoint
3. Expose endpoint that Prometheus can scrape
4. Be stateless
5. Support multiple instances
6. Always be in a releasable state
7. Automate build and deployment.

Some of above recommendations are heavily inspired by [https://12factor.net/](https://12factor.net/). It is recommended 
read [https://12factor.net/](https://12factor.net/) for more inspiration and further details. Some points go 
further than just being a good service and also touches areas like operations.

## Getting started

Run `./setup.sh GIT_REPOSITORY_NAME`.

Above does a search/replace in relevant files. 

## Endpoints

### Service

The service is listening for connections on port 8080.

Spring boot actuator is listening for connections on port 8081. This is used as prometheus scrape endpoint and health monitoring. 

Prometheus scrape endpoint: `http://localhost:8081/actuator/prometheus`  
Health URL that can be used for readiness probe: `http://localhost:8081/actuator/health`

### Documentation

Documentation of the API is build as a separate Docker image. Documentation is build using Swagger. The documentation 
image is post-fixed with `-documentation`. The file `documentation/docker/compose/docker-compose.yml` contains a  setup 
that starts both the service and documentation image. The documentation can be accessed at `http://localhost/test` 
and the service can be called through the Swagger UI. 

In the docker-compose setup is also an example on how to set custom endpoints for the Swagger documentation service.

## Configuration

| Environment variable           | Description                                                                                           | Required   |
|--------------------------------|-------------------------------------------------------------------------------------------------------|------------|
| JDBC_URL                       | JDBC connection URL                                                                                   | Yes        |
| JDBC_USER                      | JDBC user                                                                                             | Yes        |
| JDBC_PASS                      | JDBC password                                                                                         | Yes        |
| LOG_LEVEL                      | Log Level for applikation  log. Defaults to INFO.                                                     | No         |
| LOG_LEVEL_FRAMEWORK            | Log level for framework. Defaults to INFO.                                                            | No         |
| CORRELATION_ID                 | HTTP header to take correlation id from. Used to correlate log messages. Defaults to "x-request-id".  | No         | 
| MAIL_HOST                      | SMTP Relay host                                                                                       | Yes        |
| MAIL_PORT                      | SMTP Relay port. Defaults to port 587                                                                 | No         |
| MAIL_USER                      | SMTP Relay username                                                                                   | Yes        |
| MAIL_PASSWORD                  | SMTP Relay password                                                                                   | Yes        |
| MAIL_FROM                      | From address when sending mails.                                                                      | Yes        | 
| STATUS_UPDATE_BODY_TEMPLATE    | Template to use for body in status update mails.                                                      | Yes        |
| STATUS_UPDATE_SUBJECT_TEMPLATE | Template to use for subject in status update mails.                                                   | Yes        | 
| ADAPTER_API_KEY                | API Key to use in adapter API.                                                                        | Yes        |
| JWT_SIGNING_KEY                | Path to public key in PEM format that was used to sign JWT tokens.                                    | Yes        |
| ALLOWED_ORIGINS                | Allowed origins. Normally set to frontend endpoint.                                                   | Yes        |
| BASE_URL                       | Base URL for frontend application.                                                                    | Yes        |
| CHECK_MESSAGES_FREQUENCY       | Frequency for how often it is checked if there are messages to send out. E.g. PT5S = every 5 seconds. | Yes        |
