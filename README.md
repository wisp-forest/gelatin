# Gelatin

[![curseforge](https://img.shields.io/badge/-CurseForge-gray?style=for-the-badge&logo=curseforge&labelColor=orange)](https://www.curseforge.com/minecraft/mc-mods/jello)
[![modrinth](https://img.shields.io/badge/-modrinth-gray?style=for-the-badge&labelColor=green&labelWidth=15&logo=appveyor&logoColor=white)](https://modrinth.com/mod/jello)
[![release](https://img.shields.io/github/v/release/Dragon-Seeker/Jello?logo=github&style=for-the-badge)](https://github.com/Dragon-Seeker/Jello/releases)
[![discord](https://img.shields.io/discord/825828008644313089?label=wisp%20forest&logo=discord&logoColor=white&style=for-the-badge)](https://discord.gg/xrwHKktV2d)

## Overview

Gelatin's is Color Focused Library Mod useful for Mod creators who want to add new DyeColor's to minecraft without needing to do it themself. Most of Minecraft isn't designed for new Colors and can cause some issues:

- If you attempt to extend the DyeColor Enum, not only Load Order can mess with Color order but there is not syncing of the Color's with the server and client leading to desyncs

The Main Goal of gelatin is to implement the DyeColorant Registry all around minecraft allowing for any amount of Colors to be added by other mods without going thru hell to do it and possibly conflicting with other mods

**Build Setup:**

```grovvy
repositories {
    maven { url 'https://maven.wispforest.io' }
}
```

```groovy
dependencies {
    // Recommend creating a Gelatin version within your gradle.properties 
    modImplementation "io.wispforest.gelatin:gelatin:${project.gelatin_version}"
    
    // You can also target specfic modules to use if you only need one
    modImplementation "io.wispforest.gelatin:(moudle name here):${project.jello_version}"
}
```
