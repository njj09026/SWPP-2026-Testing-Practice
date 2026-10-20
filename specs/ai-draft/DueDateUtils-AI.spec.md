# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Return the task-list due-date label using calendar days. Completion takes precedence over the due date.

**Pre-conditions**
- `today` is the calendar date to compare against; `dueDate` may be null.

**Post-conditions**
- Success cases
  - A completed task returns `CLEAR`, regardless of its due date. `[Unit]`
    - Tip: Given `dueDate = 2026-10-25`, `today = 2026-10-20`, and `isCompleted = true` / When `dueDateLabel` is called / Then it returns `CLEAR`.
  - An active task without a due date returns the empty string. `[Unit]`
    - Tip: Given `dueDate = null`, `today = 2026-10-20`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns `""`.
  - An active task due in the future returns `D-N`, where N is the number of calendar days until the due date. `[Unit]`
    - Tip: Given `dueDate = 2026-10-25`, `today = 2026-10-20`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns `D-5`.
  - An active task past due returns `D+N`, where N is the number of calendar days since the due date. `[Unit]`
    - Tip: Given `dueDate = 2026-10-15`, `today = 2026-10-20`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns `D+5`.
- Error cases
  - None.
- Edge cases
  - A completed task without a due date also returns `CLEAR`, at the nullable due-date boundary. `[Unit]`
    - Tip: Given `dueDate = null`, `today = 2026-10-20`, and `isCompleted = true` / When `dueDateLabel` is called / Then it returns `CLEAR`.
  - At one day before today, an active task is one day overdue. `[Unit]`
    - Tip: Given `dueDate = 2026-10-19`, `today = 2026-10-20`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns `D+1`.
  - At today, an active task is due today. `[Unit]`
    - Tip: Given `dueDate = 2026-10-20`, `today = 2026-10-20`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns `D-Day`.
  - At one day after today, an active task is due in one day. `[Unit]`
    - Tip: Given `dueDate = 2026-10-21`, `today = 2026-10-20`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns `D-1`.
  - The calendar-day count stays correct across a month end. `[Unit]`
    - Tip: Given `dueDate = 2026-11-02`, `today = 2026-10-30`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns `D-3`.
  - The calendar-day count stays correct across a year end. `[Unit]`
    - Tip: Given `dueDate = 2027-01-02`, `today = 2026-12-30`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns `D-3`.
  - Leap day counts as one calendar day. `[Unit]`
    - Tip: Given `dueDate = 2024-03-01`, `today = 2024-02-28`, and `isCompleted = false` / When `dueDateLabel` is called / Then it returns `D-2`.

**Undefined behavior** (optional)
- None.
