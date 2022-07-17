Jello 4.1.0 adds two big fixes for Item count on DyeableBlockItemVariants and resolve issues with the Main variant crafting recipe

##### Bug Fixes:
- Fix for Item count for DyeableBlockVariants being set to 1 due to vanilla logic issues in the base Item.Settings
- Fixes for the main Dyeing Variant Crafting Recipe:
  - Fix issues with using the Wrong Dye Tag
  - Fix issues with crafting logic not matching certain edge cases

##### API Changes:
- Change to how the override setting when registering Dyes to the DyeableVariantManager to only get the ItemGroup and Tab index if an OwoItemSettings