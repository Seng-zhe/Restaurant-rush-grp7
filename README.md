# Restaurant Rush (CT2003 Group Assignment)

A turn-based restaurant management game played on the command line. The player is the manager and decides which customers and orders the Waiter and Chef handle each turn.

**Repository:** https://github.com/Seng-zhe/restaurant-rush
**Group:** CT2003 , Group 7
**Members:** LY(turn engine), Ryan (extensions), Goh Seng Zhe (design and integration)

## Requirements

- Java 17 or later (JDK, for `javac`)
- No network access needed

## Build and run

From the repository root:

```bash
# macOS / Linux
javac -d out $(find src -name "*.java")
java -cp out restaurantrush.Main
```

```powershell
# Windows (PowerShell)
javac -d out (Get-ChildItem -Recurse src -Filter *.java).FullName
java -cp out restaurantrush.Main
```

Or use the script: `./run.sh`

### Replaying a demo run

Every reproducible run is stored as an input file in `demo/`. Feed it to the game instead of typing:

```bash
java -cp out restaurantrush.Main < demo/stage1-victory.txt
```

## Project structure

```
src/restaurantrush/
  Main.java            entry point
  boundary/            ConsoleUI: input and display only, no game rules
  control/             GameController: runs the six turn steps
  entity/              Restaurant, Table, Customer types, Order, menu, staff
docs/
  class-diagram.puml   UML source (edit this, then re-render)
  class-diagram.png    rendered diagram
  test-notes.md        reproducible runs per stage
demo/                  input files for demo runs
```

## Stages

| Stage | Tag | Adds |
| --- | --- | --- |
| 1 | `stage-1` | Regular customers, menu, Waiter, Chef, Cashier, full turn sequence, victory and defeat |
| 2 | `stage-2` | VIP and Critic customers |
| 3 | `stage-3` | Host, ComboMeal, Happy Hour |

To run a stage: `git checkout stage-1`, then build and run as above.

## Stage comparisons

### Stage 1 → Stage 2

Compare link: https://github.com/Seng-zhe/restaurant-rush/compare/stage-1...stage-2

**New files**

| File | Purpose |
| --- | --- |
| `entity/VIPCustomer.java` | <fill in> |
| `entity/CriticCustomer.java` | <fill in> |

**Existing files changed or deleted**

| File | Change | Reason |
| --- | --- | --- |
| <file> | <what changed> | <why it was needed> |

### Stage 2 → Stage 3

Compare link: https://github.com/Seng-zhe/restaurant-rush/compare/stage-2...stage-3

**New files**

| File | Purpose |
| --- | --- |
| `entity/Host.java` | <fill in> |
| `entity/ComboMeal.java` | <fill in> |
| `entity/HappyHour.java` | <fill in> |

**Existing files changed or deleted**

| File | Change | Reason |
| --- | --- | --- |
| <file> | <what changed> | <why it was needed> |

## Documented rules

- **Customer food choice:** <describe the deterministic rule or seed>
- **Critic special behaviour:** <describe>
- **Combo choice rule:** <describe>
- **Rounding:** money is stored in integer cents; percentages round half up to the nearest cent.

## Additional features

<List any extra features here, with a one-line reason each shows good design.>
