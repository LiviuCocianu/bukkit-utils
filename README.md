# Bukkit Utils
My utility bundle that I personally use to create Minecraft plugins. It comes with useful functions for command creation, config file setup, inventory UI builder, SQLite setup and much more!

## Acknowledgements
* The code is not fully documented, so I apologize in advance for the unintuitive undocumented methods here and there. This bundle was originally intended to be only used by me, so I didn't feel the need to document my methods at the time of their creation...

## Installation
* Clone this repository on your machine with `git clone https://github.com/LiviuCocianu/bukkit-utils.git`
* Run `mvn install` to install this project on your local Maven repository. *If you use IntelliJ, there should be a "Maven" side menu to your right, then "Lifecycle", where you can run this command from*
* Access the pom.xml file of your plugin and add this snippet of code to your dependencies:

```XML
  <dependency>
    <groupId>io.github.idoomful</groupId>
    <artifactId>BukkitUtils</artifactId>
    <version>1.0.0</version>
  </dependency>
```

## Building
If you want to build the jar, make sure you edit this section from BukkitUtils' pom.xml to whatever directory you want to represent the output path:

```XML
<outputFile>C:/Users/user/Desktop/${project.artifactId}-${project.version}.jar</outputFile>
```

You will also need to install Spigot's artifacts to your local Maven repository with [BuildTools](https://www.spigotmc.org/wiki/buildtools/#what-is-it) depending on what Minecraft version you want to use. This bundle currently uses Spigot for 1.18.2, but you can change the version in pom.xml and install the corresponding artifacts with BuildTools