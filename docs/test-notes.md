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
Waiting penalties per turn: Regular 8, VIP 5, Critic 12. The Critic needs to be
served after at most two waiting turns (100 - 2 x 12 = 76) for a glowing
review; after three (64) the review is harsh.

### `demo/stage2-victory.txt`: Victory, glowing review

On turn 7 the Waiter seats the Critic C3 instead of serving the VIP C2, whose
Salad is already READY. The VIP loses only 5 per turn, so the wait costs little.

| Customer | Type | Dish | Bill | Served (turn) | Satisfaction when paid |
| --- | --- | --- | --- | --- | --- |
| C1 | Regular | Pasta | $18.00 | 4  | 76 |
| C2 | VIP     | Salad | $8.10  | 10 | 70 (six waiting turns at 5) |
| C3 | Critic  | Salad | $9.00  | 9  | 91 (served at 76: glowing review +15) |
| C4 | Regular | Pasta | $18.00 | 15 | 60 |
| C5 | VIP     | Salad | $8.10 (unpaid) | 17 | - |
| C6 | Critic  | -     | -      | -  | - |

Result: **VICTORY**, 4 paid, revenue $53.10, average satisfaction 74.3
(74.25).

### `demo/stage2-critic-harsh.txt`: harsh review

The same choices as the Stage 1 victory run, in strict arrival order. C2 is
served on turn 7 and the Critic is only seated on turn 8.

| Customer | Type | Dish | Bill | Served (turn) | Satisfaction when paid |
| --- | --- | --- | --- | --- | --- |
| C1 | Regular | Pasta | $18.00 | 4  | 76 |
| C2 | VIP     | Salad | $8.10  | 7  | 85 |
| C3 | Critic  | Salad | $9.00  | 10 | 49 (served at 64: harsh review -15) |
| C4 | Regular | Pasta | $18.00 | 14 | 68 |
| C5 | VIP     | Salad | $8.10  | 16 | 85 |
| C6 | Critic  | Burger | -     | -  | - |

Result: **VICTORY**, 5 paid, revenue $61.20, average satisfaction 72.6. One
more customer pays than in the glowing-review run, but the Critic leaves at 49
and the average is lower.

### `demo/stage1-defeat.txt` replayed in Stage 2

Every task is Wait. C1 leaves at the end of turn 13 (13 waiting turns at 8) and
the Critic C3 at the end of turn 15 (9 waiting turns at 12). The VIPs lose only
5 per turn and are still queueing at the end. Result: **DEFEAT**, 0 paid.

### Rounding

`Money.discount` rounds half up in whole cents: 10% off $9.05 = $8.145 ->
$8.15; 10% off $18.00 = $16.20; 100% off = $0.00.

## Stage 3

Each turn the game now asks, in order: Happy Hour (until it has been used),
Host, Waiter, Chef. The menu has four entries, so the seeded orders are, in the
order they are taken: Pasta, Salad, Pasta, Salad, Burger, Burger & Pasta Combo.

The waiting penalties are unchanged from Stage 2 (Regular 8, VIP 5, Critic 12).

### `demo/stage3-victory.txt`: Victory with Host, Happy Hour and Combo

The Host seats every arrival on the turn they arrive; the Waiter takes the
order and the Chef starts cooking on that same turn. Happy Hour is activated on
turn 13.

| Customer | Type | Dish | Bill | Seated and ordered | Served | Paid (end of turn) | Satisfaction when paid |
| --- | --- | --- | --- | --- | --- | --- | --- |
| C1 | Regular | Pasta  | $18.00 | 1  | 3  | 5  | 84 |
| C2 | VIP     | Salad  | $8.10  | 4  | 5  | 7  | 95 |
| C3 | Critic  | Pasta  | $18.00 | 7  | 9  | 11 | 91 (served at 76: glowing review +15) |
| C4 | Regular | Salad  | $9.00  | 10 | 11 | 13 | 92 |
| C5 | VIP     | Burger | $10.80 | 13 | 14 | 16 | 95 |
| C6 | Critic  | Burger & Pasta Combo | $29.70 (unpaid) | 16 | - | - | - |

