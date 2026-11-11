# Test notes

Every run below is reproducible: build, then feed the input file.

```bash
./run.sh demo/<file>.txt
```

The **Stage 3** section describes the files in `demo/` on this branch. The
Stage 1 and Stage 2 sections record the runs of those stages: to replay them,
check out the tag (`git checkout stage-1` or `stage-2`), where `demo/` holds
that stage's own input files. Files of the same name on this branch answer the
extra Stage 3 prompts and give the Stage 3 results listed below.

## Stage 1

### `demo/stage1-victory.txt`: Victory

The Waiter seats, takes orders and serves in arrival order; the Chef cooks each
dish as soon as it is ordered.

| Customer | Dish | Seated | Ordered | Served | Paid (end of turn) | Satisfaction |
| --- | --- | --- | --- | --- | --- | --- |
| C1 | Pasta  | 1  | 2  | 4  | 6  | 76 |
| C2 | Salad  | 5  | 6  | 7  | 9  | 76 |
| C3 | Salad  | 8  | 9  | 10 | 12 | 76 |
| C4 | Pasta  | 11 | 12 | 14 | 16 | 68 |
| C5 | Salad  | 13 | 15 | 16 | 18 | 76 |
| C6 | Burger | 17 | 18 | -  | -  | - |

Result: **VICTORY**, 5 paid (target 4), revenue $63.00 (target $45.00),
average satisfaction 74.4 (target 60).

### `demo/stage1-defeat.txt`: Defeat

The manager chooses Wait for every task. Nobody is seated, C1 leaves at the
end of turn 13 and C2 at the end of turn 16 (satisfaction reaches 0 after 13
waiting turns at 8 per turn). Result: **DEFEAT**, 0 paid, $0.00.

### Refused choices (typed by hand)

| Input on turn 1 | Message | Task used? |
| --- | --- | --- |
| Waiter 3 (serve) C1 | `C1 has no READY dish to serve.` | No |
| Waiter 2 (order) C1 | `C1 is not seated and waiting to order.` | No |
| Waiter 1 (seat) C9 | `There is no customer C9 in the restaurant.` | No |
| Chef 1 (prepare) C1 | `C1 has no order in preparation.` | No |
| Waiter 1 (seat) with both tables taken | `No table is free.` | No |

## Stage 2

Customer mix: C1 Regular, C2 VIP, C3 Critic, C4 Regular, C5 VIP, C6 Critic.

### `demo/stage2-victory.txt`: Victory, glowing review

The same choices as the Stage 1 victory run.

| Customer | Type | Dish | Bill | Served (turn) | Satisfaction when paid |
| --- | --- | --- | --- | --- | --- |
| C1 | Regular | Pasta | $18.00 | 4  | 76 |
| C2 | VIP     | Salad | $8.10  | 7  | 64 (12 per waiting turn) |
| C3 | Critic  | Salad | $9.00  | 10 | 85 (served at 70: glowing review +15) |
| C4 | Regular | Pasta | $18.00 | 14 | 68 |
| C5 | VIP     | Salad | $8.10  | 16 | 64 |
| C6 | Critic  | Burger | -     | -  | - |

Result: **VICTORY**, 5 paid, revenue $61.20, average satisfaction 71.4.

### `demo/stage2-critic-harsh.txt`: harsh review

The Waiter waits on turn 8 instead of seating C3. C3 is served on turn 11 at
satisfaction 60 (four waiting turns) and writes a harsh review: 60 -> 45.
Result: **VICTORY** by a narrow margin, 4 paid, $53.10, average satisfaction
61.3 (76, 64, 45, 60).

### `demo/stage1-defeat.txt` replayed in Stage 2

Every task is Wait. The VIP C2 leaves first (end of turn 12, 9 waiting turns
at 12), then C1 (end of turn 13) and the Critic C3 (end of turn 16, 10 turns at
10). Result: **DEFEAT**, 0 paid.

### Rounding

`Money.discount` rounds half up in whole cents: 10% off $9.05 = $8.145 ->
$8.15; 10% off $18.00 = $16.20; 100% off = $0.00.

## Stage 3

Each turn the game now asks, in order: Happy Hour (until it has been used),
Host, Waiter, Chef. The menu has four entries, so the seeded orders are, in the
order they are taken: Pasta, Salad, Pasta, Salad, Burger, Burger & Pasta Combo.

### `demo/stage3-victory.txt`: Victory with Host, Happy Hour and Combo

The Host seats every arrival on the turn they arrive; the Waiter takes the
order and the Chef starts cooking on that same turn. Happy Hour is activated on
turn 13.

