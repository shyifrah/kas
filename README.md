KAS
===

# What is KAS?
KAS is (or, better yet, will be) an open source Message Bus.

# Setting up your development environment
1. Development is done over Eclipse IDE (Photon) with jdk-1.8_u151. Download and install both.
2. Clone the repository to c:\dev\kas.
3. Start Eclipse. Point Eclipse to c:\dev\kas_ws for its workspace folder.
4. Configure your workspace (Window > Preferences):<br>
  a. Under Java > Installed JRE > Add the previously installed JDK.<br>
  b. Under Java > Code Style > Clean up > IMPORT - Select c:\dev\kas\cleanup.xml<br>
  c. Under Java > Code Style > Formattter > IMPORT - Select c:\dev\kas\formatter.xml<br>
  d. Under General > Editors > Text Editors > Set Displayed tab width = 2; Check Insert spaces for tabs.<br>
5. Import Existing Gradle projects and point Eclipse to c:\dev\kas.

# Projects
Development is splitted into several projects, the output of each is a JAR file. Building is done using Gradle.
Each project relies on the output of other projects. Dependecies is shown below.

## Project descriptions

| Project        | Description   |
|:--------------:|---------------|
| kas-infra      | Infrastructure |
| kas-config     | Base Configuration |
| kas-logging    | Logger |
| kas-comm       | Communication and Serialization |
| kas-db         | DataBase access |
| kas-appl       | Base Application |
| kas-mq-core    | Core KAS/MQ |
| kas-mq-server  | KAS/MQ Server     |
| kas-mq-samples | Sample KAS/MQ Applications |
| kas-mq-admin   | KAS/MQ Admin Console |
| kas-sec-core   | Security |
| kas-data       | Not a project, more like a repository |

## Project dependencies
Each level in the following tree represents a dependency on the previous level<br>
`kas-infra`<br>
`|`<br>
`+-- kas-config`<br>
`|`<br>
`+----- kas-logging`<br>
`|`<br>
`+-------- kas-comm`<br>
`|`<br>
`+-------- kas-appl`<br>
`|`<br>
`+-------- kas-mq-core`<br>
`|`<br>
`+-------- kas-db`<br>
`|`<br>
`+----------- kas-sec-core`<br>
`|`<br>
`+----------- kas-mq-admin`<br>
`|`<br>
`+----------- kas-mq-samples`<br>
`|`<br>
`+-------------- kas-mq-server`<br>
