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
