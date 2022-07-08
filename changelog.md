Finally Jello has been updated to 1.19, sorry for the late update but comes with it a lot of
minor API changes, mod compatibility with REI and Condensed Creative, and lots of bug fixes.

####Features:
- REI Support:
  - Recipe support for recipes pertaining to the Artist Palette, Custom Dyed Blocks and there Dye item.
  - Use Condensable Entries to cut down on the number of pages that jello adds.
- Condensed Entries Support:
  - Clean up Jellos new Item Group tab where all the Block/Item variants are now stored.


#####Bug Fixes:
- Implement Mixin Plugin for Mixins used for Mod Compatibility.
- Fix Artist Palette Recipe allowing any Item to be used
- Fix Cauldron rendering without a bottom due to Mojang's Json Model culling the bottom due to water typically being Opaque
- Fix issue with sodium rendering Cauldron Water incorrectly

####API Changes:
- Split DyeableBlockVariant into DyeableItemVariant now allowing for just Item Variants to be made, with 
the inclusion of the new Dye Item Variant
    - This update has caused a lot of changes within the API and may still be broken with some JavaDocs being incorrect.
- All Variants now contain a builder to hold the methods needed to create the variants to remove clutter of
methods that will only be used on creation
- [ NEW ]: LootTableInjectionEvent allows for someone to add new loot Tables during runtime of the game. 
- [ NEW ]: TranslationInjectionEvent allows for someone to add new translations during runtime of the game.
- Minor Change to the LanguageProvider for more internal use within the TranslationInjectionEvent
- Implement a [Library](https://github.com/atteo/evo-inflector) for Algorithmic Pluralization of words.
- Minor shuffle of classes and methods within some internal classes

**Both new Events only allow for adding and not removal/replacement of the existing Data*