| Customer | Type | Dish | Bill | Seated and ordered | Served | Paid (end of turn) | Satisfaction when paid |
| --- | --- | --- | --- | --- | --- | --- | --- |
| C1 | Regular | Pasta  | $18.00 | 1  | 3  | 5  | 84 |
| C2 | VIP     | Salad  | $8.10  | 4  | 5  | 7  | 88 |
| C3 | Critic  | Pasta  | $18.00 | 7  | 9  | 11 | 95 (served at 80: glowing review +15) |
| C4 | Regular | Salad  | $9.00  | 10 | 11 | 13 | 92 |
| C5 | VIP     | Burger | $10.80 | 13 | 14 | 16 | 88 |
| C6 | Critic  | Burger & Pasta Combo | $29.70 (unpaid) | 16 | - | - | - |

C5's bill shows both discounts: $15.00, 10% VIP -> $13.50, 20% Happy Hour ->
$10.80. C6's Combo needs 3 preparation units and becomes READY on turn 18, too
late to be served.

Result: **VICTORY**, 5 paid, revenue $63.90, average satisfaction 89.4.

### `demo/stage3-happy-hour.txt`: Happy Hour rescues the Critic's review

The same choices as `stage2-critic-harsh.txt` below, except that Happy Hour is
activated on turn 8 while the Critic C3 is still in the queue.

| Turn | What happens to C3 |
| --- | --- |
| 7 | Arrives; loses 10 at the end of the turn (90) |
| 8 | Happy Hour activated: 90 -> 100 (the +15 is capped at 100). Waiter seats C3. End of turn: 90 |
| 9 | Second Happy Hour turn. C3 is seated, so no recovery. Order taken: Pasta at 20% off, $14.40. End of turn: 80 |
| 10 | Chef finishes the Pasta. End of turn: 70 |
| 11 | Served at 70: glowing review, 70 -> 85 |

After turn 8 the Happy Hour prompt is no longer shown.

Result: **VICTORY**, 5 paid, revenue $63.00 ($18.00 + $8.10 + $14.40 + $9.00
+ $13.50), average satisfaction 73.0 (76, 64, 85, 76, 64). Compared with the
harsh-review run, Happy Hour costs $3.60 of revenue and gains 8 points of
average satisfaction.

### `demo/stage2-victory.txt` on Stage 3: Critic served first

The Host waits and Happy Hour is never activated, so only the Waiter seats.
The Waiter seats the Critic on turn 7 and takes the order on turn 8, leaving
the VIP C2 waiting for a Salad that is already READY.

| Customer | Type | Dish | Bill | Served (turn) | Satisfaction when paid |
| --- | --- | --- | --- | --- | --- |
| C1 | Regular | Pasta  | $18.00 | 4  | 76 |
| C2 | VIP     | Salad  | $8.10  | 9  | 40 (five waiting turns at 12) |
| C3 | Critic  | Pasta  | $18.00 | 10 | 85 (served at 70: glowing review +15) |
| C4 | Regular | Salad  | $9.00  | 14 | 68 |
| C5 | VIP     | Burger | $13.50 (unpaid) | 17 | - |
| C6 | Critic  | -      | -      | -  | - |

Result: **VICTORY**, 4 paid, revenue $53.10, average satisfaction 67.3.

### `demo/stage2-critic-harsh.txt` on Stage 3: harsh review

The Host waits and Happy Hour is never activated. The Waiter works strictly in
arrival order: C2 is served on turn 7 and the Critic is only seated on turn 8.

| Customer | Type | Dish | Bill | Served (turn) | Satisfaction when paid |
| --- | --- | --- | --- | --- | --- |
| C1 | Regular | Pasta  | $18.00 | 4  | 76 |
| C2 | VIP     | Salad  | $8.10  | 7  | 64 |
| C3 | Critic  | Pasta  | $18.00 | 11 | 45 (served at 60: harsh review -15) |
| C4 | Regular | Salad  | $9.00  | 13 | 76 |
| C5 | VIP     | Burger | $13.50 | 16 | 64 |
| C6 | Critic  | Burger & Pasta Combo | $29.70 (unpaid) | - | - |

Result: **VICTORY**, 5 paid, revenue $66.60, average satisfaction 65.0. One
more customer pays than in the Critic-first run, but the harsh review leaves
the average lower.

### `demo/stage1-defeat.txt` on Stage 3

Happy Hour is never activated and the Host, Waiter and Chef all wait every
turn. The VIP C2 leaves first (end of turn 12), then C1 (end of turn 13) and
the Critic C3 (end of turn 16). Result: **DEFEAT**, 0 paid, $0.00.

### Refused Stage 3 choices (typed by hand)

| Input | Message | Task used? |
| --- | --- | --- |
| Host 1 (seat) with an empty queue | `No customers are waiting.` | No |
| Host 1 (seat) with both tables taken | `Cannot seat customer: No tables are free.` | No |

### Rounding in Stage 3

`ComboMeal` (10% off) and `HappyHour` (20% off) work out their own discounts
and drop any fraction of a cent, unlike `Money.discount`, which rounds half up:
Combo 10% off $33.00 = $29.70; Happy Hour 20% off $13.50 = $10.80; Happy Hour
20% off a VIP Combo ($26.73) = $21.384 -> $21.38.
