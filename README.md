# okapi-maven-plugin
[![license](https://img.shields.io/github/license/dameikle/okapi-maven-plugin.svg?maxAge=2592000)](https://github.com/dameikle/okapi-maven-plugin/blob/main/LICENSE)
[![build](https://github.com/dameikle/okapi-maven-plugin/actions/workflows/maven.yml/badge.svg)](https://github.com/dameikle/okapi-maven-plugin/actions)
[![maven-central](https://img.shields.io/maven-central/v/io.meikle.maven.okapi/okapi-maven-plugin)](https://search.maven.org/artifact/io.meikle.maven.okapi/okapi-maven-plugin)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/io.meikle.maven.okapi/okapi-maven-plugin?label=ossrh-snapshot&server=https%3A%2F%2Fs01.oss.sonatype.org)](https://s01.oss.sonatype.org/#nexus-search;quick~okapi-maven-plugin)

Maven plugin that allows execution of common actions using the [Okapi Framework](http://okapiframework.org) 
within a Maven Project, inspired by the [okapi-ant](https://github.com/tingley/okapi-ant) project.

Currently, it supports performing the following actions:
* Execute a Pipeline
* Export a Batch Configuration (BCONF)
* Install a Batch Configuration (BCONF)

These actions can all be configured within the project's POM file.

## Building

You need the following pre-requisites:
* Java 8+
* Maven 3.x

To build run the following from inside the _okapi-maven-plugin_ directory:

```bash
mvn clean package
```

The plugin is released via Maven Central (see _Usage_ below for more details), however to install a local copy from
this repo, you can run:
```bash
mvn clean install
```

## Usage

The plugin is being released to [Maven Central](https://central.sonatype.org) via the Sonatype OSSRH
(OSS Repository Hosting) [repository](https://s01.oss.sonatype.org/#welcome). 

To use the plug-in you can simply include it in your project's POM file in the plugins
section, adding configuration details relevant to the action required (see _Available Mojos_ for more details).

```xml
  <build>
    <plugins>
      <plugin>
        <groupId>io.meikle.maven.okapi</groupId>
        <artifactId>okapi-maven-plugin</artifactId>
        <version>1.1</version>
        <configuration>
            ...
        </configuration>
      </plugin>
    </plugins>
  </build>
```
To access snapshot versions during development, you will need to add the OSSRH to your Maven settings and enable it to
resolve snapshots.

The plugin repository details required for this guide are shown below:
```xml
<pluginRepository>
    <id>oss-sonatype</id>
    <name>oss-sonatype</name>
    <url>https://s01.oss.sonatype.org/content/repositories/snapshots/</url>
    <snapshots>
        <enabled>true</enabled>
    </snapshots>
</pluginRepository>
```

This could be added in a profile within your _settings.xml_, or alternatively you could add a _<pluginRepositories>_
section to your project's POM file:
```xml
<pluginRepositories>
    <pluginRepository>
        <id>oss.sonatype.org-snapshot</id>
        <url>https://s01.oss.sonatype.org/content/repositories/snapshots/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </pluginRepository>
</pluginRepositories>
```

## Available Mojos

### okapi:version

#### Overview
The _OkapiVersionMojo_ outputs the version of Okapi used by the plugin.

It can be mapped against an existing phase, or executed directly using:
```bash
mvn okapi:version
```

#### Configuration
N/A

### okapi:pipeline

#### Overview
The _OkapiPipelineExecuteMojo_ executes a pipeline contained in a pipeline configuration file (a _pln_ file)
against the supplied input file(s).

It can be mapped against an existing phase, or executed directly using:
```bash
mvn okapi:pipeline
```

#### Configuration

```xml
<configuration>
    <!-- Mandatory Path to pipeline to execute -->
    <pipeline>test.pln</pipeline>
    <!-- Mandatory inputFiles using Maven FileSet format-->
    <!-- See https://maven.apache.org/shared/file-management/fileset.html for more details -->
    <inputFiles>
        <!-- Optional directory - can be absolute (include Maven variables), or relative to project baseDir -->
        <!-- defaults to project baseDir -->
        <directory>input</directory>
        <!-- Optional - Includes or Excludes -->
        <includes>
            <include>**/*.txt</include>
            <include>**/*.md</include>
        </includes>
        <excludes>
            <exclude>**/*.xml</exclude>
        </excludes>
    </inputFiles>
    <!-- Mandatory Source Language Name or Code-->
    <sourceLang>English</sourceLang>
    <!-- Mandatory Target Language Name or Code-->
    <targetLang>fr-FR</targetLang>
    <!-- Optional directory containing custom plugins -->
    <!-- Defaults to the project directory -->
    <pluginsDirectory>pluginsDir</pluginsDirectory>
    <!-- Optional Explicit Plugins using Maven FileSet format- -->
    <!-- See https://maven.apache.org/shared/file-management/fileset.html for more details -->
    <plugins>
        <!-- Optional directory - can be absolute (include Maven variables), or relative to project baseDir -->
        <!-- defaults to project baseDir -->
        <directory>input</directory>
        <!-- Optional - Includes or Excludes -->
        <includes>
            <include>**/*.jar</include>
        </includes>
    </plugins>
    <!-- Optional directory containing custom filters -->
    <!-- Defaults to the project directory -->
    <filtersDirectory>filtersDir</filtersDirectory>
    <!-- Optional output directory for the Okapi pipeline -->
    <!-- Defaults to the Maven target directory -->
    <outputDirectory>/tmp</outputDirectory>
</configuration>
```

### okapi:export

#### Overview
The _OkapiExportBatchConfigMojo_ builds and export a Binary Configuration file (a _bconf_) based on
the configuration supplied. 

It can be mapped against an existing phase, or executed directly using:
```bash
mvn okapi:export
```

#### Configuration

```xml
<configuration>
    <!-- Mandatory Name or Path to Export the BCONF to -->
    <bconf>export.bconf</bconf>
    <!-- Optional Pipeline to be used in the BCONF -->
    <pipeline>test.pln</pipeline>
    <!-- Optional Custom Filter Mapping(s) to be embedded in the BCONF -->
    <filterMappings> 
        <filterMapping>
            <!-- File extension to be mapped -->
            <extension>.htm</extension>
            <!-- Configuration to be used -->
            <configuration>okf_html@test</configuration>
        </filterMapping>
    </filterMappings>
    <!-- Optional Plugins to be included in the BCONF using Maven FileSet format- -->
    <!-- See https://maven.apache.org/shared/file-management/fileset.html for more details -->
    <plugins>
        <!-- Optional directory - can be absolute (include Maven variables), or relative to project baseDir -->
        <!-- defaults to project baseDir -->
        <directory>input</directory>
        <!-- Optional - Includes or Excludes -->
        <includes>
            <include>**/*.jar</include>
        </includes>
    </plugins>
    <!-- Optional output directory for the Okapi pipeline -->
    <!-- Defaults to the Maven target directory -->
    <outputDirectory>/tmp</outputDirectory>
</configuration>
```

### okapi:install

#### Overview
The _OkapiInstallBatchConfigMojo_ installs a Binary Configuration file (a _bconf_) to a folder on the local
machine so it can be used.

It can be mapped against an existing phase, or executed directly using:
```bash
mvn okapi:install
```

#### Configuration

```xml
<configuration>
    <!-- Mandatory relative or absolute path to the BCONF to be installed -->
    <bconf>export.bconf</bconf>
    <!-- Mandatory relative or absolute path to install the BCONF to -->
    <outputDirectory>/tmp/install-test</outputDirectory>
</configuration>
```

## Advanced Configuration

### Different Okapi Version

The plugin has been developed against the latest released version at the time of writing (i.e. 1.44.0) and will aim to
track against the latest version.

If you want to use a different Okapi version, you can override the Okapi Framework dependency in the plugin's
configuration section of the POM.

For example, to use Okapi Framework version 1.43.0 you could use the following:
```xml
<build>
    <plugins>
        <plugin>
            <groupId>io.meikle.maven.okapi</groupId>
            <artifactId>okapi-maven-plugin</artifactId>
            <version>1.0-SNAPSHOT</version>
            <configuration>
                ...
            </configuration>
            <dependencies>
                <dependency>
                    <groupId>net.sf.okapi</groupId>
                    <artifactId>okapi-core</artifactId>
                    <version>1.43.0</version>
                </dependency>
                <dependency>
                    <groupId>net.sf.okapi.applications</groupId>
                    <artifactId>okapi-application-rainbow</artifactId>
                    <version>1.43.0</version>
                    <!-- Exclude non-required UI dependencies -->
                    <exclusions>
                        <exclusion>
                            <artifactId>*</artifactId>
                            <groupId>org.eclipse.platform</groupId>
                        </exclusion>
                    </exclusions>
                </dependency>
            </dependencies>
        </plugin>
    </plugins>
</build>
```

## Licence
Copyright 2021-22 David Meikle

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
