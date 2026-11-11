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
java -cp out restaurantrush.Main < demo/stage3-victory.txt
```

| File | Run |
| --- | --- |
| `demo/stage3-victory.txt` | Host seats every arrival, Happy Hour on turn 13, Combo ordered: Victory |
| `demo/stage3-happy-hour.txt` | Happy Hour rescues the Critic's review: Victory |
| `demo/stage2-victory.txt` | Waiter-only, Critic served first, glowing review: Victory |
| `demo/stage2-critic-harsh.txt` | Waiter-only, arrival order, harsh review: Victory with a lower average |
| `demo/stage1-defeat.txt` | Everyone waits every turn: Defeat |

The files on this branch answer the Stage 3 prompts (Happy Hour, Host, Waiter, Chef). The `stage1-` and `stage2-` files replay those scenarios on the Stage 3 game; to replay a run exactly as it was in an earlier stage, check out that stage's tag, where `demo/` holds that stage's own input files. Expected results are in `docs/test-notes.md`.

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
| `entity/Host.java` | A new `StaffMember` who seats the first customer in the waiting queue at the first free table. Has its own one task per turn, separate from the Waiter's. |
| `entity/ComboMeal.java` | An `Orderable` built from two other `Orderable` items: price is the two prices added, less 10%; preparation units are the two added. |
| `entity/HappyHour.java` | Tracks the once-per-game Happy Hour: whether it has been used, how many turns are left, and the 20% discount. |
| `entity/VictoryChecker.java` | Holds the three victory targets and checks them, so the targets are no longer spread across `Restaurant` and `ConsoleUI`. |
| `demo/stage3-victory.txt` | Host, Happy Hour and Combo used in one winning run. |
| `demo/stage3-happy-hour.txt` | Happy Hour turns the Critic's harsh review into a glowing one. |

**Existing files changed or deleted**

| File | Change | Reason |
| --- | --- | --- |
| `control/GameController.java` | Added `hostSeat()`, `hostWait()`, `activateHappyHour()`, `getHappyHourTurnsLeft()` and `isHappyHourUsed()`. The Host joins the staff list; `startTurn()` applies the Happy Hour recovery and `endTurn()` counts Happy Hour down. `isVictory()` now asks `VictoryChecker`. | The new staff member and the new player choice need controller methods for the UI to call. |
| `boundary/ConsoleUI.java` | Each turn now asks for Happy Hour (until it has been used) and the Host before the Waiter and Chef; shows the menu, with combos marked, and the Happy Hour turns left. | The manager must be able to make the two new choices and see what is on the menu. |
| `entity/Customer.java` | `billFor(dish, happyHour)` applies the Happy Hour discount after the customer's own discount; added `recoverSatisfaction()`; `applyWaitingLoss` takes the `HappyHour`. | Happy Hour changes the bill and restores satisfaction to queueing customers. |
| `entity/Waiter.java` | `takeOrder` takes the `HappyHour` and passes it to `billFor`. | The bill locked into the order depends on whether Happy Hour is active. |
| `entity/Menu.java` | Adds the Burger & Pasta Combo to the menu; `addComboMeal()` enabled. | Customers can now choose a combo. |
| `entity/Restaurant.java` | Owns the `HappyHour`; victory targets moved out to `VictoryChecker`. | One shared Happy Hour state for the controller, Waiter and customers. |
| `demo/*.txt` | Regenerated with the Happy Hour and Host answers on every turn. | The Stage 2 input files no longer lined up with the Stage 3 prompts. |
| `docs/test-notes.md` | Added the Stage 3 runs. | Keep the documentation in step with the code. |

`Order`, `Chef`, `Cashier`, `ArrivalSchedule` and the three customer subclasses are unchanged: a `ComboMeal` is cooked, served and paid for through the `Orderable` interface like any other dish.

## Documented rules

- **Customer food choice:** when the Waiter takes an order, the customer picks a dish with `java.util.Random` seeded with `42` (`OrderGenerator.SEED`). Every run is identical: with the Stage 3 menu the orders taken are Pasta, Salad, Pasta, Salad, Burger, Burger & Pasta Combo, in the order they are taken. (With the three-dish menu of Stages 1 and 2 the sequence was Pasta, Salad, Salad, Pasta, Salad, Burger.)
- **Turn sequence:** 1. the scheduled customer arrives (turns 1, 4, 7, 10, 13, 16). 2. The manager may activate Happy Hour (asked until it has been used); Host: seat the first waiting customer, or wait; Waiter: seat, take order, serve, or wait. 3. Chef: one preparation unit, or wait. 4. The Cashier charges one customer who became ready to pay on an earlier turn, then customers served on an earlier turn finish eating. 5. Customers who are WAITING, SEATED or ORDERED lose satisfaction (Regular: 8); at 0 they leave without paying. 6. After turn 18, victory needs 4+ paid customers, $45.00+ revenue and average satisfaction 60+.
- **Timing:** a dish served on turn *t* is eaten during turn *t*+1 and paid for at the end of turn *t*+2. Satisfaction is recorded when the customer pays.
- **Cashier:** works automatically but, like all staff, does one task per turn. If two customers can pay in the same turn, the one at the lower-numbered table pays first.
- **Refused choices:** an invalid choice (wrong customer, no free table, dish not READY) is explained and the manager chooses again; the staff task is not used up.
- **Customer mix (Stage 2):** arrivals C1 to C6 are Regular, VIP, Critic, Regular, VIP, Critic. Waiting penalties per turn: Regular 8, VIP 12, Critic 10.
- **VIP discount:** VIPs pay 10% less on every bill. The discount is applied when the order is taken and locked into the bill.
- **Critic special behaviour:** the Critic writes a review the moment their dish is served, judged on their satisfaction at that moment, which already counts every turn they waited. At **70 or more** (served after at most 3 waiting turns) the review is glowing and satisfaction goes **up 15** (max 100). Below 70 the review is harsh and satisfaction goes **down 15** (min 0). Served customers lose no more satisfaction, so the reviewed value is the one recorded when the Critic pays. This makes serving the Critic quickly a real priority.
- **Host (Stage 3):** the Host has one task per turn, separate from the Waiter's, and is asked before the Waiter. The Host always seats the customer at the front of the queue at the first free table; the Waiter can still seat any waiting customer. A customer seated by the Host can have their order taken by the Waiter on the same turn.
- **Happy Hour (Stage 3):** the manager can activate Happy Hour once per game, at the start of a turn. It lasts for that turn and the next one. Every order taken while it is active is **20% off**, applied after the customer's own discount (a VIP Burger: $15.00 → $13.50 → $10.80) and locked into the bill. At the moment it is activated, and again at the start of its second turn, every customer still in the waiting queue recovers **15** satisfaction (max 100). The normal end-of-turn waiting loss still applies.
- **Combo choice rule (Stage 3):** the menu has one combo, the **Burger & Pasta Combo**: the two prices added, less 10% ($15.00 + $18.00 = $33.00 → **$29.70**), and the two preparation times added (1 + 2 = **3 units**). The combo is a fourth entry on the menu and customers choose it by the same seeded pick as any dish, each of the four entries being equally likely. With seed 42 the sixth order taken is the combo. VIP and Happy Hour discounts apply on top of the combo price.
- **Rounding:** money is stored in integer cents. The VIP discount (`Money.discount`) rounds half up to the nearest cent. The Combo and Happy Hour discounts are worked out in `ComboMeal` and `HappyHour` and drop any fraction of a cent (round down).

## Additional features

- **Annotated demo files:** `InputReader` skips lines starting with `#` and echoes piped input, so each demo file explains every turn and its replay reads like a live game. Keeps test evidence readable without changing any game code.
- **Read-only snapshots:** the UI is given `GameSnapshot`, `CustomerView` and `TableView` records instead of the entities, so the boundary layer cannot change game state.
- **Refused choices cost nothing:** every staff method checks the rules first and returns an `ActionResult`; a refused choice is explained and asked again without using the staff member's task.
- **`VictoryChecker`:** the victory targets live in one class, used by both the controller and the welcome and result screens, so the numbers shown always match the numbers checked.
