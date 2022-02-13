# Jello

[![discord](https://img.shields.io/discord/825828008644313089?label=wisp%20forest&logo=discord&logoColor=white&style=for-the-badge)](https://discord.gg/xrwHKktV2d)
## Overview
A Library mod that adds some useful entity coloring API's and new Blocks & Features based around Color! Its main purpose is to be included with my Upcoming Slime Friend's mod.


## Features
 1. Currently Jello has custom Dye Coloring Event for Coloring minecrafts differing colored blocks(Wool, Concrete, Non-Glazed Terracotta, etc) with Dye Items.
 2. Adds new Colored Slime Block Varaints and A new slime block slab!
    - These blocks will be covered more when Docs are made but Slabs and Blocks of the same color will stick to each other but will not stick to differing colored slime blocks.
    - Also the slabs have differing interactions due to the half-block nature of such so it can allow for some compact redstone machines!
 3. By default, all minecraft mobs can be Dyed a color using the Dye Color Entity Event made for the API mentioned below
    - Future plans will allow for blacklisting certain mobs or mods by developers and modpack creators.

## Api's
  - A Entity Colorizing API: Such is a NBT tag based way of assigning differing DyeColor's, Custom Decimal Or hex based Color's and even the ablity to Rainbow-ify any entity that Implements the Living entity and its render.
    - Documention is included within the Mod. 
  - Experimental Gray-Scaling API: Such is used by the above Coloring API to get true color of the set Color value
    - More Documention will be added in the future.

## Setup
```grovvy
repositories {
    TODO: Work In Progress
}
```
```groovy
dependencies {
  // Recommend creating a jello version within your gradle.properties 
  modImplementation "com.dragon:jello:${project.jello_version}"
}
```

## Future Plans and Todo's
   1. The current GrayScaling API is not as fleshed out as the Coloring API and in the future I hope to have the ablity for someone to just register a EntityType and then it will dynamically get and create the texture's 
      with the teuxture manager requiring no need of registering a gray scale version if your entity
   2. My hope is to add the ability to color almost any block without the need for creating a new texture for each color allowing for endless possilbitys
   3. Maybe add Slime stairs and custom sticky slime block based around a video made By Mumbo Jumbo within the future (His ideas for slime slabs maybe quite difficult).
      - With this I might add a custom API for adding your own sticky block but I digress and promise nothing yet.

