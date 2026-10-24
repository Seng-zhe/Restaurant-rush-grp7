# Test notes

Each run uses the base setting (18 turns, 2 tables, 1 Waiter, 1 Chef, 1 Cashier, arrivals on turns 1, 4, 7, 10, 13 and 16) unless stated. Every run has an input file in `demo/` so anyone can reproduce it.

## Stage 1 (`stage-1`)

### Victory run

- **Input file:** `demo/stage1-victory.txt`
- **Seed / food-choice rule:** <fill in>
- **Final result:** paid customers <n>, revenue $<x>, average satisfaction <y> → Victory
- **Key decisions:** <e.g. turn 4: served customer 1 instead of seating customer 2 because...>

### Defeat run

- **Input file:** `demo/stage1-defeat.txt`
- **Final result:** paid customers <n>, revenue $<x>, average satisfaction <y or N/A> → Defeat
- **Why it fails:** <which target was missed>

### Rule checks

| Check | Expected | Result |
| --- | --- | --- |
| Brief's Pasta example (Section 3.3) | 92, 84, 76, served turn 4, paid $18.00 on turn 6 | |
| Dish finished this turn cannot be served this turn | Error message, Waiter task not used | |
| Invalid menu choice | Error message, re-prompt, task not used | |
| Customer reaches 0 satisfaction | Leaves, order cancelled, table freed, no revenue | |
| No one paid yet | Average shown as N/A | |
| Turn 16 customer | Cannot pay by turn 18 | |

## Stage 2 (`stage-2`)

- **Input file:** `demo/stage2-run.txt` (includes at least one VIP and one Critic; six arrivals)

| Check | Expected | Result |
| --- | --- | --- |
| VIP loses 5 per waiting turn | | |
| VIP bill 10% off | | |
| Critic loses 12 per waiting turn | | |
| Critic special behaviour | <describe> | |
| Stage 1 runs still give the same results | | |

## Stage 3 (`stage-3`)

- **Input file:** `demo/stage3-run.txt`

| Check | Expected | Result |
| --- | --- | --- |
| Salad + Pasta combo | $24.30, 3 units | |
| VIP Burger during Happy Hour | $10.80 | |
| Happy Hour lasts two turns, once per game | | |
| Waiting customers recover 15 during Happy Hour (max 100) | | |
| Host seats one extra customer per turn | | |
| Orders placed before Happy Hour keep their price | | |
