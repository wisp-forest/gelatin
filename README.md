# Jello

[![curseforge](https://img.shields.io/badge/-CurseForge-gray?style=for-the-badge&logo=curseforge&labelColor=orange)](https://www.curseforge.com/minecraft/mc-mods/jello)
[![modrinth](https://img.shields.io/badge/-modrinth-gray?style=for-the-badge&labelColor=green&labelWidth=15&logo=appveyor&logoColor=white)](https://modrinth.com/mod/jello)
[![release](https://img.shields.io/github/v/release/Dragon-Seeker/Jello?logo=github&style=for-the-badge)](https://github.com/Dragon-Seeker/Jello/releases)
[![discord](https://img.shields.io/discord/825828008644313089?label=wisp%20forest&logo=discord&logoColor=white&style=for-the-badge)](https://discord.gg/xrwHKktV2d)

## Overview
Jello is a Mainly a Mod that makes handling Minecrafts color System easy for Other modders or just to add Quirky new ways of Coloring Blocks, Items and Entitys. Within contains some new Content to make using Jello easier for Players and API's for Mod creators looking for a easy solution for hardcoded varaiables within Mojang's code.

**Build Setup:**

```grovvy
repositories {
    maven { url 'https://maven.wispforest.io' }
}
```
```groovy
dependencies {
  // Recommend creating a Jello version within your gradle.properties 
  modImplementation "io.wispforest:jello:${project.jello_version}"
}
```

# Content Features

### New Dye Functionality 
 1. Jello adds many new ways to get and Create mincrafts many Colorful Blocks(Wool, Concrete, Non-Glazed Terracotta, etc).
    - You can now Right Click with any Dye Item within Minecraft or Created by Jello to Color the variase Block Variants.
    - It is now possible to do this with the Dye Bundle, it works similar to the regular Bundle but only stores Dyes allowing you Dye upto 8 blocks using one color before a Dye will be consumed allowing for the ability to change your concrete house to whatever block you want!

### Cauldron Rework
2. Cauldrons now have parity with the Bedrock Version of the Game allowing for Dyeing of All Leather Armor Types! Right Click with any Dye to Color the water, only one Color can be present at a time.
    - **Similar to the Block Coloring above with Dyes, you can also do such within Cauldrons too! You can get 24 Dyed Blocks from one Dye!**

### Slime Block/Slabs
 3. Adds new Colored Slime Block Varaints and A new slime block slab! Both variants will stick to each other if there the same color.
    - The Slab version depending on its state will stick to either the top or bottom block and function the same on its other four sides.

 ### Entity Dyeing
 4. All Living Entity's or Mobs can be dyed to a certain color! It is mostly cosmetic and currently have no fuction but to be a cool little edition for Players and is part of the API useful for Mod Creators

## Api's
  **TODO: REDO API README SECTION AFTER FINISHING NEWLY ADDED DYE COLOR API AND REGISTRY
  - A Entity Colorizing API: Such is a NBT tag based way of assigning differing DyeColor's, Custom Decimal Or hex based Color's and even the ablity to Rainbow-ify any entity that Implements the Living entity and its render.
    - Documention is included within the Mod. 
  - Experimental Gray-Scaling API: Such is used by the above Coloring API to get true color of the set Color value
    - More Documention will be added in the future.
    - 
## Future Plans and Todo's
   1. The current GrayScaling API is not as fleshed out as the Coloring API and in the future I hope to have the ablity for someone to just register a EntityType and then it will dynamically get and create the texture's 
      with the teuxture manager requiring no need of registering a gray scale version if your entity
   2. Maybe add Slime stairs and custom sticky slime block based around a video made By Mumbo Jumbo within the future (His ideas for slime slabs maybe quite difficult).
      - With this I might add a custom API for adding your own sticky block but I digress and promise nothing yet.

