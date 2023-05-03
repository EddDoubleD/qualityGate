# GitLab Quality Gate
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white) 
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Jira](https://img.shields.io/badge/jira-%230A0FFF.svg?style=for-the-badge&logo=jira&logoColor=white)
![GitLab](https://img.shields.io/badge/gitlab-%23181717.svg?style=for-the-badge&logo=gitlab&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)<br/> 
[![build](https://github.com/EddDoubleD/qualityGate/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/EddDoubleD/qualityGate/actions/workflows/build.yml) [![codecov](https://codecov.io/gh/EddDoubleD/qualityGate/branch/master/graph/badge.svg?token=TYXJX2Z7TH)](https://codecov.io/gh/EddDoubleD/qualityGate)<br/>
Service application for customizing gitlab and jira integration

## Settings
Application setup is divided into several modules, in the main setting, the paths to the settings files are set
### application.yml
```yaml
jira:
  login: your jira login
  password: your jira password
  url: https://your-domain.atlassian.net/
  mask: \w{2,4}-\d{1,10}
  link:
    LINK:
      name: DEVELOPMENT
      strategy: LINK
      issueTypes: ["bug", "dev"]
    jql:
      name: DEVELOPMENT
      strategy: JQL
      jql: "project = PROJECT AND \"custom_field\" = #{key}"
      issueTypes: ["bug", "dev"]
    default:
      name: DEFAULT
      strategy: DEFAULT

smtp:
  disable: true
  username: user.name
  password: user.password
  directoryPath: /
  template: email.vm

gitlab:
  url: https://your-domain.gitlab.com
  token: quality_gate_bot_token
```
Parameters that need to be masked can be set as application startup parameters, example:
```bash
java -jar app.jar --jira.login=log.in --jira.password=passw0rd ... --smtp.host-mail.server.com ...
```
## Usage

### GitLab stage yml-file
```yml
quality-gate:
  image: quality-gate:1.0.0 # path to docker image
  stage: quality-gate
  allow_failure: true
  rules:
    ... # set start stage rules
  before_script:
    # Preparing custom application settings
    # Setting up interaction with Jira
    - cp ./${project_dir}/jira.json /src/main/resources/settings/jira.json
    # Setting up interaction with GitLab
    - cp ./${project_dir}/gitlab.json /src/main/resources/settings/gitlab.json
  script:
    - cd /app/ && java -jar app.jar
```

## Build docker
Use the liberica image, depending on the architecture of your runners:
* bellsoft/liberica-openjdk-debian:17.0.6  for *arm64*
* bellsoft/liberica-openjdk-debian:17.0.6-x86_64 for *amd64*

``` bash
# maven build app 
mvn -e clean install
# build docker image 
docker build --build-arg JAR_FILE=target/qualityGate-0.0.1-SNAPSHOT.jar -t edddoubled/quality_gate:v1 .
# run docker image
docker run -d --name quality-gate edddoubled/quality_gate:v1
```
