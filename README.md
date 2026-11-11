# Restaurant Rush (CT2003 Group Assignment)

A turn-based restaurant management game played on the command line. The player is the manager and decides which customers and orders the Waiter and Chef handle each turn.

**Repository:** https://github.com/Seng-zhe/Restaurant-rush-grp7
**Group:** CT2003 , Group 7
**Members:** LY (turn engine), Ryan (extensions), Goh Seng Zhe (design and integration)

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

Compare link: https://github.com/Seng-zhe/Restaurant-rush-grp7/compare/stage-1...stage-2

**New files**

| File | Purpose |
| --- | --- |
| `entity/VIPCustomer.java` | Loses 12 satisfaction per waiting turn; 10% off every bill. Overrides `waitingPenalty()`, `discountPercent()`, `typeName()`. |
| `entity/CriticCustomer.java` | Loses 10 satisfaction per waiting turn; reviews the restaurant when served (see Critic rule below). Overrides `waitingPenalty()`, `onServed()`, `typeName()`. |
| `demo/stage2-victory.txt` | Stage 1 victory choices replayed with the Stage 2 customer mix: the Critic writes a glowing review. |
| `demo/stage2-critic-harsh.txt` | The Critic is left waiting one extra turn and writes a harsh review. |

**Existing files changed or deleted**

| File | Change | Reason |
| --- | --- | --- |
| `entity/Customer.java` | Added `discountPercent()` (default 0), `billFor(dish)`, and the `onServed(order, turn)` hook, called from `receiveDish()`, which now returns the reaction message. | Gives VIP and Critic a method to override, so no other class checks the customer type. |
| `entity/Waiter.java` | `takeOrder` locks `customer.billFor(dish)` instead of the menu price; `serve` adds the customer's reaction to its message. | The bill depends on the customer type; the player sees the Critic's review. |
| `entity/Money.java` | Added `discount(cents, percent)`, rounding half up in integer cents. | One shared, documented rounding rule for every percentage. |
| `control/ArrivalSchedule.java` | `createCustomer` picks each arrival's type from a fixed list: Regular, VIP, Critic, Regular, VIP, Critic. | Brings the new types into the game while keeping every run the same. |
| `docs/class-diagram.puml`, `docs/test-notes.md` | Updated for Stage 2. | Keep the documentation in step with the code. |

`GameController` and `ConsoleUI` are unchanged: the new types work through polymorphism alone.

### Stage 2 → Stage 3

Compare link: https://github.com/Seng-zhe/Restaurant-rush-grp7/compare/stage-2...stage-3

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

- **Customer food choice:** when the Waiter takes an order, the customer picks a dish with `java.util.Random` seeded with `42` (`OrderGenerator.SEED`). Every run is identical: the orders taken are Pasta, Salad, Salad, Pasta, Salad, Burger, in the order they are taken.
- **Turn sequence:** 1. the scheduled customer arrives (turns 1, 4, 7, 10, 13, 16). 2. Waiter: seat, take order, serve, or wait. 3. Chef: one preparation unit, or wait. 4. The Cashier charges one customer who became ready to pay on an earlier turn, then customers served on an earlier turn finish eating. 5. Customers who are WAITING, SEATED or ORDERED lose satisfaction (Regular: 8); at 0 they leave without paying. 6. After turn 18, victory needs 4+ paid customers, $45.00+ revenue and average satisfaction 60+.
- **Timing:** a dish served on turn *t* is eaten during turn *t*+1 and paid for at the end of turn *t*+2. Satisfaction is recorded when the customer pays.
- **Cashier:** works automatically but, like all staff, does one task per turn. If two customers can pay in the same turn, the one at the lower-numbered table pays first.
- **Refused choices:** an invalid choice (wrong customer, no free table, dish not READY) is explained and the manager chooses again; the staff task is not used up.
- **Customer mix (Stage 2):** arrivals C1 to C6 are Regular, VIP, Critic, Regular, VIP, Critic. Waiting penalties per turn: Regular 8, VIP 12, Critic 10.
- **VIP discount:** VIPs pay 10% less on every bill. The discount is applied when the order is taken and locked into the bill.
- **Critic special behaviour:** the Critic writes a review the moment their dish is served, judged on their satisfaction at that moment, which already counts every turn they waited. At **70 or more** (served after at most 3 waiting turns) the review is glowing and satisfaction goes **up 15** (max 100). Below 70 the review is harsh and satisfaction goes **down 15** (min 0). Served customers lose no more satisfaction, so the reviewed value is the one recorded when the Critic pays. This makes serving the Critic quickly a real priority.
- **Combo choice rule:** <describe>
- **Rounding:** money is stored in integer cents; percentages round half up to the nearest cent.

## Additional features

<List any extra features here, with a one-line reason each shows good design.>
