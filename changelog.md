Gelatin 1.1.0 has a small change to how DyeControls within the Library are handled and some minor fixes

### Features

- Player Action System [**NEW**]: New API used primarily to sync Key Presses to the server for use within Code that may be run on the server or the client.
- Change Dye Controls to be toggled based with a new Keybind using the Player Action System. Can be configured back to the other system based on holding crouch within the config
- Implement DyeStorage for Colored Storage Block Entity instead of hardcoded methods

### Fixes
- Add missing translations
- Fix missing Gelatin Creative Tab bug with owo-lib
- Fix Sodium compat mixin due to Sodium 5.0.0 changes
