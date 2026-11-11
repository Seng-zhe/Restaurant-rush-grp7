# Test notes

Every run below is reproducible: build, then feed the input file.

```bash
./run.sh demo/<file>.txt
```

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