C5's bill shows both discounts: $15.00, 10% VIP -> $13.50, 20% Happy Hour ->
$10.80. C6's Combo needs 3 preparation units and becomes READY on turn 18, too
late to be served.

Result: **VICTORY**, 5 paid, revenue $63.90, average satisfaction 91.4.

### `demo/stage3-happy-hour.txt`: Happy Hour rescues the Critic's review

As `stage3-victory.txt`, except that the Host does not seat the Critic C3 on
arrival and Happy Hour is activated on turn 8 instead of turn 13.

| Turn | What happens to C3 |
| --- | --- |
| 7 | Arrives; the Host waits. Loses 12 at the end of the turn (88) |
| 8 | Happy Hour activated: 88 -> 100 (the +15 is capped at 100). Host seats C3; order taken: Pasta at 20% off, $14.40. End of turn: 88 |
| 9 | Second Happy Hour turn. C3 is seated, so no recovery. Chef finishes the Pasta. End of turn: 76 |
| 10 | Served at 76: glowing review, 76 -> 91 |

Without Happy Hour C3 would be served at 64 (88, 76, 64) and the review would
be harsh. After turn 8 the Happy Hour prompt is no longer shown.

Result: **VICTORY**, 5 paid, revenue $63.00 ($18.00 + $8.10 + $14.40 + $9.00
+ $13.50), average satisfaction 89.8 (84, 95, 91, 84, 95).

### `demo/stage2-victory.txt` on Stage 3: the VIP waits, the Critic is served first

The Host waits and Happy Hour is never activated, so only the Waiter seats.
The VIP loses only 5 per turn and the Critic 12, so the Waiter seats C2 but
does not take the order. The next order (the Salad, 1 unit) goes to the Critic,
who is served after two waiting turns.

| Customer | Type | Dish | Bill | Served (turn) | Satisfaction when paid |
| --- | --- | --- | --- | --- | --- |
| C1 | Regular | Pasta  | $18.00 | 4  | 76 |
| C2 | VIP     | Pasta  | $16.20 | 12 | 60 (eight waiting turns at 5) |
| C3 | Critic  | Salad  | $9.00  | 9  | 91 (served at 76: glowing review +15) |
| C4 | Regular | Salad  | $9.00  | 15 | 60 |
| C5 | VIP     | Burger | $13.50 (unpaid) | 18 | - |
| C6 | Critic  | -      | -      | -  | - |

Result: **VICTORY**, 4 paid, revenue $52.20, average satisfaction 71.8
(71.75).

### `demo/stage2-critic-harsh.txt` on Stage 3: harsh review

The Host waits and Happy Hour is never activated. The Waiter works strictly in
arrival order: C2 is served on turn 7 and the Critic is only seated on turn 8.
The Critic's order is the Pasta (2 units).

| Customer | Type | Dish | Bill | Served (turn) | Satisfaction when paid |
| --- | --- | --- | --- | --- | --- |
| C1 | Regular | Pasta  | $18.00 | 4  | 76 |
| C2 | VIP     | Salad  | $8.10  | 7  | 85 |
| C3 | Critic  | Pasta  | $18.00 | 11 | 37 (served at 52: harsh review -15) |
| C4 | Regular | Salad  | $9.00  | 13 | 76 |
| C5 | VIP     | Burger | $13.50 | 16 | 85 |
| C6 | Critic  | Burger & Pasta Combo | $29.70 (unpaid) | - | - |

Result: **VICTORY**, 5 paid, revenue $66.60, average satisfaction 71.8. One
more customer pays than in the Critic-first run and the averages are almost
the same (71.80 against 71.75), but here the Critic leaves at 37.

### `demo/stage1-defeat.txt` on Stage 3

Happy Hour is never activated and the Host, Waiter and Chef all wait every
turn. C1 leaves at the end of turn 13 (13 waiting turns at 8) and the Critic
C3 at the end of turn 15 (9 waiting turns at 12). The VIPs lose only 5 per
turn and are still queueing at the end. Result: **DEFEAT**, 0 paid, $0.00.

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
