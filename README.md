# Merlin Dreambot Scripts

This repo contains a collection of botting scripts for the [DreamBot][1] Old
School Runescape client. Why Merlin? That's my DreamBot username. Feel free to
reachout to me on the DreamBot forums if you have any questions or suggestions
for the scripts.

## Installation

To build and run these scripts, you will need to have the following software
installed on your machine:

- Java Development Kit (JDK) 11. The DreamBot team officially recommends
  [Temurin 11][2].
- An installation of the [DreamBot Client][3].
- The Maven build tool. You can install this using your system's package manager
  or by downloading it from the [Maven website][4].

To build the project, run the following command from the root directory:

```bash
mvn install
```

The command will compile all script modules and install their JAR files to
`$HOME/DreamBot/Scripts/`. You can now run the scripts from the client menu.

## Releases

You can downloaded the latest release of each script from the [releases
page][5].

[1]: https://dreambot.org
[2]: https://adoptium.net/temurin/releases?version=11
[3]: https://downloads.dreambot.org/launcher/Launcher.jar
[4]: https://maven.apache.org/download.cgi
[5]: https://github.com/ivan-guerra/merlin_dreambot_scripts/releases
