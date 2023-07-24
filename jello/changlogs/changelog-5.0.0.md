Jello 5.0.0 is a Major release including the separation of the Mods underline API into its own mod to allow for better mod inclusion and implmentation.

### Features: 
- Rework to DyeBundle with the inclusion of new way of interacting with the contents of the bundle 
- Rework to how the new DyeColors are loaded by jello allowing for easier revisions to the list of colors.
- Color Deduplication: This was done due to how many colors (Roughly 500) were only off by 1-5 units away from each other in color space. I have a system inplace to properly remap these colors to the chose alternative meaning there should be no problems upgrading the world. This means that the amount of colors is around 1300 now instead of 1800.
- **[NEW]** Jello Cups: These exist for a while within the creative screen but were not really finished. They act like stackable potions for Vanilla Status Effects meaning that you can stack them up to 16 but the downside is that you will need eat more often than a potion. Some even slightly better duration depending on the crafting item used. Info about making them can be found using REI.
- **[NEW]** Dragon Health: Similar to Absorption but are permanent for the Potions duration. You need Condensed Dragons Breath to brew such a potion require both Ghast tear and 4 Dragons breath

### Fixes:
- Fix bug with sorting entries within condensed creative