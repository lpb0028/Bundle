# Bundle Changelog
> This document created after the making of Bundle v3.
> Updated to better naming conventions 7/12/25

## Bundle v0.1:
> Created FIRST
- `GridView` and `Entity` are classes that draw on `Canvas sharedCanvas`, which `CanvasView` then draws in `onDraw()`. `CanvasView` is added to `WindowManager` as `TYPE_APPLICATION_OVERLAY`, with .8 alpha in order to allow touches through to lower content.

- Current Window Layout:  
```
WindowManager Window
  └─ CanvasView
```

## Bundle v0.2 (later changed to v1.0):  
> Created SECOND
- `GridView` and `Entity` changed to views, instances added as children of main_activity.xml's `FrameLayout`, which is added as a `FLAG_NOT_TOUCHABLE` overlay. Entities move with `setX()` and `setY()`, setting positions within the `FrameLayout`.
- Current Window Layout:
```
WindowManager Window
  └─ FrameLayout  
    ├─ Entity  
    ├─ Grid  
    └─ etc.  
```

## Bundle v0.3:
> Created THIRD
- `GridView` and `Entity` now independent of `FrameLayout`, each added to `WindowManager` as individual windows. `GridView` untouchable, entity touchable. First version to acheive correct touch format, very slow due to expensive window update calls to move entities and grids.

- Window Layout:
```
WindowManager Window
  ├─ Entity
  ├─ Grid
  └─ etc.
```

## Bundle v1.1:
> Created 8/7/24
- GOALS: 
Implement touch targets as a workable alternative to version 3
- CHANGES:
  - Implemented touch targets as TouchBox (extends View) class
  - Set TouchBoxes to update position every 15 frames (4x a second)
  - Added bundle app image
  - Changed changelog format to add headers and cleaner visuals
- PROBLEMS: 
  - Orientation change not handled
  - TouchBoxes updating can cause movement to be jittery
- NEXT STEPS:
  - Create inventory system, manage state saving
  - Create in-app layout, menus, etc.
  - Create bundle, bundle layout
  - Manage interactions with picked-up items
  - Manage interactions with entities

- Window Layout:
```
WindowManager Window
  ├─ FrameLayout
    ├─ Entity
    ├─ Grid
    └─ etc.
  ├─ TouchBox
  └─ etc.
```

## Bundle v1.2:
> Created 8/13/24
- GOALS: 
  - Create an in-app interface to launch adventure mode
  - Ensure one adventure instance at a time
- PROBLEMS: 
  - Static Views cause memory leaks
  - In-app interface empty, no content to fill it with yet
  - Crashes on restarting adventure after open app, start adventure+close app, open app again
- CHANGES:
  - Created a basic in-app interface, bottom menu buttons switch displayed fragment
  - Bottom buttons change to indicate selected menu
- NEXT STEPS:
  - Fix restart crash
  - Add blocks to adventure mode
  - Handle entity and block touches

## Bundle v1.3:
> Created 8/19/24
- GOALS:
  - Fix restart crash
  - Handle Entity touches
  - Continue filling out home fragment
- PROBLEMS:
  - Inventory Interaction between service and activity sketchy
  - Enemies glitch out on hit
- CHANGES:
  - Patched restart crash
  - Created an inventory system, added icons and ItemDictionary used to staticly get items, ids, and files
  - Added hurt animation for entities on touch

## Bundle v1.4: 
> Created 1/4/25
- GOALS:
  - Slim down codebase, remove spaghetti code and streamline program interaction
  - Debug and fix/workaround service and activity interaction
  - Fix entity hit physics glitch
- CHANGES:
  - Refactored and annotated all code for simplicity
  - Created Manager script to have control over and offer references to all scripts
  - Patched enemy hit glitch
- PROBLEMS:
  - Hitbox positioning bug related to entity position, frame bounds?
  - Enemies unable to despawn?
  - Enemies spawn too frequently
  - Entity hit patch is a bandaid, real physics are needed for real fix.
- NEXT STEPS:
  - Replace physics animations with actual physics, handle collision for enemy feet
  - Debug hitbox positioning issue
  - Debug inventory service issues

## Bundle v1.5:
> Created 1/7/25
- GOALS:
  - Debug hitbox positioning issue
  - Implement vertical physics for enemies jumping, falling, and getting hit
  - Debug service-inventory interaction issues, toasts
- CHANGES:
  - Fixed hitbox positioning issue
  - Added optional DebugScript to aid debugging and learning while developing
  - Implemented basic vertical physics
  - Added custom toast layout, fixed ToastManager completely
- PROBLEMS:
  - Untouchable container overlay for entities is no longer untouchable and I can't figure out why
- NEXT STEPS:
  - Fix broken overlay
- NOTES:
  - Discovered WindowManager-added views (windows) cannot go beyond the extent of the screen, they instead cling to the closest edge. This should be fine because entities only touch the edges of the screen upon entering and leaving.
  - The vertical physics work really well, but can always be tweaked to be better, smoother, or snappier in settings.
  - In trying to debug and patch service-inventory interaction issues with toasts, I discovered that toasts are only designed to work within an application, and can't show outside that. I'll fix this by implementing my own basic toast view and displaying it like I display entities (outside of the activity).

