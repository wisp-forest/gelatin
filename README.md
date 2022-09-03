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

# Content Features

### New Dye Functionality

1. Jello adds new ways to get and create Minecraft's many colorful blocks (Wool, Concrete, Non-Glazed Terracotta, etc)
    - You can now right-click with any dye to change the color of dyeable blocks in-world
    - The new *Dye Bundle*, which works similarly to a vanilla bundle, allows you to get up to 8 uses out of a single dye

### Cauldron Rework

2. Cauldrons now have parity with the Bedrock version of the game, allowing you to dye Leather Armor! Right-click with any dye to color the water, but note that only one color can be present at a time.
    - **Similar to the Dye Bundle above, Cauldrons allow you to get more out of a single dye. In fact, you can get 24 Dyed Blocks from a single piece of colorant!**

### Slime Block/Slabs

3. New colored Slime Block variants and as well as **Slime Block Slabs**! Both variants will stick to each other if they are the same color
    - The slab version, depending on its state, will stick to either the top or bottom block and function the same on its other four sides.

### Entity Dyeing

4. All living entities, or mobs if you will, can be dyed a certain color! It is mostly cosmetic and currently has no function but to be a cool little addition for players and being part of the API useful for mod creators

## APIs (this needs updating)

- An Entity Colorizing API: Such is a NBT tag based way of assigning differing DyeColor's, Custom Decimal Or hex based Color's and even the ablity to
  Rainbow-ify any entity that Implements the Living entity and its render.
    - Documention is included within the Mod.
- Experimental Gray-Scaling API: Such is used by the above Coloring API to get true color of the set Color value
    - More Documention will be added in the future.

## Future Plans and Todos

1. The current GrayScaling API is not as fleshed out as the Coloring API and in the future I hope to have the ability for someone to just register a EntityType, and then it will dynamically get and create the textures with the texture manager requiring no need of registering a gray scale version if your entity

2. Maybe add Slime stairs and custom sticky slime block based around a video by Mumbo Jumbo within the future (His ideas for slime slabs could prove quite difficult).
    - With this I might add a custom API for adding your own sticky block, but I digress and promise nothing yet.
