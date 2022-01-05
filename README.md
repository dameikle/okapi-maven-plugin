# okapi-maven-plugin
[![license](https://img.shields.io/github/license/dameikle/okapi-maven-plugin.svg?maxAge=2592000)](http://www.apache.org/licenses/LICENSE-2.0)
![build](https://github.com/dameikle/okapi-maven-plugin/actions/workflows/maven.yml/badge.svg)


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

The plugin is released used GitHub Packages (see _Usage_ below for more details), however to install a local copy from
this repo, you can run:
```bash
mvn clean install
```

## Usage

Currently, the plugin is released used [GitHub Packages](https://docs.github.com/en/packages/learn-github-packages/introduction-to-github-packages).

To be able to access this package you will need to configure access within your Maven settings by following
the [Working with the Apache Maven registry](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages) guide.

The plugin repository details required for this guide are shown below:
```xml
<pluginRepository>
  <id>github</id>
  <url>https://maven.pkg.github.com/dameikle/okapi-maven-plugin</url>
  <snapshots>
    <enabled>true</enabled>
  </snapshots>
</pluginRepository>
```

Once you have set up access, to use the plug-in you can simple include it in your project's POM file in the plugins 
section, adding configuration details relevant to the action required (see _Available Mojos_ for more details).

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
      </plugin>
    </plugins>
  </build>
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
        <directory>input</directory>
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
    <!-- Optional directory containing custom filters -->
    <!-- Defaults to the project directory -->
    <filtersDirectory>filtersDir</filtersDirectory>
    <!-- Optional directory containing custom plugins -->
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
    <!-- Optional Plugins to be included in the BCONF -->
    <plugins>
        <plugin>plugin_one.jar</plugin>
        <plugin>plugin_two.jar</plugin>
    </plugins>
    <!-- Optional directory containing custom plugins -->
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
    <!-- Mandatory Name or Path to Export the BCONF to -->
    <bconf>export.bconf</bconf>
    <!-- Mandatory Name or Path to Install the BCONF to -->
    <outputDirectory>/tmp/install-test</outputDirectory>
</configuration>
```

## Licence
Copyright 2021 David Meikle

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