## Bundle v1.5.1:
> Created 1/12/25
- GOALS:
  - Fix broken overlay by sequential implementation of previous features - Hitbox positioning patch, vertical physics, toast system
- CHANGES:
  - Reimplemented hitbox positioning patch
  - Moved getPermissions to run on start (fixed crash)
  - Implemented basic vertical physics for entities
- PROBLEMS:
  - Having one frame active is fine, but having both is not. I'll try having just one overarching frame.
- NEXT STEPS:
  - Implement Manager-controlled general frame as a parent to adventureFrame and toastFrame.
- NOTES:
  - In version 2.5, I implemented several features at once and somehow screwed up the entity display fundamental to the program, and couldn't figure out how to fix it. I'll try to remedy this by reverting to version 2.4 and adding the features added in 2.5 with vigorous testing to ensure that the overlay system is never messed up.
  - Both Adventure frame and Toast frame have the same same layouts and use the same parameters, instantiating one in the place of the other (totaling 2 frames) causes touchable.

## Bundle v1.5.2:
> Created 2/5/25
- GOALS:
  - Implement Manager-controlled general frame as a possible solution for frame touchable issues.
- CHANGES:
  - Implemented a general frame in manager which both the entity and toast frames add as children to.
  - Added a static notification associated with the service, working buttons "Clear" and "Stop Adventuring" used for associated functions.
- PROBLEMS:
  - In-app is boring
- NEXT STEPS:
  - Improve in-app ui 

## Bundle v1.6:
> Created 2/5/25
- GOALS:
  - Improve in-app ui on home, inventory screen
  - Extensively test/debug current inventory function
- CHANGES:
  - Imported item/block textures from minecraft, scaled all to double size
  - Added new home menu UI
- NEXT STEPS:
  - Support for block grids
  - Implement build menu UI?
  - Improve inventory menu UI

## Bundle 1.7:
> Created 2/8/25
- GOALS:
  - Implement block grids
  - Use block grids to implement basic build menu UI
  - Improve inventory menu UI
- CHANGES:
  - Added inventory item sprites and scaled all image resources to 128x128
  - Implemented block grids as GridLayouts with helper class BlockGrid
  - Renamed GridViews to EntityGridViews to not clash with layout GridViews
  - Created CloudView, renders 3d-ish clouds based on floating parameters
- PROBLEMS:
  - CloudView paramaters need to be tweaked
- NEXT STEPS:
  - Tweak CloudView params
  - Add BlockGrids to Build Fragment and background of home
  - Look into entity anims and framedata

## Bundle 1.8:
> Created 2/27/25
- GOALS:
  - Create SimulationView to replace OverlayView - abstracting to work in both overlay and activity.
- CHANGES:
  - Created BundleService, a Service replacing the service portion of OverlayView.
  - Created SimulationLayout, a FrameLayout replacing the gameplay portion of OverlayView. Tested, this also works without any service attached.
  - Added a SimulationLayout to BuildFragment, works perfectly.
  - Added more verbose logging for script interactions.
- PROBLEMS:
  - Entities in build menu ignore blocks - need more collision detection and need-ground toggle
- NEXT STEPS:
  - Add block spawns to SimulationLayout
  - Add more/better entity collision detection
  - Add entity- and block-specific spawn toggles to SimulationLayout (remove from Settings)
  - Add need-ground toggle for entities in SimulationLayout
  - Add more logging toggles in Settings
- NOTES:
  - I was originally planning some ui updates this version, but I realized for the build menu to work as I want it to, I need to create like a smaller version of the entity-simulation view used in the overlay. The easiest way to do this is to reformat that as a view (or a layout), scalable to whatever size I need, the whole screen (as a child of transparentlayer) or just a small subsection. Once I do this, I can also have it simulate entities in the build menu and any entities I want in the home menu. Entities and hitboxes are handled by SimulationView and WindowManager, while the layout is kept alive by BundleService.

- Current Overlay:
```
[Screen Content]
  └─ WindowManager
    ├─ Active Hitboxes
    └─ TransparentFrame - Manager
      ├─ SimulationLayout
        ├─ Entities
        └─ Grids
      └─ ToastFrame
```

## Bundle 1.9:
> Created 4/1/25 - IN PROGRESS
- GOALS:
  - Add features to SimulationLayout:
    - Block Spawns
    - Entity-Block Collisions
    - Entity/Block Spawn Toggles
    - Entity Need-Ground Toggle
  - Add more logging toggles in Settings
- CHANGES:
  - Changed BlockGrid from a helper class to an object class, added loadFromResources() to support old usages.
  - Added functions to BlockGrid:
    - SetBlock
    - RemoveBlock
    - GetBlock
    - DistToBlock
    - FillAll
    - Fill
    - FillAsResources
  - Moved BlockGrid initial loading off the main thread to reduce activity freezing.
  - Added more verbose BlockGrid logging.
  - Added EstablishSize function to SimulationLayout - called on first Layout call so that BlockGrid doesn't initialize when size is (0,0).
  - Tested block placement functions, demo'd structure placement with FillAsResources.
- PROBLEMS:
- NEXT STEPS:
